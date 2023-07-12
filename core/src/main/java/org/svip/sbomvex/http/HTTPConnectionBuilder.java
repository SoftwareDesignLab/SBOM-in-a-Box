package org.svip.sbomvex.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * file: HTTPConnectionBuilder.java
 * Class to help build HTTP connections
 *
 * @author Matthew Morrison
 */
public class HTTPConnectionBuilder {

    private String endpoint;

    private Map<String, String> params;

    private Map<String, String> requestBody;

    private Integer timeout = 1000;

    private RequestMethod requestMethod = RequestMethod.GET;

    public HTTPConnectionBuilder(String endpoint){
        this.endpoint = endpoint;
    }

    public HTTPConnectionBuilder updateParam(String key, String value){
        this.params.put(key, value);
        return this;
    }

    public HTTPConnectionBuilder updateRequestBody(String key, String value){
        this.requestBody.put(key, value);
        return this;
    }

    public HTTPConnectionBuilder setTimeout(Integer timeout){
        this.timeout = timeout;
        return this;
    }

    public HTTPConnectionBuilder setRequestMethod(RequestMethod requestMethod){
        this.requestMethod = requestMethod;
        return this;
    }

    //TODO implement
    private String addParams(){
        return null;
    }

    //TODO implement
    public HttpURLConnection buildConnection(){
        return null;
    }
}
