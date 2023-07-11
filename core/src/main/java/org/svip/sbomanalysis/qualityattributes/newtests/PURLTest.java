package org.svip.sbomanalysis.qualityattributes.newtests;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.uids.PURL;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.ResultFactory;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Text;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.STATUS;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * file: PURLTest.java
 * Test class for PURL strings in a component
 *
 * @author Matthew Morrison
 */
public class PURLTest extends MetricTest{

    private final String TEST_NAME = "PURLTest";

    private ResultFactory resultFactory;

    private final Component component;

    /**
     * Constructor to create a new PURLTest
     *
     * @param component the component that is being tested
     * @param attributes the list of attributes used
     */
    public PURLTest(Component component, List<ATTRIBUTE> attributes) {
        super(attributes);
        this.component = component;
    }

    /**
     * Conduct a series of tests for a given PURL string
     * @param field the field being tested (purl)
     * @param value the value being tested (the purl string)
     * @return a set of results from each test
     */
    @Override
    public Set<Result> test(String field, String value) {
        Set<Result> results = new HashSet<>();
        // check that purl string value is not null
        if(value != null) {
            resultFactory = new ResultFactory(this.attributes, TEST_NAME);
            results.addAll(isValidPURL(field, value));
            results.addAll(isAccuratePURL(field, value));
            results.addAll(existsInRepo(field, value));
        }
        // purl string is null so no tests can be run
        // return missing Result
        else {
            Text text = new Text(null, field);
            String message = text.getMessage(INFO.MISSING, field);
            String details = text.getDetails(INFO.MISSING, field);
            Result r = new Result(attributes, TEST_NAME, message, details, STATUS.ERROR);
            results.add(r);
        }
        return results;
    }

    /**
     * Test the purl string if it is valid and follows purl schema
     * @param field the field being tested (purl)
     * @param value the purl string
     * @return a result if the purl is valid or not
     */
    private Set<Result> isValidPURL(String field, String value){
        Set<Result> results = new HashSet<>();
        Result r;
        try{
            // create new purl object
            new PURL(value);
            r = resultFactory.pass(field, INFO.VALID, value);
        }
        // failed to create new purl, test fails
        catch(Exception e){
            r = resultFactory.fail(field, INFO.INVALID, value);
        }
        results.add(r);
        return results;
    }

    private Set<Result> isAccuratePURL(String field, String value){
        Set<Result> results = new HashSet<>();
        Result r;
        try{
            PURL purl = new PURL(value);
            results.add(isEqual(field, purl.getName(), component.getName()));

            //TODO test for version? How to access component version for all types?

        }
        // failed to create new purl, test automatically fails
        catch(Exception e){
            r = resultFactory.fail(field, INFO.INVALID, value);
            results.add(r);
        }
        return results;
    }

    /**
     * Helper function to check if 2 fields are equal
     *
     * @param field name of field that is being checked
     * @param purlValue Value stored in the PURL string
     * @param componentValue Value stored in the Component
     * @return Result with the findings
     */
    private Result isEqual(String field, String purlValue, String componentValue){
        Result r;
        // Check if purl value is different
        if(!purlValue.equals(componentValue)){
            r = resultFactory.fail(field, INFO.INVALID, purlValue);
        }
        // Else they both match
        else {
            r = resultFactory.pass(field, INFO.VALID, purlValue);
        }

        return r;
    }

