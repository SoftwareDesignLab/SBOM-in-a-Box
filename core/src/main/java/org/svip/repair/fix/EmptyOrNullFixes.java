package org.svip.repair.fix;

import org.svip.generation.parsers.utils.QueryWorker;
import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23FileObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.uids.PURL;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Fixes class to generate suggested repairs for NULL attributes of a component
 */
public class EmptyOrNullFixes implements Fixes {

    // threads to query potential purls from Googl
    protected final ArrayList<QueryWorker> queryWorkers;
    private final List<Fix<?>> emptyString = Collections.singletonList(new Fix<>("null", ""));

    public EmptyOrNullFixes(ArrayList<QueryWorker> queryWorkers) {
        this.queryWorkers = queryWorkers;
    }

    @Override
    public List<Fix<?>> fix(Result result, SBOM sbom, String repairSubType) throws Exception {

        // Depending on the type of fix, call the correct fix method
        if (result.getDetails().contains("Bom Version was a null value"))
            return bomVersionFix(sbom);
        else if (result.getDetails().contains("Bom-Ref")) // i.e., UID
            return bomRefFix(repairSubType);
        else if (result.getDetails().contains("Creation Data"))
            return creationDataFix();
        else if (result.getDetails().contains("SPDXID"))
            return SPDXIDFix(result);
        else if (result.getDetails().contains("Comment"))
            return commentNullFix();
        else if (result.getDetails().contains("Attribution Text"))
            return attributionTextNullFix();
        else if (result.getDetails().contains("File Notice"))
            return fileNoticeNullFix();
        else if (result.getDetails().contains("Author"))
            return authorNullFix(sbom, repairSubType);
        else if (result.getDetails().contains("Copyright"))
            return copyrightNullFix();

        return null;

    }

    /**
     * @param sbom sbom
     * @return potential fixes for bom version
     */
    private List<Fix<?>> bomVersionFix(SBOM sbom) {

        if (sbom instanceof SPDX23SBOM)
            return Collections.singletonList(new Fix<>("", "2.3"));
        else if (sbom instanceof CDX14SBOM)
            return Collections.singletonList(new Fix<>("", "1.4"));
        else if (sbom instanceof SVIPSBOM)
            return Collections.singletonList(new Fix<>("", "1.04a"));
        else
            return new ArrayList<>(List.of(new Fix<>("", "2.3"), new Fix<>("", "1.4"),
                    new Fix<>("", "1.04a"))); // todo check 1.04a is current SVIP bomVersion

    }

    /**
     * @return a list of potential fixes for a null bom-ref
     */
    private List<Fix<?>> bomRefFix(String subType) {

        // Get the subtype hex
        String subTypeHex = Integer.toHexString(subType.hashCode()).toLowerCase();

        // Create a new list of fixes and add a fix for the bom ref using the hex
        List<Fix<?>> result = new ArrayList<>();
        result.add(emptyString.get(0));
        result.add(new Fix<>(null, subTypeHex)); // hexadecimal hash of subtype of component
        result.add(new Fix<>(null, "pkg:/" + subType + "-" + subTypeHex));

        // Return the fix
        return result;

    }

    /**
     * Creation date is the only fixable attribute for creation data
     *
     * @return potential fixes for creation data
     */
    private List<Fix<?>> creationDataFix() {

        // Create a new date and time string
        String dateAndTime;

        // Get the current date and format it
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatterLocalDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dateAndTime = formatterLocalDate.format(localDate);

        // Get the current time and format it
        LocalTime localTime = LocalTime.now();
        DateTimeFormatter formatterLocalTime = DateTimeFormatter.ofPattern("HH:mm:ss");
        dateAndTime += "T" + formatterLocalTime.format(localTime) + localTime.atOffset(ZoneOffset.UTC);

        // Add the new date and time as a fix
        return Collections.singletonList(new Fix<>("", "\"created\" : .\"" + dateAndTime + "\""));

    }

    /**
     * Fixes SPDXID
     *
     * @param result failed test result
     * @return fix for SPDXID
     */
    private List<Fix<?>> SPDXIDFix(Result result) {
        return Collections.singletonList(new Fix<>(result.getMessage(), "SPDXRef-DOCUMENT"));
    }

    /**
     * @return empty string in place for null comment
     */
    private List<Fix<?>> commentNullFix() {
        return Collections.singletonList(new Fix<>("null", ""));
    }

    /**
     * @return empty string in place for null attribution text
     */
    private List<Fix<?>> attributionTextNullFix() {
        return emptyString;
    }

    /**
     * @return empty string in place for null file notice
     */
    private List<Fix<?>> fileNoticeNullFix() {
        return emptyString;
    }

    /**
     * @return empty string in place for null copyright
     */
    private List<Fix<?>> copyrightNullFix() {
        return emptyString;
    }

