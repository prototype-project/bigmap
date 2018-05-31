package io.bigmap.store.infrastructure;

import io.bigmap.common.CriticalError;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class PartitionsManager {
    private final String partitionsDirPath;
    private final int partitionSizeThreshold;

    private int currentPartitionNumber;
    private int currentPartitionSizeBytes;

    PartitionsManager(
            String partitionsDirPath,
            int partitionSizeThresholdBytes) {
        new File(partitionsDirPath).mkdirs();
        this.partitionsDirPath = partitionsDirPath;
        this.partitionSizeThreshold = partitionSizeThresholdBytes;
        this.currentPartitionNumber = getCurrentPartitionNumber(partitionsDirPath) + 1;
        this.currentPartitionSizeBytes = 0;
    }

    private int getCurrentPartitionNumber(
            String partitionsDirPath) {
        return getPartitionNumbers(partitionsDirPath).stream()
                .max(Integer::compareTo)
                .orElse(0);
    }

    private List<Integer> getPartitionNumbers(String partitionsDirPath) {
        File dir = new File(partitionsDirPath);
        File[] directoryListing = dir.listFiles();
        List<Integer> result = new ArrayList<>();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                result.add(Integer.valueOf(child.getName()));
            }
        } else {
            throw new CriticalError();
        }
        return result;
    }

    void update(long newSegmentSizeBytes) {
        if (currentPartitionSizeBytes + newSegmentSizeBytes >= partitionSizeThreshold) {
            currentPartitionSizeBytes = 0;
            currentPartitionNumber++;
        } else {
            currentPartitionSizeBytes += newSegmentSizeBytes;
        }
    }

    List<String> getPartitionPaths() {
        return getPartitionNumbers(partitionsDirPath).stream()
                .map(p -> partitionsDirPath + "/" + p)
                .collect(Collectors.toList());
    }

    int getOffset() {
        return currentPartitionSizeBytes;
    }

    String getCurrentPartitionFilePath() {
        String path = partitionsDirPath + "/" + currentPartitionNumber;

        if (!Files.exists(Paths.get(path))) {
            try {
                Files.createFile(Paths.get(path));
            } catch (IOException e) {
                throw new CriticalError();
            }
        }
        return path;
    }

    void removePartitions(List<String> partitionFilePaths) {
        try {
            for (String path: partitionFilePaths) {
                Files.delete(Paths.get(path));
            }
        } catch (IOException e) {
            throw new CriticalError();
        }

    }
}
