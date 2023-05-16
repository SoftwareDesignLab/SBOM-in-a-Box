package org.svip.sbomfactory.generators.generators.utils;

import org.svip.sbomfactory.generators.utils.QueryWorker;
import org.svip.sbomfactory.generators.utils.SPDXLicenseQueryWorker;
import org.svip.sbomfactory.generators.utils.Debug;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.svip.sbomfactory.generators.utils.Debug.log;

/**
 * File: LicenseManager.java
 * <p>
 * This class is responsible for checking the validity of multiple licenses against the list of valid SPDX licenses and
 * converting any valid licenses to their short string equivalent for use in an SPDX document.
 * </p>
 * @author Ian Dunn
 */
public class LicenseManager {

    //#region Constants

    /** TODO Support license list version (valid field in SPDX documents)
     * The URL where the list of valid SPDX licenses and their details are stored.
     */
    private static final String SPDX_LICENSE_URL = "https://spdx.org/licenses/";

    private static final Set<String> COMMON_TOKENS =  new HashSet<>() {{
        add("the");
        add("open");
        add("free");
        add("documentation");
        add("software");
        add("version");
        add("license");
        add("notice");
        add("disclaimer");
        add("all");
        add("rights");
        add("reserved");
        add("copyright");
        add("c");
    }};

    //#endregion

    //#region Attributes

    /** TODO We can add a backup map if no licenses are found for the query
     * A HashMap to map long license strings to their shortened equivalent that will be used in an SPDX document.
     * <br>
     * Format: {@code Map<Long License, Short License>}
     */
    private static final Map<String, String> licenses = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    static {
        // Query the SPDX license URL to get valid licenses
        QueryWorker licenseWorker = new SPDXLicenseQueryWorker(licenses, SPDX_LICENSE_URL);
        licenseWorker.run();

        // Check if licenses were found
        int numLicenses = licenses.keySet().size();
        log(Debug.LOG_TYPE.DEBUG, String.format("LicenseManager: %d licenses parsed.", numLicenses));
        if(numLicenses < 1)
            log(Debug.LOG_TYPE.DEBUG, String.format("LicenseManager: No licenses found from %s", licenseWorker.getUrl()));
    }

    //#endregion

    //#region Core Methods

    // TODO Docstring
    public static boolean isValidShortString(String shortString) {
        return licenses.values().stream().map(String::toLowerCase).collect(Collectors.toSet())
                .contains(shortString.toLowerCase());
    }

    public static String getNameFromSpdxString(String shortString) {
        Map.Entry<String, String> found = licenses.entrySet().stream()
                .filter(entry -> shortString.equals(entry.getValue())).findFirst().orElse(null);

        if(found != null) return found.getKey();
        return null;
    }

    /**
     * This method parses a single long license string into an SPDX short license string. If the long string does not
     * exactly match the SPDX long license format, it will be tokenized in order to assume the most similar license.
     *
     * @param longString The long license string to parse into a valid license.
     * @return An SPDX short license string for use in an SPDX document.
     */
    public static String parseLicense(String longString) {
        if(isValidShortString(longString)) return longString; // Check if the short license string
        String shortString = licenses.get(longString); // Attempt to directly get the short string
        if(shortString != null) return shortString;

        Set<String> tokens = tokenizeLicense(longString); // Get unique words from the license
        String licenseVersion = getLicenseVersion(longString); // Get a version (if one exists) from the license
        Set<String> validKeys = licenses.keySet(); // Get a set of valid long strings to search through later

        // If a license version exists, filter out all licenses that don't have that version
        if(licenseVersion != null) {
            validKeys = validKeys.stream().filter(l -> l.contains(licenseVersion)).collect(Collectors.toSet());

            // Remove license version from valid tokens
            tokens.remove(licenseVersion);
            tokens.remove("v" + licenseVersion);
            tokens.remove("version" + licenseVersion);
            tokens.remove("Version" + licenseVersion);
        }

        /*
            Common Edge Cases
         */

        if(tokens.size() == 1) {
            switch(tokens.stream().map(String::toLowerCase).toList().get(0)) {
                case "bsd" -> { return "0BSD"; }
                case "apache" -> { return "Apache-2.0"; }
                case "mit" -> { return "MIT"; }
            }
        }

        for(String token : tokens) {
            if (isValidShortString(token)) return token;
        }

        Map<String, Integer> matches = new HashMap<>();

        for(String license : validKeys) { // Loop through the keys we have left
            // Tokenize a lowercase version of the license to check for matches
            Set<String> tokenizedLicense = tokenizeLicense(license.toLowerCase());

            tokens.forEach(token -> {
                if(tokenizedLicense.contains(token.toLowerCase())) {
                    matches.merge(license, 1, Integer::sum); // Increment the number of matches for this license
                }
            });
        }

        // If the end of the license list has been reached, we cannot assume a license
        if(matches.size() > 0) {
            String license = Collections.max(matches.entrySet(), Map.Entry.comparingByValue()).getKey();
            Debug.log(Debug.LOG_TYPE.WARN, String.format("LicenseManager: License \"%s\" assumed" +
                    " to be \"%s\"", longString, license));
            return licenses.get(license);
        }

        Debug.log(Debug.LOG_TYPE.WARN, (String.format("No license found when attempting to parse: \"%s\"",
                longString)));
        return null;
    }

    /**
     * Returns a string of licenses concatenated by "AND" (according to the SPDX license specification). It defaults to
     * the short string, but if one cannot be found it will concatenate the long string.
     *
     * @param licenses The set of licenses to concatenate
     * @return The string version of each license concatenated by "AND"
     */
    public static String getConcatenatedLicenseString(Collection<License> licenses) {
        List<String> stringLicenses = licenses.stream().map(License::toString).toList();
        return String.join(" AND ", stringLicenses);
    }

    //#endregion

    //#region Helper Methods

    /**
     * Private helper method to tokenize a license by splitting up each word by whitespace, comma, or colon and
     * set each token to lowercase. It then removes common words, such as "the", "open", and "free".
     *
     * @param license The license string to tokenize.
     * @return A list containing all tokens in the license.
     */
    private static Set<String> tokenizeLicense(String license) {
        // Split by whitespace to get words in license text
        Set<String> tokens = new HashSet<>(List.of(license.split("[\\s,():]+")));

        return tokens.stream().filter(token -> !COMMON_TOKENS.contains(token.toLowerCase()))
                .collect(Collectors.toSet());
    }

    /**
     * Private helper method to extract the version from a license string, if one exists.
     *
     * @param license The license string to extract the version from.
     * @return The version of a license in the format X.X
     */
    private static String getLicenseVersion(String license) {
        String licenseVersion = null;

        // Regex to match a license version
        // Regex101: https://regex101.com/r/luc3Cn/1
        Matcher m = Pattern.compile(".*(\\d\\.\\d+).*", Pattern.MULTILINE).matcher(license);
        if(m.find()) licenseVersion = m.group(1).trim(); // If a match is found, get it and return the version

        return licenseVersion;
    }

    //#endregion
}