    /**
     * Test if a purl string exists in its respective package manager repo
     * @param field the field being tested (purl)
     * @param value the purl string
     * @return a Set<Result> of if the purl string exists in its repo
     */
    private Set<Result> existsInRepo(String field, String value){
        Set<Result> results = new HashSet<>();
        Result r;
        PURL p;
        try{
            // create new purl object
           p = new PURL(value);
        }
        // failed to create new purl, test automatically fails
        catch(Exception e){
            r = resultFactory.fail(field, INFO.INVALID, value);
            results.add(r);
            return results;
        }
        // holds the response code and package manager of the purl
        int response;
        String packageManager = p.getType();

        try {
            // extract method based on package manager type
            switch (packageManager.toLowerCase()) {
                // TODO More cases need to be added for package manager types
                case "maven" -> response = extractFromMaven(p);
                case "pypi" -> response = extractFromPyPi(p);
                case "nuget" -> response = extractFromNuget(p);
                case "cargo" -> response = extractFromCargo(p);
                case "golang" -> response = extractFromGo(p);
                case "npm" -> response = extractFromNPM(p);
                case "composer" -> response =
                        extractFromComposer(p);
                case "gem" -> response = extractFromGem(p);
                case "hackage" -> response = extractFromHackage(p);
                case "hex" -> response = extractFromHex(p);
                case "conan" -> response = extractFromConan(p);
                case "huggingface" -> response =
                        extractFromHuggingFace(p);
                case "cocoapods" -> response =
                        extractFromCocoapods(p);
                case "cran" -> response = extractFromCran(p);
                case "pub" -> response = extractFromPub(p);
                case "conda" -> response = extractFromConda(p);

                // package managers that are not supported yet
                case "alpm", "apk", "bitbucket", "deb",
                        "docker", "generic", "github", "mlflow",
                        "qpkg", "oci", "rpm", "swid", "swift"
                        -> {
                    r = resultFactory.fail(field, INFO.MISSING, value);
                    results.add(r);
                    // error number to skip other results
                    response = -1;
                }


                // a package manager that is not currently supported
                default -> {
                    r = resultFactory.fail(field, INFO.INVALID, value);
                    results.add(r);
                    // error number to skip other results
                    response = -1;
                }
            }
        }
        // if there are any issues with the url or http connection
        catch (IOException e) {
            r = resultFactory.fail(field, INFO.INVALID, value);
            results.add(r);
            // error number to skip other results
            response = -1;

        }

        if (response != 0 && response != -1) {
            r = checkResponseCode(field, value, response);
            results.add(r);
        }
        // some tests will throw a 0 if a different error occurs
        else if (response == 0) {
            r = resultFactory.fail(field, INFO.INVALID, value);
            results.add(r);
        }

        return results;
    }

    /**
     * Extract data for maven based packages.
     * Source: <a href="https://mvnrepository.com/">...</a> -> returns 403
     * New Source: <a href="https://central.sonatype.com/artifact/">...</a>
     * @param p purl to use to query for info
     * @return an int response code when opening up a connection with the PURL
     * @throws IOException issue with http connection
     */
    private int extractFromMaven(PURL p) throws IOException{
        // maven requires namespace
        if(p.getNamespace() == null || p.getNamespace().size() == 0)
            return 0;

        // maven requires version
        if(p.getVersion() == null)
            return 0;

        // build namespace for request
        StringBuilder namespaceUrl = new StringBuilder();
        for(int i = 0; i < p.getNamespace().size(); i++)
            namespaceUrl.append(p.getNamespace().get(i).toLowerCase()).append("/");

        URL url = new URL ("https://central.sonatype.com/artifact/" +
                namespaceUrl +
                p.getName().toLowerCase() +
                "/" + p.getVersion());
        HttpsURLConnection huc = (HttpsURLConnection) url.openConnection();
        // get the response code from this url
        int responseCode = huc.getResponseCode();
        huc.disconnect();
        return responseCode;
    }

    /**
     * Extract data for python based packages.
     * Source: <a href="https://pypi.org/project/">...</a>
     * @param p purl to use to query for info
     * @return an int response code when opening up a connection with the PURL
     * @throws IOException issue with http connection
     */
    private int extractFromPyPi(PURL p) throws IOException {
        // Query page
        URL url = new URL ("https://pypi.org/project/" +
                p.getName().toLowerCase() +
                (p.getVersion() != null ? "/" + p.getVersion() : ""));
        HttpsURLConnection huc = (HttpsURLConnection) url.openConnection();

        // get the response code from this url
        int responseCode = huc.getResponseCode();
        huc.disconnect();
        return responseCode;
    }

    /**
     * Extract data from C# NuGet based packages
     * Source: <a href="https://www.nuget.org/packages/">...</a>
     * @param p purl to use to query for info
     * @return an int response code when opening up a connection with PURL info
     * @throws IOException issue with http connection
     */
    private int extractFromNuget(PURL p) throws IOException{
        // Query nuget page
        // package name is required, add the version if it is
        // included in the purl
        URL url = new URL ("https://www.nuget.org/packages/" +
                p.getName().toLowerCase() +
                (p.getVersion() != null ? "/" + p.getVersion() : ""));
        HttpsURLConnection huc = (HttpsURLConnection) url.openConnection();
        // get the response code from this url
        int responseCode = huc.getResponseCode();
        huc.disconnect();
        return responseCode;
    }

