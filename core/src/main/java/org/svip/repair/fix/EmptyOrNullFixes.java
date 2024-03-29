/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
 */

package org.svip.repair.fix;

import org.svip.generation.parsers.utils.QueryWorker;
import org.svip.metrics.resultfactory.Result;
import org.svip.repair.extraction.Extraction;
import org.svip.repair.extraction.NugetExtraction;
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
import java.util.*;
import java.util.stream.Stream;

/**
 * Fixes class to generate suggested repairs for NULL attributes of a component
 */
public class EmptyOrNullFixes implements Fixes {

    // threads to query potential purls from Googl
    protected final ArrayList<QueryWorker> queryWorkers;

    public EmptyOrNullFixes(ArrayList<QueryWorker> queryWorkers) {
        this.queryWorkers = queryWorkers;
    }

    @Override
    public List<Fix<?>> fix(Result result, SBOM sbom, String componentName, Integer componentHashCode) throws Exception {

        // Depending on the type of fix, call the correct fix method
        if (result.getDetails().contains("Bom Version was a null value"))
            return bomVersionFix(sbom);
        else if (result.getDetails().contains("Bom-Ref")) // i.e., UID
            return bomRefFix(sbom, componentHashCode);
        else if (result.getDetails().contains("Creation Data"))
            return creationDataFix(result.getDetails());
        else if (result.getDetails().contains("SPDXID"))
            return SPDXIDFix(result);
        else if (result.getDetails().contains("Comment"))
            return commentNullFix(result.getMessage());
        else if (result.getDetails().contains("Attribution Text"))
            return attributionTextNullFix(result.getMessage());
        else if (result.getDetails().contains("File Notice"))
            return fileNoticeNullFix(result.getMessage());
        else if (result.getDetails().contains("Author"))
            return authorNullFix(sbom, componentHashCode, componentName);
        else if (result.getDetails().contains("Copyright"))
            return copyrightNullFix(sbom, componentHashCode);

        return null;

    }

    /**
     * @param sbom sbom
     * @return potential fixes for bom version
     */
    private List<Fix<?>> bomVersionFix(SBOM sbom) {

        if (sbom instanceof SPDX23SBOM)
            return Collections.singletonList(new Fix<>(FixType.METADATA_BOM_VERSION, "", "2.3"));
        else if (sbom instanceof CDX14SBOM)
            return Collections.singletonList(new Fix<>(FixType.METADATA_BOM_VERSION, "", "1.4"));
        else if (sbom instanceof SVIPSBOM)
            return Collections.singletonList(new Fix<>(FixType.METADATA_BOM_VERSION, "", "1.04a"));
        else
            return new ArrayList<>(List.of(new Fix<>(FixType.METADATA_BOM_VERSION, "", "2.3"), new Fix<>(FixType.METADATA_BOM_VERSION, "", "1.4"),
                    new Fix<>(FixType.METADATA_BOM_VERSION, "", "1.04a"))); // todo check 1.04a is current SVIP bomVersion

    }

    /**
     * @return a list of potential fixes for a null bom-ref
     */
    private List<Fix<?>> bomRefFix(SBOM sbom, Integer hashCode) {

        List<Component> filtered = sbom.getComponents().stream().filter(
                comp -> comp.hashCode() == hashCode).toList();

        if(filtered.size() == 0)
            return null;

        Component comp = filtered.get(0);

        // Get the subtype hex
        String subTypeHex = Integer.toHexString(hashCode).toLowerCase();

        // Create a new list of fixes and add a fix for the bom ref using the hex
        List<Fix<?>> result = new ArrayList<>();
        result.add(new Fix<>(FixType.COMPONENT_BOM_REF, sbom.getUID(), subTypeHex)); // hexadecimal hash of subtype of component
        result.add(new Fix<>(FixType.COMPONENT_BOM_REF, sbom.getUID(), "pkg:/" + comp.getName() + "-" + subTypeHex));

        // Return the fix
        return result;

    }

    /**
     * Creation date is the only fixable attribute for creation data
     *
     * @return potential fixes for creation data
     */
    private List<Fix<?>> creationDataFix(String details) {

        String[] split = details.split(" ");

        //We only fix creation data at the current time
        if(!split[split.length].equals("Manufacture"))
            return new ArrayList<Fix<?>>();

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
        return Collections.singletonList(new Fix<>(FixType.METADATA_CREATION_DATA, "", "\"created\" : .\"" + dateAndTime + "\""));

    }

