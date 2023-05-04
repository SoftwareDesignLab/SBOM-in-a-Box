package org.svip.sbomvex.database.mockNetworking;

import java.net.http.HttpResponse;
import java.util.ArrayList;


/**
 * This class stores the expected inputs and the output of a HTTP request. It allows for the testing of network code
 * without requiring the network or NVIP to be running.
 */
public class mockHTTPConversation{
    public String resource;
    public String username;
    public String token;
    public ArrayList<String> params;
    public HttpResponse<String> response;

    public mockHTTPConversation(String resource, String username, String token, ArrayList<String> params, HttpResponse<String> response) {
        if(resource == null) {
            resource = "";
        }
        if(username == null) {
            username = "";
        }
        if(token == null){
            token = "";
        }
        if(params == null) {
            params = new ArrayList<>();
        }

        this.resource = resource;
        this.username = username;
        this.token = token;
        this.params = params;
        this.response = response;
    }
}
