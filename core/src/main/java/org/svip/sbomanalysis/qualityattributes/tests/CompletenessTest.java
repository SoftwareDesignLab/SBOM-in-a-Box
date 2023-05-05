package org.svip.sbomanalysis.qualityattributes.tests;

import org.svip.sbomanalysis.qualityattributes.tests.testresults.*;

import org.svip.sbom.model.*;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * File: CompletenessTest.java
 * An instance of CompletenessTest tests the publisher name, component name, component version, all CPEs, and all PURLs
 * to determine if they are not empty and formatted correctly (if applicable).
 *
 * @author Dylan Mulligan
 * @author Ian Dunn
 */
public class CompletenessTest extends MetricTest {

    /**
     * The Regex used to test the format of a component publisher email.
     */
    private final Pattern publisherEmailRegex;

    /**
     * The Regex used to test the format of a component version.
     */
    private final Pattern componentVersionRegex;

    /**
     * The Regex used to test the format of a CPE v2.3 identifier.
     */
    private final Pattern cpe23Regex;

    /**
     * The Regex used to test the format of a PURL string.
     */
    private final Pattern purlRegex;

    /**
     * Constructor to build the regex patterns used to test the format of a component information
     */
    protected CompletenessTest() {
        super("Completeness Test"); // Test name

        /*
            Checks if publisher email is in form: "email@mail.com"
         */
        this.publisherEmailRegex = Pattern.compile("(?:(Person|Organization)?: (.*?))? ?<?(\\S+@\\S+\\.[^\\s>]+)>?", Pattern.MULTILINE);

        /*
            Regex101: https://regex101.com/r/BjMJCP/1
            Checks if version is in form: "12.*" | "4:*", version format varies a lot
            Also supports git commit hashes (for example docker compose uses this)
         */
        this.componentVersionRegex = Pattern.compile("^(v?[0-9]+[\\.:\\-].*|[0-9a-fA-F]{7,40})$", Pattern.MULTILINE);

        // TODO for these patterns: check if name, version, etc matches component name, version, etc. Make classes?

        /*
            Official CPE Schema: https://csrc.nist.gov/schema/cpe/2.3/cpe-naming_2.3.xsd
         */
        this.cpe23Regex = Pattern.compile("cpe:2\\.3:[aho\\*\\-](:(((\\?*|\\*?)([a-zA-Z0-9\\-\\._]|(\\\\[\\\\\\*\\?" +
                "!\"#$$%&'\\(\\)\\+,/:;<=>@\\[\\]\\^`\\{\\|}~]))+(\\?*|\\*?))|[\\*\\-])){5}(:(([a-zA-Z]{2,3}" +
                "(-([a-zA-Z]{2}|[0-9]{3}))?)|[\\*\\-]))(:(((\\?*|\\*?)([a-zA-Z0-9\\-\\._]|(\\\\[\\\\\\*\\?!\"#$$%&'" +
                "\\(\\)\\+,/:;<=>@\\[\\]\\^`\\{\\|}~]))+(\\?*|\\*?))|[\\*\\-])){4}", Pattern.MULTILINE);

        /*
            Regex101: https://regex101.com/r/vp2Hk0/1 (i love writing regex!!!)
            Official PURL spec: https://github.com/package-url/purl-spec/blob/master/PURL-SPECIFICATION.rst
         */
        this.purlRegex = Pattern.compile("^pkg:([a-zA-Z][a-zA-Z0-9-~._%]*\\/)+[a-zA-Z][a-zA-Z0-9-~._%]*(@(" +
                "[a-zA-Z0-9-~._%]+))?(\\?(([a-zA-Z][a-zA-Z0-9_.-]*=.+)&)*([a-zA-Z][a-zA-Z0-9-~._%]*=.+))?(#(" +
                "[a-zA-Z0-9-~._%]*\\/)+[a-zA-Z0-9-~._%]*)?", Pattern.MULTILINE);
    }

    /**
     * Test a single Component's publisher name, component name, component version, all CPEs, and all PURLs
     * to determine if they are not empty and formatted correctly (if applicable).
     *
     * @param c The component to test for completeness
     * @return A TestResults instance containing the results of all the above tests
     */
    @Override
    public TestResults test(Component c) {
        // Init StringBuilder
        final TestResults testResults = new TestResults(c); // Init TestResults for this component

        // Test Publisher Name
        testResults.addTest(testPublisherName(c));

        // Test Publisher Email
        testResults.addTest(testPublisherEmail(c));

        // Test Component Name
        testResults.addTest(testComponentName(c));

        // Test Component Version
        testResults.addTest(testComponentVersion(c));

        // Test CPEs
        testResults.addTest(testCPEs(c));

        // Test PURLs
        testResults.addTest(testPURLs(c));

        // Return result
        return testResults;
    }

    // TODO: Update translator to have publisherName and publisherEmail fields so we don't have to do it in this class

    /**
     * Helper method to get a substring of ONLY the first and last name of a publisher.
     *
     * @param c The component to get the publisher name of
     * @return A substring containing ONLY the first and last name of a publisher (no email).
     */
    private String getPublisherName(Component c) {
        if(c.getPublisher() == null) return null;
        String publisher = c.getPublisher(); // Do this to make it more semantic

        int firstCharFirstName = publisher.indexOf(" ") + 1; // Get first index of " " + 1 to get firstname (if SPDX)
        int endCharLastName = publisher.indexOf("<") - 1; // Get first index of "<" - 1 since endIndex is not inclusive

        // If publisher does not contain a colon, this is CDX format of "Firstname Lastname <email@mail.xyz>"
        if(!publisher.contains(":"))
            firstCharFirstName = 0; // Set first character to beginning
        // Otherwise, it will be SPDX format of "Person: Firstname Lastname <email@mail.xyz>"

        if(endCharLastName == -2) // If no email in string, just use end of string to get full name
            endCharLastName = publisher.length();

        return publisher.substring(firstCharFirstName, endCharLastName); // Return final substring
    }

