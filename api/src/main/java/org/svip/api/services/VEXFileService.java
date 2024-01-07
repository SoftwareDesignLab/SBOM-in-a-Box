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

package org.svip.api.services;

import org.springframework.stereotype.Service;
import org.svip.api.entities.VEXFile;
import org.svip.api.repository.VEXFileRepository;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.vex.VEXResult;
import org.svip.vex.database.NVDClient;
import org.svip.vex.database.OSVClient;
import org.svip.vex.database.interfaces.VulnerabilityDBClient;
import org.svip.vex.model.VEX;
import org.svip.vex.model.VEXType;
import org.svip.vex.vexstatement.VEXStatement;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Optional;

/**
 * File: VEXFileService.java
 * Business logic for accessing the VEX File table
 *
 * @author Derek Garcia
 **/
@Service
public class VEXFileService {


    private final VEXFileRepository vexFileRepository;

    /**
     * Create new Service for a target repository
     *
     * @param vexFileRepository VEX repository to access
     */
    public VEXFileService(VEXFileRepository vexFileRepository){
        this.vexFileRepository = vexFileRepository;
    }


    /**
     * Create a new vex entry in the database
     *
     * @param vf VEX to upload
     * @return uploaded VEX entry
     * @throws Exception Error uploading to the Database
     */
     public VEXFile upload(VEXFile vf) throws Exception {
        try{
            return this.vexFileRepository.save(vf);
        } catch (Exception e){
            // todo custom exception instead of generic
            throw new Exception("Failed to upload to Database: " + e.getMessage());
        }
    }

    /**
     * Generate VEX for a given SBOM
     *
     * @param sbom SBOM to generate VEX from
     * @param client Vulnerability datasource to use
     * @param format VEX Schema to use
     * @param apiKey Optional API key
     * @return VEXResult with VEX and any errors
     * @throws Exception Failed to generate VEX
     */
    public VEXResult generateVEX(SBOM sbom, String client, String format, String apiKey) throws Exception {

        VulnerabilityDBClient vc;
        // check that user entered a valid database client
        switch (client.toLowerCase()) {
            case "osv" -> vc = new OSVClient();
            case "nvd" -> vc = new NVDClient();
            // todo custom error
            default -> throw new Exception(client + " is an unsupported vulnerability database");
        }

        // create new VEX builder and add sbom fields
        VEX.Builder vb = new VEX.Builder();
        String creationTime = String.valueOf(java.time.LocalDateTime.now());
        vb.setVEXIdentifier(sbom.getName());
        vb.setDocVersion("1.0");
        vb.setTimeFirstIssued(creationTime);
        vb.setTimeLastUpdated(creationTime);

        // check that user entered a valid format
        switch (format.toLowerCase()) {
            case "cyclonedx" -> {
                vb.setOriginType(VEXType.CYCLONE_DX);
                vb.setSpecVersion("1.4");
            }
            case "csaf" -> {
                vb.setOriginType(VEXType.CSAF);
                vb.setSpecVersion("2.0");
            }
            // todo custom error
            default -> throw new Exception(format + " is an unsupported VEX schema");
        }

        HashMap<String, String> error = new HashMap<>();
        // add vex statements and/or errors for every component
        for (Component c : sbom.getComponents()) {
            if (c instanceof SBOMPackage) {
                try {
                    List<VEXStatement> statements;
                    if (client.equalsIgnoreCase("nvd") && apiKey != null)
                        statements = vc.getVEXStatements((SBOMPackage) c, apiKey);
                    else
                        statements = vc.getVEXStatements((SBOMPackage) c);

                    if (!statements.isEmpty())
                        for (VEXStatement vs : statements)
                            vb.addVEXStatement(vs);
                } catch (Exception e) {
                    error.put(c.getName(), e.getMessage());
                }
            }
        }

        VEX vex = vb.build();

        // Return VEXResult
        return new VEXResult(vex, error);
    }

}