    /**
     * Fixes SPDXID
     *
     * @param result failed test result
     * @return fix for SPDXID
     */
    private List<Fix<?>> SPDXIDFix(Result result) {
        return Collections.singletonList(new Fix<>(FixType.METADATA_SPDXID, result.getMessage(), "SPDXRef-DOCUMENT"));
    }

    /**
     * @return empty string in place for null comment
     */
    private List<Fix<?>> commentNullFix(String message) {

        String[] split = message.split(" ");

        if(split[3].equals("null"))
            return Collections.singletonList(new Fix<>(FixType.COMPONENT_COMMENT, "null", ""));

        return null;
    }

    /**
     * @return empty string in place for null attribution text
     */
    private List<Fix<?>> attributionTextNullFix(String message) {

        String[] split = message.split(" ");

        if(split[4].equals("null"))
            return Collections.singletonList(new Fix<>(FixType.COMPONENT_ATTRIBUTION_TEXT, null, ""));

        return null;
    }

    /**
     * @return empty string in place for null file notice
     */
    private List<Fix<?>> fileNoticeNullFix(String message) {
        String[] split = message.split(" ");

        if(split[4].equals("null"))
            return Collections.singletonList(new Fix<>(FixType.COMPONENT_FILE_NOTICE, null, ""));

        return null;
    }

    /**
     * Fixes empty copyright
     * @param sbom The SBOM to fix
     * @param componentHash The component that is missing copyright
     * @return a list of potential fixes or null
     */
    private List<Fix<?>> copyrightNullFix(SBOM sbom, Integer componentHash) throws Exception {
        List<Component> filtered = sbom.getComponents().stream().filter(
                comp -> comp.hashCode() == componentHash).toList();

        if(filtered.size() == 0)
            return null;

        Component comp = filtered.get(0);
        Set<String> purls = null;

        if(comp instanceof SPDX23Package spdx23Package) {
            purls = spdx23Package.getPURLs();
        }

        else if(comp instanceof CDX14Package cdx14Package) {
            purls = cdx14Package.getPURLs();
        }

        if(purls == null || purls.isEmpty())
            return null;

        List<Fix<?>> fixes = new ArrayList<>();

        for(String purlString : purls) {

            PURL p = new PURL(purlString);

            String type = p.getType();

            if(type != null) {
                Extraction ex = null;

                switch(type) {
                    case "nuget":
                        ex = new NugetExtraction(p);
                        break;
                }

                if(ex == null)
                    continue;

                ex.extract();

                if(ex.getCopyright() != null) {
                    fixes.add(new Fix<>(FixType.COMPONENT_COPYRIGHT, "null", ex.getCopyright()));
                }
            }
        }

        if(fixes.size() > 0)
            return fixes;

        return null;

    }

    /**
     * @param sbom          sbom
     * @param repairSubType the key most closely relating to the component
     * @return a list of potential authors
     */
    private List<Fix<?>> authorNullFix(SBOM sbom, Integer componentHash, String repairSubType) throws Exception {

        List<Component> filtered = sbom.getComponents().stream().filter(
                comp -> comp.hashCode() == componentHash).toList();

        if(filtered.size() == 0 || repairSubType == null)
            return null;

        Component component = filtered.get(0);

        // if this is an SPDX 2.3 file object
        if (component instanceof SPDX23FileObject spdx23FileObject) {
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
                        fixes.add(new Fix<>(FixType.COMPONENT_AUTHOR, "null", author));
                    }

                    // Reutnr the fixes
                    return fixes;
                }

                // If no fixes were returned, return null
                return null;

            }

        }



        // Create a new set of purls
        Set<String> purls = null;

        // And if it's an SPDX 2.3 package
        if (component instanceof SPDX23PackageObject spdx23PackageObject) {
            // Get the package's PURLs
            purls = spdx23PackageObject.getPURLs();
        }
        // Or, if it is a CycloneDX 1.4 Package
        else if (component instanceof CDX14Package cdx14Package) {
            // Get the package's PURLs
            purls = cdx14Package.getPURLs();
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
                            fixList.add(new Fix<>(FixType.COMPONENT_AUTHOR, "null", split2[0] + " (POTENTIAL AUTHOR/UNCONFIRMED)"));

                        }

                    }

                }

            }
        });

        // Return the fix list
        return fixList;

    }

}
