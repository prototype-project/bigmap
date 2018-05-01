package io.bigmap.store.infrastructure;

import io.bigmap.store.CriticalError;
import io.bigmap.store.StoreMap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class FileMap implements StoreMap {

    private final Index index;

    FileMap(Index index) {
        this.index = index;
    }

    @Override
    public Optional<String> get(String key)
            throws CriticalError {
        Optional<Position> positionOptional = index.get(key);
        if (positionOptional.isPresent()) {
            Position position = positionOptional.get();
            try {
                RandomAccessFile reader = new RandomAccessFile(position.getPartitionFilePath(), "r");
                reader.seek(position.getOffset());
                byte[] result = new byte[position.getLength()];
                reader.read(result, 0, position.getLength());
                reader.close();
                return Optional.of(new String(result, StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new CriticalError();
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    synchronized public void put(String key, String value)
            throws CriticalError {
        String segment = key.getBytes().length + "," + value.getBytes().length + "\n" + key + value;
        String path = index.getCurrentPartitionFilePath();
        try {
            if (!Files.exists(Paths.get(path))) {
                Files.createFile(Paths.get(path));
            }
            Files.write(Paths.get(path), segment.getBytes(), StandardOpenOption.APPEND);
            index.update(key, value);
        } catch (IOException e) {
            throw new CriticalError();
        }
    }

    synchronized void cleanup() {
        Map<String, Position> positions = index.getAllPositions();
        String lastPartitionFilePath = index.getCurrentPartitionFilePath();
        Set<String> toDelete = new HashSet<>(index.getPartitionPaths());
        toDelete.remove(lastPartitionFilePath);
        positions.forEach((k, v) -> put(k, get(k).orElseThrow(CriticalError::new)));
        index.removePartitions(new ArrayList<>(toDelete));
    }
}
