package io.bigmap.router;

public class MasterMeta {
    private final String url;

    MasterMeta(String url) {
        this.url = url;
    }

    public String getConfigUrl() {
        return url + "/map/admin/config";
    }
}
