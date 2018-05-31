package io.bigmap.router;

import java.util.List;

public interface RouterSetupRepository {
    RouterSetup get();

    void update(List<MasterMeta> masters);
}
