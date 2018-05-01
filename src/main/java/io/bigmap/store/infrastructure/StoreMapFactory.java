package io.bigmap.store.infrastructure;

public class StoreMapFactory {
    private final String partitionsDirPath;
    private final int partitionSizeThresholdBytes;

    public StoreMapFactory(
            String partitionsDirPath,
            int partitionSizeThresholdBytes) {
        this.partitionsDirPath = partitionsDirPath;
        this.partitionSizeThresholdBytes = partitionSizeThresholdBytes;
    }

    public FileMap create() {
        return new FileMap(new Index(new PartitionsManager(partitionsDirPath, partitionSizeThresholdBytes)));
    }
}