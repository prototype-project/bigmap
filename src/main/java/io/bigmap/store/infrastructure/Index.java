package io.bigmap.store.infrastructure;

import io.bigmap.store.domain.CriticalError;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

class Index {
    private final Map<String, Position> positions = new ConcurrentHashMap<>();
    private final PartitionsManager partitionsManager;
    private final InfrastructureMetrics infrastructureMetrics;

    Index(PartitionsManager partitionsManager, InfrastructureMetrics infrastructureMetrics) {
        try {
            loadPositions(partitionsManager.getPartitionPaths(), this.positions);
            this.partitionsManager = partitionsManager;
            this.infrastructureMetrics = infrastructureMetrics;
            infrastructureMetrics.meterMapSize(positions);
        } catch (IOException e) {
            throw new CriticalError();
        }
    }
    private void loadPositions(
            List<String> partitionFilePaths,
            Map<String, Position> positions) throws IOException {
        for (String path: partitionFilePaths) {
            loadPositions(path, positions);
        }
    }

    private void loadPositions(
            String partitionFilePath,
            Map<String, Position> positions) throws IOException {
        RandomAccessFile reader = new RandomAccessFile(partitionFilePath, "r");
        BufferedReader lineReader = new BufferedReader(new FileReader(partitionFilePath));
        int newLineCharLength = "\n".getBytes().length;

        int offset = 0;
        while (true) {
            String keyAndValueLength = lineReader.readLine();
            if (keyAndValueLength == null) {
                break;
            }

            reader.skipBytes(keyAndValueLength.getBytes().length + newLineCharLength);

            String[] keyAndValueLengthAsArray = keyAndValueLength.split(",");
            int keyLength = Integer.valueOf(keyAndValueLengthAsArray[0]);
            int valueLength = Integer.valueOf(keyAndValueLengthAsArray[1]);


            byte[] keyBytes = new byte[keyLength];
            reader.read(keyBytes, 0, keyLength);
            String key = new String(keyBytes, StandardCharsets.UTF_8);
            String value = "";

            if (valueLength == -1) { // tombstone
                positions.remove(key);
                valueLength = 0;
            } else {
                byte[] valueBytes = new byte[valueLength];
                reader.read(valueBytes, 0, valueLength);
                value = new String(valueBytes, StandardCharsets.UTF_8);

                Position position = new Position(
                        offset + keyAndValueLength.getBytes().length + newLineCharLength + keyLength,
                        valueLength,
                        partitionFilePath);
                positions.put(key, position);
            }

            offset += keyAndValueLength.getBytes().length + newLineCharLength + keyLength + valueLength;
            lineReader.skip(key.length() + value.length());
            reader.seek(offset);
        }
    }

    Optional<Position> get(String key) {
        return Optional.ofNullable(positions.get(key));
    }

    void update(String key, String value) {
        int offset = partitionsManager.getOffset();
        String positionPrefix = key.getBytes().length + "," + value.getBytes().length + "\n";
        int valueOffset = positionPrefix.getBytes().length + key.getBytes().length;

        positions.put(
                key,
                new Position(
                        offset + valueOffset,
                        value.getBytes().length,
                        partitionsManager.getCurrentPartitionFilePath()
                )
        );
        partitionsManager.update(valueOffset + value.getBytes().length);
    }

    void delete(String key) {
        String positionPrefix = key.getBytes().length + ",-1\n";
        positions.remove(key);
        partitionsManager.update(positionPrefix.getBytes().length + key.getBytes().length);
    }

    String getCurrentPartitionFilePath() {
        return partitionsManager.getCurrentPartitionFilePath();
    }

    Map<String, Position> getAllPositions() {
        return positions; // immutable?
    }

    void removePartitions(List<String> partitionFilePaths) {
        partitionsManager.removePartitions(partitionFilePaths);
    }

    List<String> getPartitionPaths() {
        return partitionsManager.getPartitionPaths();
    }
}
