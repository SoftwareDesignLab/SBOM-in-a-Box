package org.svip.sbomvex.http;

import java.net.HttpURLConnection;

public abstract class HTTPClient {
    public HTTPResponse queryURL(String url){
        return null;
    }

    public HTTPResponse queryURL(HTTPConnectionBuilder httpConnectionBuilder){
        return null;
    }

    private HTTPResponse openConnection(HttpURLConnection httpURLConnection){
        return null;
    }
}
