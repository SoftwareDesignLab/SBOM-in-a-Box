package org.svip.sbomanalysis.qualityattributes.tests;

import org.svip.sbom.model.Component;
import org.svip.sbom.model.SBOM;
import org.svip.sbom.model.uids.PURL;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * file: IsRegisteredTest.java
 *
 * Test each component in a given SBOM if it is registered with a
 * given package manager through its PURL
 * @author Matthew Morrison
 */
public class IsRegisteredTest extends MetricTest{
    // name of the test for results
    private static final String TEST_NAME = "IsRegistered";

    /**
     * Run the test for all components in the SBOM
     * @param sbom the SBOM to test
     * @return a collection of results given from the tests
     */
    @Override
    public List<Result> test(SBOM sbom) {
        List<Result> results = new ArrayList<>();
        // for every component, test for purls and if they are valid
        for(Component c : sbom.getAllComponents()){
            results.addAll(testComponentPURLs(c));
        }
        // return findings of tests
        return results;
    }

    /**
     * Given a component, get all purls and test if the package is
     * registered thorugh its specified type/package manager
     * @param c the component to test
     * @return a collection of results for each PURL associated with
     * the component
     */
    private List<Result> testComponentPURLs(Component c){
        List<Result> purlResults = new ArrayList<>();
        Result r;

        // get all the purls for the given component
        Set<String> purls = c.getPurls();
        // if no purls are present, the test automatically fails
        if(purls.isEmpty()){
            r = new Result(TEST_NAME, Result.STATUS.ERROR,
                    "Component has no PURLs to test");
            r.addContext(c, "PURL Validation");
            r.updateInfo(Result.Context.FIELD_NAME, "PURL");
            r.updateInfo(Result.Context.STRING_VALUE, c.getName());
            purlResults.add(r);
        }
        else{
            // check all purl based on its type
            for(String purlString : c.getPurls()){
                // purl is null, test cannot run, add result as an error
                if(isEmptyOrNull(purlString)){
                    r = new Result(TEST_NAME, Result.STATUS.ERROR,
                            "PURL is null, test cannot run");
                    r.addContext(c, "PURL Validation");
                    r.updateInfo(Result.Context.FIELD_NAME, "PURL");
                    purlResults.add(r);
                    continue;
                }

                PURL p;

                try {
                    p = new PURL(purlString);
                } catch (Exception e) {
                    r = new Result(TEST_NAME, Result.STATUS.ERROR,
                            "PURL is invalid, test cannot run");
                    r.addContext(c, "PURL Validation");
                    r.updateInfo(Result.Context.FIELD_NAME, "PURL");
                    purlResults.add(r);
                    continue;
                }

                // purl is not null, the test can continue
                // holds the response code from the purl
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
                            r = new Result(TEST_NAME, Result.STATUS.ERROR,
                                    "Package Manager is a valid type but " +
                                            "is currently not supported: " +
                                            packageManager);
                            r.addContext(c, "PURL Package Validation");
                            r.updateInfo(Result.Context.FIELD_NAME,
                                    "PURL Package Manager");
                            r.updateInfo(Result.Context.STRING_VALUE,
                                    packageManager);
                            purlResults.add(r);
                            // error number to skip other results
                            response = -1;
                        }


                        // a package manager that is not currently supported
                        default -> {
                            r = new Result(TEST_NAME, Result.STATUS.FAIL,
                                    "Package Manager is an invalid type: " +
                                            packageManager);
                            r.addContext(c, "PURL Package Validation");
                            r.updateInfo(Result.Context.FIELD_NAME,
                                    "PURL Package Manager");
                            r.updateInfo(Result.Context.STRING_VALUE,
                                    p.toString());
                            purlResults.add(r);
                            // error number to skip other results
                            response = -1;
                        }
                    }
                }
                // if there are any issues with the url or http connection
                catch (IOException e) {
                    r = new Result(TEST_NAME, Result.STATUS.ERROR,
                            "PURL had an error in producing URL");
                    r.addContext(c, "PURL Package Validation");
                    r.updateInfo(Result.Context.FIELD_NAME, "PURL");
                    r.updateInfo(Result.Context.STRING_VALUE, p.toString());
                    purlResults.add(r);
                    // error number to skip other results
                    response = -1;

                }
                // no errors occurred in checking the PURL through the URL
                // so some response code was returned
                if (response != 0 && response != -1) {
                    r =checkResponseCode(response, packageManager);
                    r.addContext(c, "PURL Package Validation");
                    r.updateInfo(Result.Context.FIELD_NAME, "PURL");
                    r.updateInfo(Result.Context.STRING_VALUE,
                            p.toString());
                    purlResults.add(r);
                }
                // some tests will throw a 0 if a different error occurs
                else if (response == 0) {
                    r = new Result(TEST_NAME, Result.STATUS.ERROR,
                            "PURL had an error");
                    r.addContext(c, "PURL Package Validation");
                    r.updateInfo(Result.Context.FIELD_NAME, "PURL");
                    r.updateInfo(Result.Context.STRING_VALUE,
                            p.toString());
                    purlResults.add(r);
                }
            }
        }
        return purlResults;
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
                (!isEmptyOrNull(namespace) ? namespace : "") + "/" +
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


    private Result checkResponseCode(int response, String packageManager){
        Result r;

        // if the response code is 200 (HTTP_OK), then
        // package is registered with package manager
        if (response == HttpURLConnection.HTTP_OK) {
            r = new Result(TEST_NAME, Result.STATUS.PASS,
                    "Package is registered with package " +
                            "manager: " + packageManager);
        }
        // any other response codes result in a test fail
        else {
            r = new Result(TEST_NAME, Result.STATUS.FAIL,
                    "Package is not registered with " +
                            "package manager: " +
                            packageManager);
        }
        return r;
    }
}
