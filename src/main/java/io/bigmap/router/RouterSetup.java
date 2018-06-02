package io.bigmap.router;

import com.google.common.collect.ImmutableList;

import java.util.List;

class RouterSetup {

    private final List<MasterSetup> masters;

    RouterSetup(List<MasterSetup> masters) {
        this.masters = ImmutableList.copyOf(masters);
    }

    List<MasterSetup> getMasters() {
        return masters;
    }

    static class MasterSetup {
        private final String masterAddress;
        private final List<ReplicaMeta> replicaMetaList;

        MasterSetup(
                List<ReplicaMeta> replicaMetaList,
                String masterAddress) {
            this.replicaMetaList = ImmutableList.copyOf(replicaMetaList);
            this.masterAddress = masterAddress;
        }

        List<ReplicaMeta> getReplicaMetaList() {
            return replicaMetaList;
        }

        String getMasterAddress() {
            return masterAddress;
        }

        String getPutUrl(String key) {
            return getMasterAddress() + "/map/" + key;
        }
    }
}
