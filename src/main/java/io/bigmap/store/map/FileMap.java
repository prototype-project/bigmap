package io.bigmap.store.map;

import com.google.common.base.Preconditions;
import io.bigmap.store.map.infrastructure.ValuePosition;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class FileMap {
    private final Index index;
    private final String filePath;

    public FileMap(String filePath, Index index) {
        Preconditions.checkNotNull(filePath);
        Preconditions.checkNotNull(index);

        this.filePath = filePath;
        this.index = index;
    }

    public String get(Key key) throws IOException {
        return getValueFromPosition(index.position(key));
    }

    public String getHead(String id) throws IOException {
        return getValueFromPosition(index.headPosition(id));
    }

    private String getValueFromPosition(ValuePosition position) throws IOException {
        RandomAccessFile reader = new RandomAccessFile(filePath, "r");
        reader.seek(position.getOffset());
        byte[] result = new byte[position.getLength()];
        reader.read(result, 0, position.getLength());
        reader.close();
        return new String(result, StandardCharsets.UTF_8);
    }

    synchronized public void add(Key key, String value) throws IOException, KeyDuplicationException {
        if (index.contains(key)) {
            throw new KeyDuplicationException();
        }
        RandomAccessFile writer = new RandomAccessFile(filePath, "rw");
        writer.seek(index.tail());
        writer.writeBytes(value);
        writer.close();
        index.extend(key, value.getBytes().length);
    }
}
