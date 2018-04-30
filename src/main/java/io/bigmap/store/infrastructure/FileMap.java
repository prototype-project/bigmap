package io.bigmap.store.infrastructure;

import io.bigmap.store.CriticalError;
import io.bigmap.store.StoreMap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

public class FileMap implements StoreMap {
    // TODO refactor
    public static final String STANDARD_PATH = "/tmp/bigmap";
    private final Index index;
    private final String path;

    public FileMap(Index index, String path) {
        this.index = index;
        this.path = path;
    }

    @Override
    public Optional<String> get(String key) throws CriticalError {
        Optional<Position> positionOptional = index.get(key);
        if (positionOptional.isPresent()) {
            Position position = positionOptional.get();
            try {
                RandomAccessFile reader = new RandomAccessFile(path, "r");
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
    public void put(String key, String value) throws CriticalError {
        String segment = key.getBytes().length + "," + value.getBytes().length + "\n" + key + value;
        try {
            Files.write(Paths.get(path), segment.getBytes(), StandardOpenOption.APPEND);
            index.update(key, value);
        } catch (IOException e) {
            throw new CriticalError();
        }
    }
}
