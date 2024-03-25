/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
 */

package org.svip.vex.http;

import java.net.HttpURLConnection;
import java.util.Map;

/**
 * file: HTTPConnectionBuilder.java
 * Class to help build HTTP connections
 *
 * @author Matthew Morrison
 */
public class HTTPConnectionBuilder {

    private final String endpoint;

    private Map<String, String> params;

    private Map<String, String> requestBody;

    private Integer timeout = 1000;

    private RequestMethod requestMethod = RequestMethod.GET;

    public HTTPConnectionBuilder(String endpoint) {
        this.endpoint = endpoint;
    }

    public HTTPConnectionBuilder updateParam(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    public HTTPConnectionBuilder updateRequestBody(String key, String value) {
        this.requestBody.put(key, value);
        return this;
    }

    public HTTPConnectionBuilder setTimeout(Integer timeout) {
        this.timeout = timeout;
        return this;
    }

    public HTTPConnectionBuilder setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    //TODO implement
    private String addParams() {
        return null;
    }

    //TODO implement
    public HttpURLConnection buildConnection() {
        return null;
    }
}
