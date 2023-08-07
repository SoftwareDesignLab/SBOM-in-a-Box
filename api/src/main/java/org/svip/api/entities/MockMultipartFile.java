package org.svip.api.entities;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * Implementation of the MultipartFile interface used for unit testing with binary/non encoded files
 *
 * @author Juan Francisco Patino
 */
public class MockMultipartFile implements MultipartFile {

    private final String name;
    private final InputStream inputStream;

    public MockMultipartFile(File file) throws FileNotFoundException {
        this.name = file.getName();
        this.inputStream = new FileInputStream(file);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public byte[] getBytes(){
        return null;
    }

    @Override
    public InputStream getInputStream(){
        return this.inputStream;
    }

    @Override
    public void transferTo(File dest) throws IllegalStateException {

    }
}