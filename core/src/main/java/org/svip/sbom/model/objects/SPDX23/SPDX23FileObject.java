package org.svip.sbom.model.objects.SPDX23;

import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23File;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;
import java.util.Map;

/**
 * file: SPDX23FileObject.java
 * Holds information for a single SPDX 2.3 File object
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
// todo
public class SPDX23FileObject implements SPDX23File {

    private String type;

    private String uid;

    private String author;

    private String name;

    private LicenseCollection licenses;

    private String copyright;

    private HashMap<String, String> hashes;

    private String fileNotice;

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public String getAttributionText() {
        return null;
    }

    @Override
    public String getFileNotice() {
        return null;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getUID() {
        return this.uid;
    }

    @Override
    public String getAuthor() {
        return this.author;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public LicenseCollection getLicenses() {
        return this.licenses;
    }

    @Override
    public String getCopyright() {
        return this.copyright;
    }

    @Override
    public Map<String, String> getHashes() {
        return this.hashes;
    }

    public SPDX23FileObject(String type, String uid, String author, String name,
                      LicenseCollection licenses, String copyright,
                      HashMap<String, String> hashes, String fileNotice){
        this.type = type;
        this.uid = uid;
        this.author = author;
        this.name = name;
        this.licenses = licenses;
        this.copyright = copyright;
        this.hashes = hashes;
        this.fileNotice = fileNotice;



    }
}
