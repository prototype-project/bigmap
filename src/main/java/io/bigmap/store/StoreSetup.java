package io.bigmap.store;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class StoreSetup {
    private Role role;
    private List<String> replicas;

    public StoreSetup() {
        this.role = Role.MASTER;
        this.replicas = ImmutableList.copyOf(new ArrayList<>());
    }

    public void setAsMaster(List<String> replicas) {
        this.role = Role.MASTER;
        this.replicas = ImmutableList.copyOf(replicas);
    }

    public void setAsReplica() {
        this.role = Role.REPLICA;
        this.replicas = ImmutableList.copyOf(new ArrayList<>());
    }

    public List<String> getReplicas() {
        return replicas;
    }

    public Role getRole() {
        return role;
    }
}
