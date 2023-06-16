package org.svip.sbomanalysis.qualityattributes.tests;

import org.svip.sbom.model.Component;
import org.svip.sbomanalysis.qualityattributes.tests.testresults.Test;
import org.svip.sbomanalysis.qualityattributes.tests.testresults.TestResults;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * File: ActionableTest.java
 * Test fields to ensure data contained is usable. (not placeholder, not null, not invalid, etc.)
 *
 * @author Asa Horn
 */
public abstract class ActionableTest extends MetricTest{
    /**
     * URL to lookup CPEs. Should be the whole URL which just needs the search term appended to the end.
     * Should return 404 for a non-existant CPE, and 200 for a existing CPE.
     */
    private final String CPE_LOOKUP_URL;

    /**
     * Flag which keeps track of weather the overall test should return undefined behavior or not.
     */
    private boolean undefinedBehavior;

    /**
     * Flag which keeps track of weather the overall test should return failed or not.
     */
    private boolean failed;

    /**
     * Constructor for ActionableTest
     */
    public ActionableTest() {
        super("Actionable Test");

        //one result per page because we just care about the response code (404 vs 200)
        CPE_LOOKUP_URL = "https://services.nvd.nist.gov/rest/json/cpes/2.0?resultsPerPage=1&cpeMatchString=";
        //PURL_LOOKUP_URL = "https://purl.org/"; //todo implement purl lookup
        //SWID_LOOKUP_URL = "https://swidtag.org/"; //todo implement swid lookup

        undefinedBehavior = false;
        failed = false;
    }

    /**
     * Runs the tests for Actionable.
     * @param c - component to test.
     * @return - TestResults object containing the results of the test.
     */
    @Override
    public TestResults test(Component c) {
        // Init StringBuilder
        TestResults testResults = new TestResults(c);


        // Preform subtests
        testResults.addTest(testUniqueIdentifiers(c));

        // Return result
        return testResults;
    }

    /**
     * Tests the unique identifiers of a component to ensure they are actionable.
     * @param c - component to test.
     * @return - Test object containing the results of the test.
     */
    private Test testUniqueIdentifiers(Component c) {
        /*
         * the identifier objects have multiple identifiers in them. If one fails the lookup, then the entire test fails.
         * If there are no fails, and at least one undefined, then the test is undefined.
         * If all of them pass, then the test passes.
         */

        ArrayList<String> messages = new ArrayList<>();
        if (tryURL(CPE_LOOKUP_URL) == 200) {
            for (String id : c.getCpes()) {
                //go to the URL and see if it returns a 200 or 404, then get the human message for that status code.
                //the flags for the ultimate test result are updated by the getMessage method.
                messages.add(
                        String.format(
                                getMessage(tryURL(CPE_LOOKUP_URL + id))
                                , id) //add the ID to the message using string format
                );
            }
        } else {
            // the NIST API is not working, we can't do this test.
            messages.add("The CPE lookup service is currently unavailable. Please try again later.");
            undefinedBehavior = true;
        }

        //todo add SWID and PURL tests

        //sort messages by type (Fails, then Undefines, then Passes), then alphabetically.
        messages.sort((o1, o2) -> {
            if (o1.startsWith("FAIL")) {
               if(o2.startsWith("FAIL")){
                    return o1.compareTo(o2);
                } else {
                    return -1;
                }
            } else if (o1.startsWith("UNDEFINED")) {
                if (o2.startsWith("UNDEFINED")) {
                    return o1.compareTo(o2);
                } else if (o2.startsWith("FAIL")) {
                    return 1;
                } else {
                    return -1;
                }
            } else if (o1.startsWith("PASS")) {
                if (o2.startsWith("PASS")) {
                    return o1.compareTo(o2);
                } else {
                    return 1;
                }
            } else {
                System.err.println("Warn: ActionableTest.java/testUniqueIdentifiers/sort. Unknown message start: " + o1);
                return 0;
            }
        });

        //then make a string with every message
        StringBuilder messageString = new StringBuilder();
        for(String message : messages){
            messageString.append('\t').append(message).append('\n');
        }

        //finally return the test result with the messages string.
        if (failed) {
            messageString.insert(0, "The test failed because one or more of the identifiers was not found in the databases. See checks below for more details.\n");
            return new Test(false, messageString.toString());
        } else if (undefinedBehavior) {
            messageString.insert(0, "The test was inconclusive for at least one of the identifiers. See checks below for more details.\n");
            return new Test(false, messageString.toString());
        } else {
            messageString.insert(0, "The test passed because all identifiers were located in the databases.\n");
            return new Test(true, messageString.toString());
        }
    }

    /**
     * Attempts to connect to the given URL and returns the HTTP status code.
     * @param url - Full URL to connect to
     * @return - int HTTP status code
     */
    private int tryURL(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            return connection.getResponseCode();
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * Returns a human-readable message for the given status code.
     * @param status - HTTP status code
     * @return - String message
     */
    private String getMessage(int status) {
        if(status == 200) { //we found some result with this tag. Note that we don't check if the tag referenced the correct component.
            return("PASS: %s identifier is registered in the database.");
        } else if(status == 404) { //the tag does not exist, we fail this metric because we can't use a tag which doesn't exist.
            this.failed = true;
            return("FAIL: %s identifier is not registered in the database.");
        } else if (status == -1) { //something went funky and the service stopped responding between the first "are you alive" request and second request.
            this.undefinedBehavior = true;
            return("UNDEFINED: %s identifier may or may not be valid. The lookup service was reachable but did not respond to the lookup request.");
        } else { //something went wrong, and we got a response code we don't know how to handle.
            this.undefinedBehavior = true;
            return("UNDEFINED: %s identifier may or may not be valid. The lookup service returned an unexpected response code: " + status);
        }
    }
}
