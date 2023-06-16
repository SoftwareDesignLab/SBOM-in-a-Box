package org.svip.sbomanalysis.qualityattributes.tests;


import jregex.Matcher;
import jregex.Pattern;
import jregex.REFlags;
import org.apache.commons.io.IOUtils;
import org.svip.sbom.model.Component;
import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.utils.Debug;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * file: ValidSPDXLicenseTest.java
 *
 * Test each component in the given SBOM if the license is an existing
 * valid SPDX License
 * @author Matthew Morrison
 * @author Derek Garcia
 */
public class ValidSPDXLicenseTest extends MetricTest{

    private static final String TEST_NAME = "ValidSPDXLicense";
    private static final String SPDX_LICENSE_LIST_URL = "https://spdx.org/licenses/";

    // Regexes
    private static final String SPDX_TABLE_REGEX = "<tbody>([\\s\\S]*?)<\\/tbody>";
    private static final String SPDX_ROW_REGEX = "<a.*?>(.*?)<\\/a>[\\s\\S]*?<code.*?>(.*?)<\\/code>";

    private final Set<String> SPDX_LICENSE_IDENTIFIERS = new HashSet<>();
    private final HashSet<String> SPDX_LICENSE_NAMES = new HashSet<>();

    private final Set<String> DEPRECIATED_SPDX_LICENSE_IDENTIFIERS = new HashSet<>();
    private final Set<String> DEPRECIATED_SPDX_LICENSE_NAMES = new HashSet<>();



    /**
     * Test all SBOM components for their licenses and if they are a valid SPDX
     * License
     * (url, id, name, etc)
     * @param sbom SBOM to test
     * @return Collection of results for each component
     */
    @Override
    public List<Result> test(SBOM sbom) {
        // create a list to hold each result of sbom components
        List<Result> results = new ArrayList<>();

        // Error if can't populate sets
        if(!loadSPDXLicenseData()){
            results.add(new Result(TEST_NAME, Result.STATUS.ERROR, "Couldn't query " + SPDX_LICENSE_LIST_URL));
            return results;
        }

        // for every component, test for valid SPDX Licenses
        for(Component c : sbom.getAllComponents()){

            // Skip if no licences
            if(isEmptyOrNull(c.getLicenses()))
                continue;

            results.addAll(testSPDXLicense(c));
        }
        return results;
    }

    /**
     * Query the SPDX License page and get all license details
     *
     * @return true if success, fails otherwise
     */
    private boolean loadSPDXLicenseData(){
        try {
            // Open connection
            URL url = new URL(SPDX_LICENSE_LIST_URL);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("GET");

            // valid SPDX License Identifier
            if(huc.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get HTML
                InputStream in = huc.getInputStream();
                String encoding = huc.getContentEncoding();
                encoding = encoding == null ? "UTF-8" : encoding;
                String html = IOUtils.toString(in, encoding);

                // Create table regex
                Pattern p = new Pattern(SPDX_TABLE_REGEX, REFlags.MULTILINE);
                Matcher m = p.matcher(html);

                // Populate Active Licenses
                if(!m.find()){
                    throw new Exception("Failed to parse SPDX License table");
                } else {
                    // todo numNames != numIDs, names can have >1 Ids
                    popululateDataSets(m.group(1), SPDX_LICENSE_NAMES, SPDX_LICENSE_IDENTIFIERS);
                }

                // Populate Depreciated Licenses
                if(!m.find()){
                    throw new Exception("Failed to parse Depreciated SPDX License table");
                } else {
                    popululateDataSets(m.group(1), DEPRECIATED_SPDX_LICENSE_NAMES, DEPRECIATED_SPDX_LICENSE_IDENTIFIERS);
                }
            }
        } catch (Exception e){
            // failure
            Debug.log(Debug.LOG_TYPE.ERROR, e);
            return false;
        }
        // success
        return true;
    }

    /**
     * Populate the given sets with table information
     *
     * @param tableHTML html table details with spdx license info
     * @param names Set of SPDX License Names to update
     * @param identifiers Set of SPDX License Identifiers to update
     */
    private void popululateDataSets(String tableHTML, Set<String> names, Set<String> identifiers){
        // build regex
        Pattern p = new Pattern(SPDX_ROW_REGEX, REFlags.MULTILINE);
        Matcher m = p.matcher(tableHTML);

        // Add all names and identifiers
        while(m.find()){
            names.add(m.group(1).toLowerCase());
            identifiers.add(m.group(2).toLowerCase());
        }
    }

    /**
     * Test a component's licenses to see if it is a valid SPDX license
     * via url
     * @param c the component to be tested
     * @return a collection of results for each license of a component and
     * its validity
     */
    private List<Result> testSPDXLicense(Component c) {
        List<Result> results = new ArrayList<>();
        Result r;

        // for every license id
        //TODO only held as a string. A License object should be created
        for(String l : c.getLicenses()){
            // Test if valid Identifier
            if(SPDX_LICENSE_IDENTIFIERS.contains(l.toLowerCase())){
                r = new Result(TEST_NAME, Result.STATUS.PASS, "Valid SPDX License Identifier");
            } else {
                r = new Result(TEST_NAME, Result.STATUS.FAIL, "Invalid SPDX License Identifier");
            }
            r.updateInfo(Result.Context.STRING_VALUE, l);
            r.addContext(c, "license");
            results.add(r);

            // Test if valid name
            if(SPDX_LICENSE_NAMES.contains(l.toLowerCase())){
                r = new Result(TEST_NAME, Result.STATUS.PASS, "Valid SPDX License Name");
            } else {
                r = new Result(TEST_NAME, Result.STATUS.FAIL, "Invalid SPDX License Name");
            }
            r.updateInfo(Result.Context.STRING_VALUE, l);
            r.addContext(c, "license");
            results.add(r);

            // Test if depreciated Identifier
            if(DEPRECIATED_SPDX_LICENSE_IDENTIFIERS.contains(l.toLowerCase())){
                r = new Result(TEST_NAME, Result.STATUS.FAIL, "Valid SPDX License Identifier but is depreciated");
                r.updateInfo(Result.Context.STRING_VALUE, l);
                r.addContext(c, "license");
                results.add(r);
            }

            // Test if depreciated Name
            if(DEPRECIATED_SPDX_LICENSE_NAMES.contains(l.toLowerCase())){
                r = new Result(TEST_NAME, Result.STATUS.FAIL, "Valid SPDX License Name but is depreciated");
                r.updateInfo(Result.Context.STRING_VALUE, l);
                r.addContext(c, "license");
                results.add(r);
            }
        }

        return results;
    }
}
