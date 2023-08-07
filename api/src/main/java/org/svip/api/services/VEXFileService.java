package org.svip.api.services;

import org.springframework.stereotype.Service;

/**
 * Business logic for accessing the VEX File table
 *
 * @author Derek Garcia
 **/
@Service
public class VEXFileService {

//    SBOM sbom;
//        Deserializer d;
//
//        // Get the SBOM to be tested
//        Optional<SBOMFile> sbomFile = sbomFileRepository.findById(id);
//
//        // Check if it exists
//        if (sbomFile.isEmpty()) {
//            LOGGER.info("VEX /svip/sboms/vex?id=" + id + " - FILE NOT FOUND");
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//        // Deserialize SBOM into JSON object
//        try {
//            d = SerializerFactory.createDeserializer(sbomFile.get().getContents());
//            sbom = d.readFromString(sbomFile.get().getContents());
//        } catch (JsonProcessingException e) {
//            LOGGER.info("Failed to deserialize SBOM content, may be an unsupported format");
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        } catch (Exception e) {
//            LOGGER.info("Deserialization Error");
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        VulnerabilityDBClient vc;
//        // check that user entered a valid database client
//        switch (client.toLowerCase()) {
//            case "osv" -> vc = new OSVClient();
//            case "nvd" -> vc = new NVDClient();
//            default -> {
//                LOGGER.info("VEX /svip/sboms/vex?client=" + client + " - INVALID CLIENT");
//                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//            }
//        }
//
//        // create new VEX builder and add sbom fields
//        VEX.Builder vb = new VEX.Builder();
//        String creationTime = String.valueOf(java.time.LocalDateTime.now());
//        vb.setVEXIdentifier(sbom.getName());
//        vb.setDocVersion("1.0");
//        vb.setTimeFirstIssued(creationTime);
//        vb.setTimeLastUpdated(creationTime);
//
//        // check that user entered a valid format
//        switch (format.toLowerCase()) {
//            case "cyclonedx" -> {
//                vb.setOriginType(VEXType.CYCLONE_DX);
//                vb.setSpecVersion("1.4");
//            }
//            case "csaf" -> {
//                vb.setOriginType(VEXType.CSAF);
//                vb.setSpecVersion("2.0");
//            }
//            default -> {
//                LOGGER.info("VEX /svip/sboms/vex?format=" + format + " - INVALID VEX FORMAT");
//                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//            }
//        }
//
//        HashMap<String, String> error = new HashMap<>();
//        // add vex statements and/or errors for every component
//        for (Component c : sbom.getComponents()) {
//            if (c instanceof SBOMPackage) {
//                try {
//                    List<VEXStatement> statements;
//                    if (client.equalsIgnoreCase("nvd") && apiKey != null)
//                        statements = vc.getVEXStatements((SBOMPackage) c, apiKey);
//                    else
//                        statements = vc.getVEXStatements((SBOMPackage) c);
//
//                    if (!statements.isEmpty())
//                        for (VEXStatement vs : statements)
//                            vb.addVEXStatement(vs);
//                } catch (Exception e) {
//                    error.put(c.getName(), e.getMessage());
//                }
//            }
//        }
//
//        VEX vex = vb.build();
//
//        // Log
//        LOGGER.info("VEX /svip/sboms/vex?id=" + id + " - VEX CREATED: " + sbomFile.get().getFileName());
//
//        // Return VEXResult
//        return new ResponseEntity<>(
//                new VEXResult(vex, error), HttpStatus.OK);
}
