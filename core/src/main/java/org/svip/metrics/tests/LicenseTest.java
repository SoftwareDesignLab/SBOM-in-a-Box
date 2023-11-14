package org.svip.metrics.tests;

import jregex.Matcher;
import jregex.Pattern;
import jregex.REFlags;
import org.apache.commons.io.IOUtils;
import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.ResultFactory;
import org.svip.metrics.resultfactory.enumerations.INFO;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;
import org.svip.utils.Debug;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * file: LicenseTest.java
 * Series of tests for license string and license expression objects
 *
 * @author Matthew Morrison
 * @author Derek Garcia
 */
public class LicenseTest extends MetricTest {

    private final ResultFactory resultFactory;

    /**
     * For isValidSPDXLicense
     */
    private static final String SPDX_LICENSE_LIST_URL = "https://spdx.org/licenses/";

    // Regexes
    private static final String SPDX_TABLE_REGEX = "<tbody>([\\s\\S]*?)<\\/tbody>";
    private static final String SPDX_ROW_REGEX = "<a.*?>(.*?)<\\/a>[\\s\\S]*?<code.*?>(.*?)<\\/code>";
    private final Set<String> SPDX_LICENSE_IDENTIFIERS = new HashSet<>();
    private final HashSet<String> SPDX_LICENSE_NAMES = new HashSet<>();

    private final Set<String> DEPRECIATED_SPDX_LICENSE_IDENTIFIERS = new HashSet<>();
    private final Set<String> DEPRECIATED_SPDX_LICENSE_NAMES = new HashSet<>();

    private final String componentName;


    /**
     * Constructor to create a new MetricTest
     *
     * @param attributes the list of attributes used
     */
    public LicenseTest(String componentName, ATTRIBUTE... attributes) {
        super(attributes);
        String TEST_NAME = "LicenseTest";
        resultFactory = new ResultFactory(TEST_NAME, attributes);
        loadSPDXLicenseData();
        this.componentName = componentName;
    }

    /**
     * Perform the tests for a license
     *
     * @param field the field being tested
     * @param value the value being tested
     * @return a set of results from each test
     */
    @Override
    public Set<Result> test(String field, String value) {
        Set<Result> results = new HashSet<>();
        // license is not a null value and does exist, tests can run
        if (value != null && !value.equals("")) {
            results.addAll(validSPDXLicenseResults(field, value));
        }
        // license is a null value and does not exist, tests cannot be run
        // return missing Result
        else {
            Result r = resultFactory.error(field, INFO.NULL,
                    value, componentName);
            results.add(r);
        }
        return results;
    }

    /**
     * Test a license string to see if it is a valid license expression
     *
     * @param field the field to test
     * @param value the license string
     * @return a set of results
     */
    //TODO implement once LicenseExpression Object is implemented and used
    private Set<Result> isValidLicenseExpression(String field, String value) {
        return null;
    }

    /**
     * Test a given license value if it is a valid SPDX license
     *
     * @param field the field to test
     * @param value the license string
     * @return a set of results
     */
    private Set<Result> validSPDXLicenseResults(String field, String value) {
        var rf = new ResultFactory("Valid SPDX License", ATTRIBUTE.COMPLETENESS, ATTRIBUTE.UNIQUENESS, ATTRIBUTE.MINIMUM_ELEMENTS);
        Set<Result> results = new HashSet<>();
        Result r;

        // Error if can't populate sets
        if (!loadSPDXLicenseData()) {
            r = rf.error(field, INFO.ERROR,
                    "SPDX License Data", componentName);
            results.add(r);
            return results;
        }
        // sets were populated
        else {
            // if the license value is empty, test cannot be run
            if (value == null || value.equals("")) {
                r = rf.error(field, INFO.MISSING,
                        value, componentName);
                results.add(r);
                return results;
            }
            results.add(testSPDXLicense(field, value));
        }

        return results;
    }

    /**
     * Query the SPDX License page and get all license details
     *
     * @return true if success, fails otherwise
     */
    private boolean loadSPDXLicenseData() {
        try {
            // Open connection
            URL url = new URL(SPDX_LICENSE_LIST_URL);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("GET");

            // valid SPDX License Identifier
            if (huc.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get HTML
                InputStream in = huc.getInputStream();
                String encoding = huc.getContentEncoding();
                encoding = encoding == null ? "UTF-8" : encoding;
                String html = IOUtils.toString(in, encoding);

                // Create table regex
                Pattern p = new Pattern(SPDX_TABLE_REGEX, REFlags.MULTILINE);
                Matcher m = p.matcher(html);

                // Populate Active Licenses
                if (!m.find()) {
                    throw new Exception("Failed to parse SPDX License table");
                } else {
                    // todo numNames != numIDs, names can have >1 Ids
                    popululateDataSets(m.group(1), SPDX_LICENSE_NAMES, SPDX_LICENSE_IDENTIFIERS);
                }

                // Populate Depreciated Licenses
                if (!m.find()) {
                    throw new Exception("Failed to parse Depreciated SPDX License table");
                } else {
                    popululateDataSets(m.group(1), DEPRECIATED_SPDX_LICENSE_NAMES, DEPRECIATED_SPDX_LICENSE_IDENTIFIERS);
                }
            }
        } catch (Exception e) {
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
     * @param tableHTML   html table details with spdx license info
     * @param names       Set of SPDX License Names to update
     * @param identifiers Set of SPDX License Identifiers to update
     */
    private void popululateDataSets(String tableHTML, Set<String> names, Set<String> identifiers) {
        // build regex
        Pattern p = new Pattern(SPDX_ROW_REGEX, REFlags.MULTILINE);
        Matcher m = p.matcher(tableHTML);

        // Add all names and identifiers
        while (m.find()) {
            names.add(m.group(1).toLowerCase());
            identifiers.add(m.group(2).toLowerCase());
        }
    }

    /**
     * Test a component's licenses to see if it is a valid SPDX license
     * via url
     *
     * @param value the license string to be tested
     * @return a collection of results for each license of a component and
     * its validity
     */
    private Result testSPDXLicense(String field, String value) {
        var rf = new ResultFactory("Valid SPDX License", ATTRIBUTE.COMPLETENESS, ATTRIBUTE.UNIQUENESS, ATTRIBUTE.MINIMUM_ELEMENTS);

        // TODO only held as a string. A License object should be created

        // Test if valid identifier
        if (SPDX_LICENSE_IDENTIFIERS.contains(value.toLowerCase())) {
            return rf.pass(field, INFO.VALID,
                    value, componentName);
        }
        // Test if valid name
        else if (SPDX_LICENSE_NAMES.contains(value.toLowerCase())) {
            return rf.pass(field, INFO.VALID,
                    value, componentName);
        }

        // Test if depreciated Identifier
        else if (DEPRECIATED_SPDX_LICENSE_IDENTIFIERS.contains(value.toLowerCase())) {
            return rf.fail(field, INFO.INVALID,
                    value, componentName);
        }

        // Test if depreciated Name
        else if (DEPRECIATED_SPDX_LICENSE_NAMES.contains(value.toLowerCase())) {
            return rf.fail(field, INFO.INVALID,
                    value, componentName);
        }

        // name/id is in neither list and does not exist
        else {
            return rf.fail(field, INFO.INVALID,
                    value, componentName);
        }
    }
}
