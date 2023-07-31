package org.svip.api.model;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class MockMultipartFile implements MultipartFile {

    private String name;
    private byte[] bytes;
    private InputStream inputStream;

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
        return this.inputStream;
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {

    }
}
