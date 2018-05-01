package io.bigmap.store.infrastructure;

public class StoreMapFactory {
    private final String partitionsDirPath;
    private final int partitionSizeThresholdBytes;
    private final int numberOfPutsThreshold;

    public StoreMapFactory(
            String partitionsDirPath,
            int partitionSizeThresholdBytes,
            int numberOfPutsThreshold) {
        this.partitionsDirPath = partitionsDirPath;
        this.partitionSizeThresholdBytes = partitionSizeThresholdBytes;
        this.numberOfPutsThreshold = numberOfPutsThreshold;
    }

    public FileMap create() {
        return new FileMap(
                new Index(new PartitionsManager(partitionsDirPath, partitionSizeThresholdBytes)),
                numberOfPutsThreshold);
    }
}