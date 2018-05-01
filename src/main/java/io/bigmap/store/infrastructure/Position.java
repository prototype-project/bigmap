package io.bigmap.store.infrastructure;

class Position {
    private final int offset;
    private final int length;
    private final String partitionFilePath;

    Position(
            int offset,
            int length,
            String partitionFilePath) {
        this.offset = offset;
        this.length = length;
        this.partitionFilePath = partitionFilePath;
    }

    int getOffset() {
        return offset;
    }

    int getLength() {
        return length;
    }

    String getPartitionFilePath() {
        return partitionFilePath;
    }
}