    /**
     * Extract data from Rust Cargo based packages
     * Source: <a href="https://crates.io/crates/">...</a>
     * @param p purl to use to query for info
     * @return an int response code when opening up a connection with PURL info
     * @throws IOException issue with http connection
     */
    private int extractFromCargo(PURL p) throws IOException{
        // Query cargo page
        // package name is required, add the version if it is
        // included in the purl
        URL url = new URL ("https://crates.io/crates/" +
                p.getName().toLowerCase() +
                (p.getVersion() != null ? "/" + p.getVersion() : ""));
        HttpsURLConnection huc = (HttpsURLConnection) url.openConnection();
        // get the response code from this url
        int responseCode = huc.getResponseCode();
        huc.disconnect();
        return responseCode;
    }
    /**
     * Extract data from Golang based packages
     * Source: <a href="https://pkg.go.dev/">...</a>
     * @param p purl to use to query for info
     * @return an int response code when opening up a connection with PURL info
     * @throws IOException issue with http connection
     */
    private int extractFromGo(PURL p) throws IOException{
        // build namespace for request
        StringBuilder namespaceUrl = new StringBuilder();
        for(int i = 0; i < p.getNamespace().size(); i++)
            namespaceUrl.append(p.getNamespace().get(i).toLowerCase()).append("/");
        // Query go page
        // package name and version are required
        URL url = new URL ("https://pkg.go.dev/" +
                namespaceUrl +
                p.getName().toLowerCase() + "@" +
                p.getVersion());
        HttpsURLConnection huc = (HttpsURLConnection) url.openConnection();
        // get the response code from this url
        int responseCode = huc.getResponseCode();
        huc.disconnect();
        return responseCode;
    }

    /**
     * Extract data from Node NPM based packages
     * Source: <a href="https://registry.npmjs.org/">...</a>
     * @param p purl to use to query for info
     * @return an int response code when opening up a connection with PURL info
     * @throws IOException issue with http connection
     */
    private int extractFromNPM(PURL p) throws IOException{
        // Query npm registry page
        // package name is required, add the version if it is
        // included in the purl
        URL url = new URL ("https://registry.npmjs.org/" +
                p.getName().toLowerCase() +
                (p.getVersion() != null ? "/" + p.getVersion() : ""));
        HttpsURLConnection huc = (HttpsURLConnection) url.openConnection();
        // get the response code from this url
        int responseCode = huc.getResponseCode();
        huc.disconnect();
        return responseCode;
    }

    /**
     * Extract data from Composer PHP based packages
     * Source: <a href="https://packagist.org/packages/">...</a>
     * @param p purl to use to query for info
     * @return an int response code when opening up a connection with PURL info
     * @throws IOException issue with http connection
     */
    private int extractFromComposer(PURL p) throws IOException{

        // concat namespace for the url
        String namespace = String.join("/", p.getNamespace());

        // Query composer packages page
        // package namespace and name is required, add the version if it is
        // included in the purl
        URL url = new URL ("https://packagist.org/packages/" +
                namespace + "/" +
                p.getName().toLowerCase() +
                (p.getVersion() != null ? "#v" + p.getVersion() : ""));
        HttpsURLConnection huc = (HttpsURLConnection) url.openConnection();
        // get the response code from this url
        int responseCode = huc.getResponseCode();
        huc.disconnect();
        return responseCode;
    }

    /**
     * Extract data from Rubygems Gem based packages
     * Source: <a href="https://rubygems.org/gems/">...</a>
     * @param p purl to use to query for info
     * @return an int response code when opening up a connection with PURL info
     * @throws IOException issue with http connection
     */
    private int extractFromGem(PURL p) throws IOException{
        // Query Gem packages page
        // package name is required, add the version if it is
        // included in the purl
        URL url = new URL ("https://rubygems.org/gems/" +
                p.getName().toLowerCase() + "/" +
                (p.getVersion() != null ? "versions/" + p.getVersion() : ""));
        HttpsURLConnection huc = (HttpsURLConnection) url.openConnection();
        // get the response code from this url
        int responseCode = huc.getResponseCode();
        huc.disconnect();
        return responseCode;
    }

