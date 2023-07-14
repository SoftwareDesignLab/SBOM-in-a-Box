package org.svip.sbomvex.database;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * file: OSVClient.java
 * Client class for OSV database
 *
 * @author Matthew Morrison
 */
public class OSVClient {

    /**url endpoint to access OSV database*/
    private final String ENDPOINT = "\"https://api.osv.dev/v1/query\"";


    private String buildCommandWithParams(String... params) {
        String CURL_COMMAND = "curl -d ";


        return CURL_COMMAND + Arrays.toString(params) + ENDPOINT;
    }

    private CompletableFuture<String> getRequestAsync(String... params) {
        String command = buildCommandWithParams(params);

        try{
            // TODO how to implement?
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(command);

            BufferedReader input = new BufferedReader(new InputStreamReader(
                    pr.getInputStream()));
            return null;
        } catch(Exception e){
            return null;
        }

    }

    public CompletableFuture<String> getOSVByNameVersion(String componentName, String componentVersion) {
        String commandString = "\"{\\\"package\\\": {\\\"name\\\": \\\"" +
                componentName + "\\\"}, \\\"version\\\": " +
                componentVersion + "\\\"}\" ";
        return getRequestAsync(commandString);
    }

    public CompletableFuture<String> getOSVByNameVersionEcosystem(
            String componentName, String componentVersion, String ecosystem) {
        String commandString = "\"{\\\"package\\\": {\\\"name\\\": \\\"" +
                componentName + "\\\", \\\"ecosystem\\\": " +   "\\\"" +
                ecosystem + "\\\"}, \\\"version\\\": " +
                componentVersion + "\\\"}\" ";
        return getRequestAsync(commandString);
    }

    public CompletableFuture<String> getOSVByPURL(String purlString) {
        String commandString = "\"{\\\"package\\\": {\\\"purl\\\": \\\"" +
                purlString + "\\\"}}\" ";
        return getRequestAsync(commandString);
    }

    public CompletableFuture<String> getOSVByCommitAsync(String commitString) {
        String commandString = "\"{\\\"commit\\\": " +
                commitString + "\\\"}\" ";
        return getRequestAsync(commandString);
    }



    //TODO implement
    private void run(){
    }
}