    /**
     * Helper method to get a substring of ONLY the email of a publisher.
     *
     * @param c The component to get the publisher email of
     * @return A substring containing ONLY the email of a publisher (no first and last name).
     */
    private String getPublisherEmail(Component c) {
        if(c.getPublisher() == null) return null;
        if (c.getPublisher().equals("")) return null;
        String publisher = c.getPublisher(); // Do this to make it more semantic

        int firstCharEmail = publisher.indexOf("<") + 1;

        if(firstCharEmail == 0) // If no "<", then there is no email that exists
            return null; // Return blank email

        return publisher.substring(
                publisher.indexOf("<") + 1, // Will result in first character of email
                publisher.indexOf(">") // Will result in last character of email, endIndex is not inclusive
        );
    }

    /**
     * Helper method that tests to ensure the component publisher name:
     * a) Exists and
     * b) Is not empty
     *
     * @param c The component to test the publisher of
     * @return A single Test instance, describing if the test passed or failed and its details.
     */
    private Test testPublisherName(Component c) {
        if (getPublisherName(c) == null || getPublisherName(c).isEmpty()) // Check to make sure publisher name exists
            return new Test(false, "Publisher Name is Not Complete '", getPublisherName(c), "'."); // Test failed
        return new Test(true, "Publisher Name is Complete."); // Test passed
    }

    /**
     * Helper method that tests to ensure the component publisher email:
     * a) Exists and
     * b) Is in the format of the Regex specified in the constructor
     *
     * @param c The component to test the publisher of
     * @return A single Test instance, describing if the test passed or failed and its details.
     */
    private Test testPublisherEmail(Component c) {
        if (getPublisherEmail(c) == null || // Check publisher email against regex
                !this.publisherEmailRegex.matcher(getPublisherEmail(c).strip()).matches())
            return new Test(false, "Publisher Email is Not Complete '", getPublisherEmail(c), "'."); // Test failed
        return new Test(true, "Publisher Email is Complete."); // Test passed
    }

    /**
     * Helper method that tests to ensure the component name exists.
     *
     * @param c The component to test the name of
     * @return A single Test instance, describing if the test passed or failed and its details.
     */
    private Test testComponentName(Component c) {
        if (c.getName().isBlank()) // Check if name exists
            return new Test(false, "Name is Not Complete: '", c.getName(), "'."); // Test failed
        return new Test(true, "Name is Complete."); // Test passed
    }

    /**
     * Helper method that tests to ensure the component version is in the format of the Regex specified
     * in the constructor.
     *
     * @param c The component to test the version of
     * @return A single Test instance, describing if the test passed or failed and its details.
     */
    private Test testComponentVersion(Component c) {
        if (c.getVersion() == null || !this.componentVersionRegex.matcher(c.getVersion().strip()).matches()) // Compare against Regex
            return new Test(false, "Version is Not Complete: '", c.getVersion(), "'."); // Test failed
        return new Test(true, "Version is Complete."); // Test passed
    }

    /**
     * Helper method that tests each CPE identifier string in the component to make sure it follows the format
     * of CPE v2.3. It includes in the test message how many CPE strings had an invalid format.
     *
     * @param c The component to test the CPEs of
     * @return A single Test instance, describing if the test passed or failed and how many CPEs were invalid.
     */
    private Test testCPEs(Component c) {
        // Check CPEs and return a number of invalid CPEs per component
        final int invalid = getNumInvalidStrings(c.getCpes(), cpe23Regex);
        if (invalid > 0) // If there are invalid CPEs, mark as failed
            return new Test(false, "Had ", Integer.toString(invalid), " CPE(s) with Invalid Format.");
        return new Test(true, "CPE(s) have Valid Format."); // Test passed
    }

    /**
     * Helper method that tests each PURL string in the component to make sure it follows the official PURL
     * specification. It includes in the test message how many PURL strings had an invalid format.
     *
     * @param c The component to test the PURLs of
     * @return A single Test instance, describing if the test passed or failed and how many PURLs were invalid.
     */
    private Test testPURLs(Component c) {
        // Check PURLs and return a number of invalid PURLs
        Set<String> purlStrings = new HashSet<>();
        for (PURL p: c.getPurls()) {purlStrings.add(p.toString());}
        final int invalid = getNumInvalidStrings(purlStrings, purlRegex);
        if (invalid > 0) // If there are invalid PURLs, mark as failed
            return new Test(false, "Had ", Integer.toString(invalid), " PURL(s) with Invalid Format.");
        return new Test(true, "PURL(s) have Valid Format."); // Test passed
    }

    /**
     * Private helper method to get a number of strings in a set that do not match a given regex.
     *
     * @param strings The set of strings to match with the regex
     * @param regex   The regex to match
     * @return The number of strings that do not match the regex
     */
    private int getNumInvalidStrings(Set<String> strings, Pattern regex) {
        int stringCounter = 0;

        for (String s : strings) { // Loop through all strings and match regex
            if (s != null &&
                    !regex.matcher(s.strip()).matches())
                stringCounter++;
        }
        return stringCounter;
    }
}