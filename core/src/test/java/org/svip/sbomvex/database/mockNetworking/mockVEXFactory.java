package org.svip.sbomvex.database.mockNetworking;

import org.svip.sbomvex.VEXFactory;

import java.net.http.HttpResponse;
import java.util.ArrayList;

/**
 * This class overrides the VEXFactory class to allow for testing of functions which use doHTTPRequest without using the network
 * It takes a list of mockHTTPConversations which are used to determine what response to return for a given set of inputs.
 */
public class mockVEXFactory extends VEXFactory {
    /**
     * Stores a list of conversations this factory knows how to have.
     */
    private final ArrayList<mockHTTPConversation> knownConversations;

    public mockVEXFactory(ArrayList<mockHTTPConversation> knownConversations) {
        super();
        this.knownConversations = knownConversations;
    }

    public mockVEXFactory(String endpoint, String username, String password, ArrayList<mockHTTPConversation> knownConversations) {
        super(); //can't call super(endpoint, username, password) because it calls the real doHTTPRequest
        super.setUsername(username);
        super.setToken("51f6d822203a36299351f427ca0dc19837f8fca9c3fc8b661844ad047efe5a4ef333d9f91ac5372a42f12fd4281ec0ed61ab0c72ca5f1196a735c742216bb281");
        this.knownConversations = knownConversations;
    }

    /**
     * Overrides doHTTPRequest and returns a response if the inputs match a known conversation.
     *
     * @param resource The resource to request
     * @param username The username to use for authentication
     * @param token The token to use for authentication
     * @param params The parameters to send with the request
     *
     * @return a HTTP response if one is found in the knownConversations. If not 404.
     */
    @Override
    protected HttpResponse<String> doHttpRequest(String resource, String username, String token, ArrayList<String> params) {
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

        for(mockHTTPConversation conversation : knownConversations) {
            //high-tech debugging technology™
            //System.out.println("resource: " + conversation.resource.equals(resource) + ", username: " + conversation.username.equals(username) + ", token: " + conversation.token.equals(token) + ", params: " + conversation.params.equals(params)); System.out.println(conversation.params + " vs " + params);
            if(conversation.resource.equals(resource) && conversation.username.equals(username) && conversation.token.equals(token) && conversation.params.equals(params)) {
                return conversation.response;
            }
        }

        //System.out.println("No mock conversation found for inputs:" + resource + ", " + username + ", " + token + ", " + params);

        return new mockHttpResponse(404, "Not Found");
    }
}
