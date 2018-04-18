package io.bigmap.store.map.infrastructure;

public class ValuePosition {
    private final int offset;
    private final int length;

    ValuePosition(int offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }
}
