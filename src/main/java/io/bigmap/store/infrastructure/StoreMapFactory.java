package io.bigmap.store.infrastructure;

class StoreMapFactory {
    private final String partitionsDirPath;
    private final int partitionSizeThresholdBytes;
    private final int numberOfPutsThreshold;
    private final InfrastructureMetrics infrastructureMetrics;

    StoreMapFactory(
            String partitionsDirPath,
            int partitionSizeThresholdBytes,
            int numberOfPutsThreshold,
            InfrastructureMetrics infrastructureMetrics) {
        this.partitionsDirPath = partitionsDirPath;
        this.partitionSizeThresholdBytes = partitionSizeThresholdBytes;
        this.numberOfPutsThreshold = numberOfPutsThreshold;
        this.infrastructureMetrics = infrastructureMetrics;
    }

    FileMap create() {
        return new FileMap(
                new Index(new PartitionsManager(partitionsDirPath, partitionSizeThresholdBytes), infrastructureMetrics),
                numberOfPutsThreshold,
                infrastructureMetrics);
    }
}