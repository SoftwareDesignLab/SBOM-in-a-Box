package org.svip.sbomanalysis.qualityattributes.tests;

import org.svip.sbomanalysis.qualityattributes.tests.testresults.*;
import org.svip.sbom.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Set;

/**
 * <b>File</b>: DataVerificationTest.java<br>
 * <b>Description</b>: Tests a component to ensure it
 * still exists on its respective package manager
 *
 * @author Juan Francisco Patino
 */
public class DataVerificationTest extends MetricTest {

    private static final int MAX_CONNECTION_TIMEOUT = 1000;
    public DataVerificationTest() {
        super("Data Verification Test");
    }

    /**
     * General test to verify component data
     *
     * @param c component to test
     * @return test results
     */
    @Override
    public TestResults test(Component c) {

        final TestResults testResults = new TestResults(c); // Init TestResults for this component

        Set<PURL> purls = c.getPurls();
        if(purls.isEmpty()){
            testResults.addTest(new Test(false, "Component has no PURL"));
            return testResults;
        }
        for (PURL p: c.getPurls()
             ) {

            try{

                String[] fromOnline = extractedFromPURL(p);
                String packageManagerName = p.getPackageManager().name().toLowerCase();

                String name = p.getName();
                String nameFoundOnline = fromOnline[0].toLowerCase();

                String version = p.getVersion();
                String versionFoundOnline = fromOnline[1].toLowerCase();

                String publisher;

                if (c.getPublisher() == null) {
                    publisher = "";
                }
                else {
                    publisher = c.getPublisher().toLowerCase();
                }

                String publisherFoundOnline = fromOnline[2].toLowerCase().strip();

                // check whatever is online at least contains this component, or vice versa
                if(name == null || !((name.contains(nameFoundOnline)|| nameFoundOnline.contains(name))))
                    testResults.addTest(new Test(false, "Name ", name, "does not match ",
                            nameFoundOnline, " in ", packageManagerName));

                if(version == null || !versionFoundOnline.contains(version))
                    testResults.addTest(new Test(false,"Version ",version," not found in ",
                            packageManagerName, " database"));

                if(!((publisher.contains(publisherFoundOnline)|| publisherFoundOnline.contains(publisher))))
                    testResults.addTest(new Test(false,"Publisher Name", publisher,
                            " does not match ", publisherFoundOnline," in ", packageManagerName, " database"));
            }
            catch(IOException e){
                testResults.addTest(new Test(true,"Error accessing ",
                        p.getPackageManager().name().toLowerCase(),
                        " database\n", e.getMessage()));

            }

        }

        if(testResults.getTests().size() == 0) testResults.addTest(new Test(true,"Component was " +
                "found online"));
        return testResults;
    }

    /**
         Extract name, version, and publisher from package manager online
         @param purl in the form of a string
         @return component name, version(s), publisher name found online. Empty strings if not found
    */
    private static String[] extractedFromPURL(PURL purl) throws IOException {
        return extractFromAlpine(purl.toString());
        // todo: we don't test for Debian or Python pm yet
    }

    /**
        Extract name, version, and publisher from Alpine linux package manager online
        @param p PURl in the form of a string
        @return component name, version(s), publisher name found online. Empty strings if not found
     */
    private static String[] extractFromAlpine(String p) throws IOException {

        String[] purlSplit = p.split("[/@]");
        String nameFromPurl = purlSplit[2];
        HttpURLConnection q = queryURL("https://pkgs.alpinelinux.org/packages?name=" + nameFromPurl);
        htmlResult result = getHtmlResult(q); // I had the IDE do this
        String html = result.response().toString();
        result.in().close();

        // if name not found
        if(html.contains("not found"))
            return new String[]{"", "", ""};

        // otherwise
        String table = html.split("<tbody>")[1];
        String row = table.split("<tr>")[1]; // we just care about the top row - most recent result
        String[] columns = row.split("<td");

        String nameColumn = "";
        String publisherColumn = "";//

        for (String column: columns
             ) {

            if(column.contains("package\">"))
                nameColumn = column;

            else if(column.contains("maintainer\">")) {
                publisherColumn = column;
                break;
            }
        }
        return new String[]{getSpecific(nameColumn), checkVersions(table), getSpecific(publisherColumn).strip()};
    }

    /**
      @param table in the form of a string
      @return all version numbers from query
     */
    private static String checkVersions(String table){

        StringBuilder versions = new StringBuilder();
        String[] rows = table.split("<tr>");
        for (String row: rows
             ) {

            String[] columns = row.split("<td");
            for (String col: columns
                 ) {
                if(col.contains("version\">"))
                    versions.append(getSpecific(col)).append(", ");

            }

        }

        if(versions.toString().length() == 0) {
            return "";
        }
        else {
            return versions.substring(0, versions.toString().length() - 1);
        }


    }

    /**
        From the last HTML element we narrow down to, find what we are looking for at the top of the table
        @param column table column in the form of a string
        @return specific word at the end of the column, right before '/a>'
     */
    private static String getSpecific(String column) {
        String[] elements = column.split("[<>]");
        String found = "";
        for(int i = 0; i < elements.length; i++){
            if(elements[i].equals("/a")){
                found = elements[i-1];
                break;
            }
        }
        return found;
    }
    /**
        Given an http connection, return the HTML
        @param q HTML connection
        @return the HTML
     */
    private static htmlResult getHtmlResult(HttpURLConnection q) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(q.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        return new htmlResult(in, response);
    }

    /**
        Created by IDE for readability
     */
    private record htmlResult(BufferedReader in, StringBuffer response) {
    }

    /**
        Adapted from queryURL() from Parser.java in BenchmarkParser
        @param urlString the URL
        @return HTTP connection
     */
    protected static HttpURLConnection queryURL(String urlString) throws IOException {
        try {
            final URL url = new URL(urlString);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setConnectTimeout(MAX_CONNECTION_TIMEOUT);
            connection.connect();
            return connection;
        }
        catch (SocketTimeoutException ignored) {
            throw new SocketTimeoutException("Connection timed out...");
        }
    }
}


