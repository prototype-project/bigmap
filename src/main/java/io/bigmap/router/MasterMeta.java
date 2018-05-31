package io.bigmap.router;

class MasterMeta {
    private final String url;

    MasterMeta(String url) {
        this.url = url;
    }

    String getConfigUrl() {
        return url + "/map/admin/config";
    }
}
