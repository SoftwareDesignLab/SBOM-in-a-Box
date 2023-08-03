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

    public SBOMService sbomService;


    public SBOMController(SBOMService sbomService){
        this.sbomService = sbomService;
    }

    @GetMapping("/sboms")
    public ResponseEntity<String> getAllIds(){
        return new ResponseEntity<>("Pong!", HttpStatus.OK);
    }

}
