package io.bigmap.store.map;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class FileMap {
    private final Index index;
    private final String filePath;

    public FileMap(String filePath, Index index) {
        Preconditions.checkNotNull(filePath);
        Preconditions.checkNotNull(index);

        this.filePath = filePath;
        this.index = index;
    }

    public Optional<String> get(Key key) {
        return index.position(key).map(this::getValueFromPosition);
    }

    public Optional<String> getHead(String id) {
        return index.headPosition(id).map(this::getValueFromPosition);
    }

    synchronized private String getValueFromPosition(ValuePosition position) {
        try {
            RandomAccessFile reader = new RandomAccessFile(filePath, "r");
            reader.seek(position.getOffset());
            byte[] result = new byte[position.getLength()];
            reader.read(result, 0, position.getLength());
            reader.close();
            return new String(result, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new CriticalError("IOException caught in getValueFromPosition for: " + position.toString());
        }
    }

    synchronized public void delete(String id) {
        if (!index.containsHead(id)) {
            throw new KeyNotFoundException();
        }
        index.delete(id);
    }

    synchronized public void add(Key key, String value) throws KeyDuplicationException {
        if (index.contains(key)) {
            throw new KeyDuplicationException();
        }
        try {
            RandomAccessFile writer = new RandomAccessFile(filePath, "rw");
            writer.seek(index.tail());
            writer.writeBytes(value);
            writer.close();
        } catch (IOException e) {
            throw new CriticalError(
                    "IOException caught in add for key:\n " + key.toString() + "\nand value:\n" + value);

        }
        index.extend(key, value.getBytes().length);
    }
}
