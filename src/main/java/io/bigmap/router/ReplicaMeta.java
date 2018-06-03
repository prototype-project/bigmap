package io.bigmap.router;

class ReplicaMeta {
    private final String url;

    ReplicaMeta(String url) {
        this.url = url;
    }

    String getUrl() {
        return url;
    }

    String getKeyUrl(String key) {
        return getUrl() + "/map/" + key;
    }
}
