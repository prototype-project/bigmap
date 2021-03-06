package io.bigmap.router;

interface Router {
    void routePut(String key, String value);

    String routeGet(String key);

    void routeDelete(String key);
}
