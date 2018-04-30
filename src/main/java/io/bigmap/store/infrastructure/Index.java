package io.bigmap.store.infrastructure;

import io.bigmap.store.CriticalError;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Index {
    private final Map<String, Position> positions;
    private int offset;

    public Index(String path) {
        try {
            positions = loadPositionsAndSetOffset(path);
        } catch (IOException e) {
            throw new CriticalError();
        }
    }

    private Map<String, Position> loadPositionsAndSetOffset(String path) throws IOException {
        Map<String, Position> result = new ConcurrentHashMap<>();

        RandomAccessFile reader = new RandomAccessFile(path, "r");
        BufferedReader lineReader = new BufferedReader(new FileReader(path));
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

            byte[] valueBytes = new byte[valueLength];
            reader.read(valueBytes, 0, valueLength);
            String value = new String(valueBytes, StandardCharsets.UTF_8);

            Position position = new Position(
                    offset + keyAndValueLength.getBytes().length + newLineCharLength + keyLength,
                    valueLength);
            result.put(key, position);

            offset += keyAndValueLength.getBytes().length + newLineCharLength + keyLength + valueLength;
            lineReader.skip(key.length() + value.length());
            reader.seek(offset);
        }

        this.offset = offset;

        return result;
    }

    Optional<Position> get(String key) {
        return Optional.ofNullable(positions.get(key));
    }

    void update(String key, String value) {
        String positionPrefix = key.getBytes().length + "," + value.getBytes().length + "\n";
        int valueOffset = positionPrefix.getBytes().length + key.getBytes().length;
        positions.put(key, new Position(offset + valueOffset, value.getBytes().length));
        offset += valueOffset + value.getBytes().length;
    }
}
