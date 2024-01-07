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

package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.svip.api.services.SBOMFileService;
import org.svip.metrics.pipelines.QualityReport;
import org.svip.repair.fix.Fix;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REST API Controller for managing SBOM repair operations
 *
 * @author Juan Francisco Patino
 **/
@RestController
@RequestMapping("/svip")
public class RepairController {

    /**
     * Spring-configured logger
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(SBOMController.class);

    private final SBOMFileService sbomService;

    /**
     * Create new Controller with services
     *
     * @param sbomService Service for handling SBOM queries
     */
    public RepairController(SBOMFileService sbomService) {
        this.sbomService = sbomService;
    }


    ///
    /// POST
    ///

    /**
     * USAGE. Send GET request to /sboms/repair/statements with a URL parameter id to get repair statement.
     *
     * @param id id of SBOM to repair, if needed
     * @return map of repair fixes as a repair statement
     */
    @GetMapping("/sboms/repair/statement")
    public ResponseEntity<?> repairStatement(@RequestParam("id") long id) {

        QualityReport repairStatement;
        try {
            repairStatement = sbomService.getRepairStatement(id);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (repairStatement == null)
            return new ResponseEntity<>("GET /svip/sboms/content?id=" + id + " Cannot find SBOM of id " + id,
                    HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(repairStatement, HttpStatus.OK);

    }

    /**
     * USAGE. Send GET request to /sboms/repair to repair a selected SBOM
     *
     * @param id              id of SBOM to repair
     * @param repairStatement repair statement to use
     * @param overwrite       whether to overwrite existing SBOM
     * @return id of repaired SBOM
     */
    @GetMapping("/sboms/repair")
    public ResponseEntity<?> repairSBOM(@RequestParam("id") long id,
                                        @RequestParam("repairStatement") String repairStatementJson,
                                        @RequestParam("overwrite") boolean overwrite) {

        long repair;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<Map<Integer, Set<Fix<?>>>> mapType = new TypeReference<>() {};
            Map<Integer, Set<Fix<?>>> repairStatement = objectMapper.readValue(repairStatementJson, mapType);
            repair = sbomService.repair(id, repairStatement, overwrite);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (repair == 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);


        return new ResponseEntity<>(repair, HttpStatus.OK);

    }


}