    /**
     * Extract data from Haskell Hackage based packages
     * Source: <a href="https://hackage.haskell.org/package/">...</a>
     * @param p purl to use to query for info
     * @return an int response code when opening up a connection with PURL info
     * @throws IOException issue with http connection
     */
    private int extractFromHackage(PURL p) throws IOException{
        // Query Haskell Hackage packages page
        // package name is required, add the version if it is
        // included in the purl
        URL url = new URL ("https://hackage.haskell.org/package/" +
                p.getName().toLowerCase() + "/" +
                (p.getVersion() != null ? "-" + p.getVersion() : ""));
        HttpsURLConnection huc = (HttpsURLConnection) url.openConnection();
        // get the response code from this url
        int responseCode = huc.getResponseCode();
        huc.disconnect();
        return responseCode;
    }

    /**
     * Extract data from Hex based packages
     * Source: <a href="https://hex.pm/packages/">...</a>
     * @param p purl to use to query for info
     * @return an int response code when opening up a connection with PURL info
     * @throws IOException issue with http connection
     */
    private int extractFromHex(PURL p) throws IOException{
        // Query Hex packages page
        // package name is required, add the version if it is
        // included in the purl
        URL url = new URL ("https://hex.pm/packages/" +
                p.getName().toLowerCase() + "/" +
                (p.getVersion() != null ? "/" + p.getVersion() : ""));
        HttpsURLConnection huc = (HttpsURLConnection) url.openConnection();
        // get the response code from this url
        int responseCode = huc.getResponseCode();
        huc.disconnect();
        return responseCode;
    }

    /**
     * Extract data from C/C++ Conan based packages
     * Source: <a href="https://conan.io/center/">...</a>
     * @param p purl to use to query for info
     * @return an int response code when opening up a connection with PURL info
     * @throws IOException issue with http connection
     */
    private int extractFromConan(PURL p) throws IOException{

        // Query Conan packages page
        // package name is required, add the version if it is
        // included in the purl
        //TODO add qualifier info? Doesn't seem to affect link
        URL url = new URL ("https://conan.io/center/" +
                p.getName().toLowerCase() +
                (p.getVersion() != null ? "?version=" + p.getVersion() : ""));
        HttpsURLConnection huc = (HttpsURLConnection) url.openConnection();
        // get the response code from this url
        int responseCode = huc.getResponseCode();
        huc.disconnect();
        return responseCode;
    }

    /**
     * Extract data from Hugging Face ML Models
     * Source: <a href="https://huggingface.co/">...</a>
     * @param p purl to use to query for info
     * @return an int response code when opening up a connection with PURL info
     * @throws IOException issue with http connection
     */
    private int extractFromHuggingFace(PURL p) throws IOException {
        // get namespace, if any
        String namespace = String.join("/", p.getNamespace());

        // Query HuggingFace ML Models page
        // namespace is required if present
        // package name is required, add the version if it is
        // included in the purl
        URL url = new URL("https://huggingface.co/" +
                (!namespace.equals("") ? namespace : "") + "/" +
                p.getName().toLowerCase() +
                (p.getVersion() != null ? "?version=" + p.getVersion() : ""));
        HttpsURLConnection huc = (HttpsURLConnection) url.openConnection();
        // get the response code from this url
        int responseCode = huc.getResponseCode();
        huc.disconnect();
        return responseCode;
    }

    /**
     * Extract data from CocoaPods based packages
     * Source: <a href="https://cdn.jsdelivr.net/cocoa/Specs/">...</a>
     * @param p purl to use to query for info
     * @return an int response code when opening up a connection with PURL info
     * @throws IOException issue with http connection
     */
    private int extractFromCocoapods(PURL p) throws IOException{
        String componentName = p.getName();
        String firstThreeString;
        // try to convert the component's name into an SHA using md5
        // the first three characters are needed for the url searching
        try{
            // convert component name into a HexaDecimal form string
            // it is required to link url (first three characters)
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(componentName.getBytes());
            BigInteger bigInt = new BigInteger(1,digest);
            // convert the bytes to a string (hexadecimal form)
            String hashtext = bigInt.toString(16);
            // split up the characters and concat the first three
            String[] hashTextSplit = hashtext.split("");
            firstThreeString = hashTextSplit[0] + "/" + hashTextSplit[1] +
                    "/" + hashTextSplit[2] + "/";
        }catch(NoSuchAlgorithmException e){
            // failed to convert component name, return 0 to end test
            return 0;
        }
        // Query cocoapods package page
        // first three character of the component's name as an SHA using
        // md5 required
        // package name and version are required
        URL url = new URL("https://cdn.jsdelivr.net/cocoa/Specs/" +
                firstThreeString +
                p.getName() + "/" +
                p.getVersion() + "/" +
                p.getName() + ".podspec.json") ;
        HttpsURLConnection huc = (HttpsURLConnection) url.openConnection();
        // get the response code from this url
        int responseCode = huc.getResponseCode();
        huc.disconnect();
        return responseCode;

    }