    /**
     * @param sbom          sbom
     * @param repairSubType the key most closely relating to the component
     * @return a list of potential authors
     */
    private List<Fix<?>> authorNullFix(SBOM sbom, String repairSubType) throws Exception {

        Component thisComponent = null;

        // For each component
        for (Component component : sbom.getComponents()
        ) {

            // If it is an SPDX 2.3 Package
            if (component instanceof SPDX23Package spdx23Package) {

                // And if the name contains the repair SubType, or it contains the repair SubType contains the name
                if (spdx23Package.getName().toLowerCase().contains(repairSubType.toLowerCase())
                        || repairSubType.toLowerCase().contains(spdx23Package.getName().toLowerCase())) {

                    // Make the current component "this" component and break
                    thisComponent = component;
                    break;

                }

            }

            // if this is an SPDX 2.3 file object
            else if (component instanceof SPDX23FileObject spdx23FileObject) {

                // And the repair subtype is the same as the file name
                if (spdx23FileObject.getName().toLowerCase().contains(repairSubType.toLowerCase())
                        || repairSubType.toLowerCase().contains(spdx23FileObject.getName().toLowerCase())) {

                    // If the authors are null
                    if (sbom.getCreationData().getAuthors() != null) {

                        // Get a set of potential authors
                        Set<Contact> potentialAuthors = sbom.getCreationData().getAuthors();

                        // Create a new list of fixes
                        List<Fix<?>> fixes = new ArrayList<>();

                        // Then iterate through the potential authors and make a fix for each
                        for (Contact author : potentialAuthors
                        ) {
                            fixes.add(new Fix<>("null", author));
                        }

                        // Reutnr the fixes
                        return fixes;
                    }

                    // If no fixes were returned, return null
                    return null;

                }

            }
            // Else, if the component is a CycloneDX 1.4 Package
            else if (component instanceof CDX14Package cdx14Package) {

                // And if the name is the same as the repair sub type
                if (cdx14Package.getName().toLowerCase().contains(repairSubType.toLowerCase())
                        || repairSubType.toLowerCase().contains(cdx14Package.getName().toLowerCase())) {

                    // Set the current component to "this" component and break
                    thisComponent = component;
                    break;

                }

            }

        }

        // Create a new set of purls
        Set<String> purls = null;

        // If this component isn't null
        if (thisComponent != null) {

            // And if it's an SPDX 2.3 package
            if (thisComponent instanceof SPDX23PackageObject spdx23PackageObject) {

                // Get the package's PURLs
                purls = spdx23PackageObject.getPURLs();

            }
            // Or, if it is a CycloneDX 1.4 Package
            else if (thisComponent instanceof CDX14Package cdx14Package) {

                // Get the package's PURLs
                purls = cdx14Package.getPURLs();

            }

        }

        // Create a new list of PURLs
        List<PURL> purlList = new ArrayList<>();

        // If the previously pulled pearls from the package is not null
        if (purls != null)

            // Iterate through them and add them to the new Purl list
            for (String purl : purls) purlList.add(new PURL(purl));

        // If the Purl list is empty, google the potential authors with an empty purl and return them
        if (purlList.isEmpty()) return googlePotentialAuthors(repairSubType, "");

        List<Fix<?>> fixList = new ArrayList<>();

        // For each PURL in the Purl list
        for (PURL purl : purlList) {

            // Add fixes by googling the potential authors for that PURL
            fixList.addAll(googlePotentialAuthors(repairSubType, purl.getName()));

        }

        // If the fix list is not empty, return it
        if (!fixList.isEmpty()) return fixList;

        return null; // we tried our best

    }

    /**
     * Queries a Google search, looks for potential authors in the first page of results
     *
     * @param repairSubType the name of the component, usually
     * @param purlName      the name of the purl (optional)
     * @return a list of potential authors
     */
    private List<Fix<?>> googlePotentialAuthors(String repairSubType, String purlName) {

        // Create a new fix list
        List<Fix<?>> fixList = new ArrayList<>();

        // Get the Query Workers List and establish a new Query Worker to search for the PURL's author
        this.queryWorkers.add(new QueryWorker(new SVIPComponentBuilder(), "https://google.com/search?q="
                + repairSubType + " author" + ((purlName.isEmpty()) ? "" : " " + purlName)) { // simply google for a potential author
            @Override
            public void run() {

                // Get page contents
                final String contents = getUrlContents(queryURL(this.url, false));

                // If there are contents
                if (!contents.isEmpty()) {

                    // Split the contents
                    String[] split = contents.split(" ");

                    // For each element in the contents
                    for (String s : split
                    ) {

                        // If an email is found
                        if (s.contains("mailto:")) {

                            //Split it
                            String[] split1 = s.split("mailto:");
                            String[] split2 = split1[1].split("\"");

                            // Then add that email as a potential fix
                            fixList.add(new Fix<>("null", split2[0] + " (POTENTIAL AUTHOR/UNCONFIRMED)"));

                        }

                    }

                }

            }
        });

        // Return the fix list
        return fixList;

    }

}