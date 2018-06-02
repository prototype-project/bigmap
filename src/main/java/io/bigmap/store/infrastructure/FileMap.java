package io.bigmap.store.infrastructure;

import io.bigmap.common.CriticalError;
import io.bigmap.store.domain.StoreMap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class FileMap implements StoreMap {

    private final Index index;
    private int putCounter = 0;
    private final int numberOfPutsThreshold;
    private final InfrastructureMetrics infrastructureMetrics;
    private final AtomicLong mapSizeBytes;

    FileMap(
            Index index,
            int numberOfPutsThreshold,
            InfrastructureMetrics infrastructureMetrics) {
        this.index = index;
        this.numberOfPutsThreshold = numberOfPutsThreshold;
        this.infrastructureMetrics = infrastructureMetrics;
        this.mapSizeBytes = new AtomicLong(0);
    }

    @Override
    public Optional<String> get(String key) throws CriticalError {
        return index.get(key)
                .map(position -> {
                    try {
                        RandomAccessFile reader = new RandomAccessFile(position.getPartitionFilePath(), "r");
                        reader.seek(position.getOffset());
                        byte[] result = new byte[position.getLength()];
                        reader.read(result, 0, position.getLength());
                        reader.close();
                        return new String(result, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        throw new CriticalError("Critical error in FileMap.get", e);
                    }
                });
    }

    @Override
    synchronized public void put(String key, String value) throws CriticalError {
        String segment = key.getBytes().length + "," + value.getBytes().length + "\n" + key + value;
        String path = index.getCurrentPartitionFilePath();
        try {
            Files.write(Paths.get(path), segment.getBytes(), StandardOpenOption.APPEND);
            index.update(key, value);
            putCounter++;
        } catch (IOException e) {
            throw new CriticalError("Critical error in FileMap.put", e);
        }
    }

    @Override
    synchronized public void delete(String key) throws CriticalError {
        String tombstone = key.getBytes().length + ",-1\n" + key;
        String path = index.getCurrentPartitionFilePath();
        try {
            Files.write(Paths.get(path), tombstone.getBytes(), StandardOpenOption.APPEND);
            index.delete(key);
            putCounter++;
        } catch (IOException e) {
            throw new CriticalError("Critical error in FileMap.delete", e);
        }
    }

    synchronized void cleanup() {
        if (putCounter >= numberOfPutsThreshold) {
            infrastructureMetrics.cleanupTimer().record(() -> {
                long currentMapSizeBytes = 0;
                Map<String, Position> positions = index.getAllPositions();
                String lastPartitionFilePath = index.getCurrentPartitionFilePath();
                Set<String> toDelete = new HashSet<>(index.getPartitionPaths());
                toDelete.remove(lastPartitionFilePath);
                for (String k: positions.keySet()) {
                    String value = get(k).orElseThrow(CriticalError::new);
                    put(k, value);
                    currentMapSizeBytes += k.getBytes().length + value.getBytes().length;
                }
                index.removePartitions(new ArrayList<>(toDelete));
                mapSizeBytes.set(currentMapSizeBytes);
            });
        }
    }
}