    /**
     * Extract data from Cran R based packages
     * Source: <a href="https://cran.r-project.org/web/packages/">...</a>
     * @param p purl to use to query for info
     * @return an int response code when opening up a connection with PURL info
     * @throws IOException issue with http connection
     */
    private int extractFromCran(PURL p) throws IOException{
        // Query Cran R packages page
        // package name is required, add the version if it is
        // included in the purl
        //TODO version does nothing, how to check for specific version?
        URL url = new URL("https://cran.r-project.org/web/packages/" +
                p.getName().toLowerCase() +
                (p.getVersion() != null ? "?version=" + p.getVersion() : "") +
                "/index.html");
        HttpsURLConnection huc = (HttpsURLConnection) url.openConnection();
        // get the response code from this url
        int responseCode = huc.getResponseCode();
        huc.disconnect();
        return responseCode;
    }

    /**
     * Extract data from the Pub repo (for Dart and Flutter packages)
     * Source: <a href="https://pub.dev/packages/">...</a>
     * @param p purl to use to query for info
     * @return an int response code when opening up a connection with PURL info
     * @throws IOException issue with http connection
     */
    private int extractFromPub(PURL p) throws IOException{
        // Query Pub Repository
        // package name is required, add the version if it is
        // included in the purl
        URL url = new URL("https://cran.r-project.org/web/packages/" +
                p.getName().toLowerCase() +
                (p.getVersion() != null ? "/versions/" + p.getVersion() : ""));
        HttpsURLConnection huc = (HttpsURLConnection) url.openConnection();
        // get the response code from this url
        int responseCode = huc.getResponseCode();
        huc.disconnect();
        return responseCode;
    }

    /**
     * Extract data from Conda based packages
     * Source: <a href="https://repo.anaconda.com">...</a>
     * @param p purl to use to query for info
     * @return an int response code when opening up a connection with PURL info
     * @throws IOException issue with http connection
     */
    private int extractFromConda(PURL p) throws IOException{
        // need to get purls qualifiers. build, channel, subdir,
        // and type required for url
        LinkedHashMap<String, String> pQualifiers = p.getQualifiers();
        // usually a string of numbers and chars
        String build = pQualifiers.get("build");
        // what channel the package is located in
        String channel = pQualifiers.get("channel");
        // the associated platform of the package
        String subdir = pQualifiers.get("subdir");
        // package type (tar.bz2,conda, etc)
        String type = pQualifiers.get("type");
        // Query Conda Repository
        // along with the qualifiers, package name and version are required
        URL url = new URL("https://repo.anaconda.com/pkgs/" +
                channel + "/" + subdir + "/" +
                p.getName().toLowerCase() + "-" + p.getVersion() + "-" +
                build + "." + type);
        HttpsURLConnection huc = (HttpsURLConnection) url.openConnection();
        // get the response code from this url
        int responseCode = huc.getResponseCode();
        huc.disconnect();
        return responseCode;
    }


    /**
     * Check the response code of the package manager url
     * @param field the field that's tested
     * @param value the purl string
     * @param response the response code
     * @return a Result based on the response code
     */
    private Result checkResponseCode(String field, String value, int response){
        Result r;

        // if the response code is 200 (HTTP_OK), then
        // package is registered with package manager
        if (response == HttpURLConnection.HTTP_OK) {
           r = resultFactory.pass(field, INFO.VALID, value);
        }
        // any other response codes result in a test fail
        else {
            r = resultFactory.fail(field, INFO.INVALID, value);
        }
        return r;
    }

}
