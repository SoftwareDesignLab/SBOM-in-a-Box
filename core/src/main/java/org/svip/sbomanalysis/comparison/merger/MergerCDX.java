package org.svip.sbomanalysis.comparison.merger;

import org.svip.sbom.builder.objects.schemas.CDX14.CDX14Builder;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;

import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class MergerCDX extends Merger {

    public MergerCDX(CDX14SBOM A, CDX14SBOM B) {
        super(A, B);
    }

    @Override
    protected SBOM mergeSBOM(SBOM A, SBOM B) {

        Set<CDX14ComponentObject> componentsA = Collections.singleton((CDX14ComponentObject) A.getComponents());
        Set<CDX14ComponentObject> componentsB = Collections.singleton((CDX14ComponentObject) A.getComponents());

        // declare SBOM A as the main SBOM, cast it back to CDX14SBOM
        CDX14SBOM mainSBOM = (CDX14SBOM) A;

        // Create a new builder for the new SBOM
        CDX14Builder builder = new CDX14Builder();

        /* Assign all top level data for the new SBOM */

        // Format
        builder.setFormat(mainSBOM.getFormat());

        // Name
        builder.setName(mainSBOM.getName());

        // UID (In this case, bom-ref)
        builder.setUID(mainSBOM.getUID());

        // SBOM Version
        builder.setVersion(mainSBOM.getVersion());

        // Spec Version (1.4)
        builder.setSpecVersion(mainSBOM.getSpecVersion());

        // Licenses
        for(String license : mainSBOM.getLicenses()) { builder.addLicense(license); }

        // Creation Data
        if(!A.getCreationData().equals(null) && !B.getCreationData().equals(null)) {
            builder.setCreationData(mergeCreationData(A.getCreationData(), B.getCreationData())); /** A new object should be created for this. Will do later.**/
        } else if (!A.getCreationData().equals(null)) {
            builder.setCreationData(A.getCreationData());
        } else if (!B.getCreationData().equals(null)) {
            builder.setCreationData(B.getCreationData());
        } else { builder.setCreationData(null); }

        // Document Comment
        builder.setDocumentComment(mainSBOM.getDocumentComment());

        // Root Component
        builder.setRootComponent(mainSBOM.getRootComponent());


        return builder.Build();

    }

    @Override
    protected Set<Component> mergeComponents(Set<Component> A, Set<Component> B) {
        return null;
    }

    @Override
    protected Component mergeComponent(Component A, Component B) {
        return null;
    }

    @Override
    protected CreationData mergeCreationData(CreationData A, CreationData B) {

        CreationData mergedCreationData = new CreationData();

        // Create a new timestamp and add it to the new SBOM, rather than using one of previous timestamps
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        mergedCreationData.setCreationTime(timeFormat.format(timestamp));

        // Authors
        Set<Contact> authorsA = A.getAuthors();
        Set<Contact> authorsB = B.getAuthors();
        Set<Contact> authorsNew = new HashSet<>();

        // Cycle through each primary SBOM author
        for(Contact authorA : authorsA) {
            // Cycle through all authors from sbom B and compare them against current primary SBOM author
            for(Contact authorB : authorsB) {
                if(
                        authorA.getName().contains(authorB.getName()) &&
                        authorA.getEmail().contains(authorB.getEmail()) &&
                        authorA.getPhone().contains(authorB.getPhone())
                ) {
                    // If a duplicate is found remove it from second SBOM authors
                    authorsB.remove(authorB);
                }
            }
            // After duplicates have been removed, add the author from the primary SBOM in
            authorsNew.add(authorA);
        }
        // Add all remaining authors from second SBOM into new authors
        for(Contact authorB : authorsB) {
            authorsNew.add(authorB);
        }


        return mergedCreationData;
    }

}
