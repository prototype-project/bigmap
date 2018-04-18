package io.bigmap.store.map;

public class ValuePosition {
    private final int offset;
    private final int length;

    public ValuePosition(int offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    int getOffset() {
        return offset;
    }

    int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "ValuePosition{" +
                "offset=" + offset +
                ", length=" + length +
                '}';
    }
}
