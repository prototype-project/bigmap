package io.bigmap.router;

import java.util.List;

class CachedRouterSetupRepository implements RouterSetupRepository {
    private final RouterSetupRepository delegate;
    private RouterSetup cachedSetup;

    CachedRouterSetupRepository(RouterSetupRepository delegate) {
        this.delegate = delegate;
        refresh();
    }

    @Override
    public RouterSetup get() {
        return cachedSetup;
    }

    @Override
    public void update(List<MasterMeta> masters) {
        delegate.update(masters);
        refresh();
    }

    private void refresh() {
        cachedSetup = delegate.get();
    }
}
