package utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * File: SPDXLicenseQueryWorker.java
 * <p>
 * QueryWorker implementation that queries an SPDX license website and parses information to add to a reference to a Map
 * mapping a license's long format to its short format.
 * </p>
 * @author Ian Dunn
 */
public class SPDXLicenseQueryWorker extends QueryWorker {

    //#region Constants

    /**
     * The regex used to parse a table row of the SPDX license table into a long and short format.
     * <a href="https://regex101.com/r/oiEUtO/1">Regex101</a>
     */
    private static final String SPDX_LICENSE_REGEX =
            "<td><a (?:.*)>(.*)</a></td>\\s*<td (?:.*)\\s*<code (?:.*)>(.*)</code></td>";

    //#endregion

    //#region Attributes

    /**
     * The Map reference that will be populated with the long and short formats of each license.
     */
    private Map<String, String> licenseMap;

    //#endregion

    //#region Constructors

    /**
     * The default constructor for an SPDXLicenseQueryWorker instance.
     *
     * @param licenseMap A reference to a map that will be populated with the long and short formats of each license.
     * @param url The URL to query to get the table of license information.
     */
    public SPDXLicenseQueryWorker(Map<String, String> licenseMap, String url) {
        super(null, url);
        this.licenseMap = licenseMap;
    }

    //#endregion

    //#region Overrides

    /**
     * Overrides the default run() method of QueryWorker to get the contents of the URL and match all licenses according
     * to a regex.
     */
    @Override
    public void run() {
        // Get page contents
        final String contents = getUrlContents(queryURL(this.url, true)).trim();

        // Match all licenses (group 1: long string, group 2: short string)
        Matcher m = Pattern.compile(SPDX_LICENSE_REGEX, Pattern.MULTILINE).matcher(contents);

        // Keep matching until all licenses are found
        while(m.find()) {
            this.licenseMap.put(m.group(1).trim(), m.group(2).trim()); // Add the license to the Map reference
        }
    }

    //#endregion
}
