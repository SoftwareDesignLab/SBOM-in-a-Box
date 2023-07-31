package org.svip.api.model;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class MockMultipartFile implements MultipartFile {

    private String name;
    private byte[] bytes;
    private InputStream inputStream;

//    public MockMultipartFile(ZipFile file){
//
//        this.name = file.getName(); // todo delete if unneeded
//        Stream< ? extends ZipEntry> stream = file.stream();
//
//    }

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
    public byte[] getBytes() throws IOException {
        return bytes;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {

    }
}
