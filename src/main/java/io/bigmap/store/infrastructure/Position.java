package io.bigmap.store.infrastructure;

class Position {
    private final int offset;
    private final int length;

    Position(int offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    int getOffset() {
        return offset;
    }

    int getLength() {
        return length;
    }
}
