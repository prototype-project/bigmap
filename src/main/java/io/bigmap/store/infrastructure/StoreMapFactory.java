package io.bigmap.store.infrastructure;

import io.bigmap.store.StoreMap;

public class StoreMapFactory {
    private final String partitionsDirPath;
    private final int partitionSizeThresholdBytes;

    public StoreMapFactory(
            String partitionsDirPath,
            int partitionSizeThresholdBytes) {
        this.partitionsDirPath = partitionsDirPath;
        this.partitionSizeThresholdBytes = partitionSizeThresholdBytes;
    }

    public StoreMap create() {
        return new FileMap(
                new Index(new PartitionsManager(partitionsDirPath, partitionSizeThresholdBytes)),
                this);
    }
}
