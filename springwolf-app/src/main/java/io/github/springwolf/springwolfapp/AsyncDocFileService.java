package io.github.springwolf.springwolfapp;

import java.util.Map;

public interface AsyncDocFileService {

    Map<String, Map<String, Object>> getAsyncApiFiles();

    /**
     * Return a mapping of protocol to url for each server listed in the provided AsyncApi docs.
     */
    Map<String, String> getServers();

}
