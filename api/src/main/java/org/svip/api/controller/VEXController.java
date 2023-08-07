package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.svip.api.entities.SBOMFile;
import org.svip.api.services.SBOMFileService;
import org.svip.api.services.VEXFileService;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.deserializer.Deserializer;
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

/**
 * REST API Controller for generating VEX
 *
 * @author Derek Garcia
 **/
@RestController
@RequestMapping("/svip")
public class VEXController {

    private final VEXFileService vexFileService;
    private final SBOMFileService sbomFileService;

    public VEXController(VEXFileService vexFileService, SBOMFileService sbomFileService){
        this.vexFileService = vexFileService;
        this.sbomFileService = sbomFileService;
    }

    /**
     * USAGE Send GET request to /vex to generate a VEX Document for an SBOM
     * The API will respond with an HTTP 200 a VEX object, and a hashmap of
     * and errors that occurred
     *
     * @param id     The id of the SBOM contents to retrieve.
     * @param format the format of teh VEX Document
     * @param client the api client to use (currently NVD or OSV)
     * @return A new VEXResult of the VEX document and any errors that occurred
     */
    @GetMapping("/sboms/vex")
    public ResponseEntity<VEXResult> vex(@RequestHeader(value = "apiKey", required = false) String apiKey,
                                         @RequestParam("id") long id,
                                         @RequestParam("format") String format,
                                         @RequestParam("client") String client) {

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }
}
