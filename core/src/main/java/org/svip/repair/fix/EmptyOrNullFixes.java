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
    public List<Fix<?>> fix(Result result, SBOM sbom, String repairSubType) {

        if (result.getDetails().contains("Bom Version was a null value"))
            return bomVersionFix(sbom);
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
     * Creation date is the only fixable attribute for creation data
     *
     * @return potential fixes for creation data
     */
    private List<Fix<?>> creationDataFix() {

        String dateAndTime;
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatterLocalDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dateAndTime = formatterLocalDate.format(localDate);
        LocalTime localTime = LocalTime.now();
        DateTimeFormatter formatterLocalTime = DateTimeFormatter.ofPattern("HH:mm:ss");
        dateAndTime += "T" + formatterLocalTime.format(localTime) + localTime.atOffset(ZoneOffset.UTC);

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
    private List<Fix<?>> authorNullFix(SBOM sbom, String repairSubType) {

        Component thisComponent = null;

        for (Component component : sbom.getComponents()
        ) {

            if (component instanceof SPDX23Package spdx23Package) {
                if (spdx23Package.getName().toLowerCase().contains(repairSubType.toLowerCase())
                        || repairSubType.toLowerCase().contains(spdx23Package.getName().toLowerCase())) {
                    thisComponent = component;
                    break;
                }
            }
            // if this is a file object, the author is the same as the SBOM's author
            else if (component instanceof SPDX23FileObject spdx23FileObject) {
                if (spdx23FileObject.getName().toLowerCase().contains(repairSubType.toLowerCase())
                        || repairSubType.toLowerCase().contains(spdx23FileObject.getName().toLowerCase())) {
                    if (sbom.getCreationData().getAuthors() != null) {
                        Set<Contact> potentialAuthors = sbom.getCreationData().getAuthors();
                        List<Fix<?>> fixes = new ArrayList<>();
                        for (Contact author : potentialAuthors
                        ) {
                            fixes.add(new Fix<>("null", author));
                        }
                        return fixes;
                    }

                    return null;

                }
            } else if (component instanceof CDX14Package cdx14Package) {
                if (cdx14Package.getName().toLowerCase().contains(repairSubType.toLowerCase())
                        || repairSubType.toLowerCase().contains(cdx14Package.getName().toLowerCase())) {
                    thisComponent = component;
                    break;
                }
            }

        }

        Object[] purls = null;

        if (thisComponent != null) {
            if (thisComponent instanceof SPDX23PackageObject spdx23PackageObject) {

                purls = spdx23PackageObject.getPURLs().toArray();

            } else if (thisComponent instanceof CDX14Package cdx14Package) {

                purls = cdx14Package.getPURLs().toArray();

            }

        }

        List<PURL> purlList = new ArrayList<>();
        if (purls != null)
            for (Object purl : purls
            )
                purlList.add((PURL) purl);

        if (purlList.isEmpty())
            return googlePotentialAuthors(repairSubType, "");

        List<Fix<?>> fixList = new ArrayList<>();

        for (PURL purl : purlList
        ) {

            fixList.addAll(googlePotentialAuthors(repairSubType, purl.getName()));

        }

        if (!fixList.isEmpty())
            return fixList;

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

        List<Fix<?>> fixList = new ArrayList<>();

        this.queryWorkers.add(new QueryWorker(new SVIPComponentBuilder(), "https://google.com/search?q="
                + repairSubType + " author" + ((purlName.isEmpty()) ? "" : " " + purlName)) { // simply google for a potential author
            @Override
            public void run() {
                // Get page contents
                final String contents = getUrlContents(queryURL(this.url, false));

                if (!contents.isEmpty()) {

                    String[] split = contents.split(" ");

                    for (String s : split
                    ) {

                        if (s.contains("mailto:")) {
                            String[] split1 = s.split("mailto:");
                            String[] split2 = split1[1].split("\"");
                            fixList.add(new Fix<>("null", split2[0]));
                        }

                    }

                }

            }
        });

        return fixList;

    }


}
