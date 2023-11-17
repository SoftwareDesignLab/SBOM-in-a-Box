package org.svip.api.services;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.svip.generation.osi.OSIClient;
import org.svip.generation.osi.exceptions.DockerNotAvailableException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.svip.generation.osi.OSIClient.isOSIContainerAvailable;

@Service
public class OSIService {

    private OSIClient osiClient = null;


    public OSIService(){
        this.osiClient = new OSIClient();
    }


    public List<String> getTools(String listTypeArg) {
        return new ArrayList<>();
    }


    public void addProject(ZipInputStream inputStream){

    }

    public Map<String, String> generateSBOMs(List<String> toolNames){
        return new HashMap<>();
    }

    public boolean isEnabled(){
        return this.osiClient != null;
    }


}
