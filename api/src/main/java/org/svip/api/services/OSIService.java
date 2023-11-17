package org.svip.api.services;

import org.springframework.stereotype.Service;
import org.svip.generation.osi.exceptions.DockerNotAvailableException;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.*;
import java.util.zip.ZipInputStream;

@Service
public class OSIService {

    private static class OSIURLBuilder{

        public enum RequestMethod {

            GET("GET"),
            POST("POST");

            private final String value;
            RequestMethod(String requestMethodStr) {
                this.value = requestMethodStr;
            }

            @Override
            public String toString() {
                return this.value;
            }
        }

        public enum RequestEndpoint {

            TOOLS("tools"),
            GENERATE("generate");

            private final String value;
            RequestEndpoint(String requestEndpoint) {
                this.value = requestEndpoint;
            }

            @Override
            public String toString() {
                return this.value;
            }

        }

        private final String rootEndpoint = new File("/.dockerenv").exists()
                // If running in container, access OSI by container name due to Docker's default network
                ? "http://osi:5000/"
                // If running outside of container, access OSI by the container's port on localhost
                : "http://localhost:50001/"; // TODO Move port to config file

        private final OSIURLBuilder.RequestEndpoint requestEndpoint;
        private final OSIURLBuilder.RequestMethod requestMethod;

        private final HashMap<String, String> requestParams = new HashMap<>();


        public OSIURLBuilder(OSIURLBuilder.RequestEndpoint requestEndpoint, OSIURLBuilder.RequestMethod requestMethod){
            this.requestEndpoint = requestEndpoint;
            this.requestMethod = requestMethod;
        }

        public OSIURLBuilder addParam(String param, String value){
            this.requestParams.put(param, value);
            return this;
        }

        public HttpURLConnection buildConnection() throws IOException {
            StringBuilder url = new StringBuilder(this.rootEndpoint + this.requestEndpoint);

            int paramCount = 0;
            for(String param : this.requestParams.keySet()) {
                url.append(paramCount++ == 0 ? "?" : "&")
                        .append(param)
                        .append("=")
                        .append(this.requestParams.get(param));
            }


            HttpURLConnection conn = (HttpURLConnection) new java.net.URL(url.toString()).openConnection();
            conn.setRequestMethod(this.requestMethod.value);

            if(this.requestMethod == OSIURLBuilder.RequestMethod.POST)
                conn.setDoOutput(true);

            return conn;


        }


    }

    private boolean enabled = false;


    public OSIService(){

        try{
            this.enabled = isOSIContainerAvailable();
        } catch (Exception ignored){

        }

    }


    public List<String> getTools(String listTypeArg) {
        return new ArrayList<>();
    }


    public void addProject(ZipInputStream inputStream){

    }

    public Map<String, String> generateSBOMs(List<String> toolNames){
        return new HashMap<>();
    }

    public boolean isEnabled(){
        return this.enabled;
    }


    /**
     * Function to check if the Docker API is running.
     *
     * @return True if the Docker API is running and can accept connections.
     *         False if the Docker API returned an error when pinging.
     * @throws DockerNotAvailableException If the container is not accessible/running at all.
     */
    private boolean isOSIContainerAvailable() throws DockerNotAvailableException {
        try {

            HttpURLConnection conn =
                    new OSIURLBuilder(OSIURLBuilder.RequestEndpoint.TOOLS, OSIURLBuilder.RequestMethod.GET).buildConnection();

            conn.connect();
            if (conn.getResponseCode() != 200)
                return false;

            conn.disconnect();
        } catch (IOException e) {
            throw new DockerNotAvailableException(Arrays.toString(e.getStackTrace()));
        }

        return true;
    }


}
