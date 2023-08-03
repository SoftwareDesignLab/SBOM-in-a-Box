package org.svip.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.svip.api.services.SBOMService;

/**
 * @author Derek Garcia
 **/
@RestController
@RequestMapping("/svip")
public class SBOMController {

    private final SBOMService sbomService;


    public SBOMController(SBOMService sbomService){
        this.sbomService = sbomService;
    }

    @GetMapping("/sboms")
    public ResponseEntity<Long[]> getAllIds(){

        Long[] ids = this.sbomService.getAllIDs();

        if(ids.length == 0)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(ids, HttpStatus.OK);
    }

}
