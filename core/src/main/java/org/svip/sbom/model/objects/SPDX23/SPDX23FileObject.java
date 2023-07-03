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
public class SPDX23FileObject implements SPDX23File {

    /**File's type*/
    private final String type;

    /**File's uid*/
    private final String uid;

    /**File's author*/
    private final String author;

    /**File's name*/
    private final String name;

    /**File's licenses*/
    private final LicenseCollection licenses;

    /**File's copyright*/
    private final String copyright;

    /**File's hashes*/
    private final HashMap<String, String> hashes;

    /**File's file notice*/
    private final String fileNotice;

    /**File's comment*/
    private final String comment;

    /**File's attribution text*/
    private final String attributionText;

    /**
     * Get the file's comment
     * @return the file's comment
     */
    @Override
    public String getComment() {
        return this.comment;
    }

    /**
     * Get the file's attribution text
     * @return the file's attribution text
     */
    @Override
    public String getAttributionText() {
        return this.attributionText;
    }

    /**
     * Get the file's file notice
     * @return the file's file notice
     */
    @Override
    public String getFileNotice() {
        return this.fileNotice;
    }
    /**
     * Get the file's type
     * @return the file's type
     */
    @Override
    public String getType() {
        return this.type;
    }

    /**
     * Get the file's uid
     * @return the file's uid
     */
    @Override
    public String getUID() {
        return this.uid;
    }

    /**
     * Get the file's author
     * @return the file's author
     */
    @Override
    public String getAuthor() {
        return this.author;
    }

    /**
     * Get the file's name
     * @return the file's name
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Get the file's licenses
     * @return the file's licenses
     */
    @Override
    public LicenseCollection getLicenses() {
        return this.licenses;
    }

    /**
     * Get the file's copyright info
     * @return the file's copyright info
     */
    @Override
    public String getCopyright() {
        return this.copyright;
    }

    /**
     * Get the file's hashes
     * @return the file's hashes
     */
    @Override
    public Map<String, String> getHashes() {
        return this.hashes;
    }

    /**
     * Constructor to build a new SPDX 2.3 File Object
     * @param type file type
     * @param uid file uid
     * @param author file author
     * @param name file name
     * @param licenses file licenses
     * @param copyright file copyright
     * @param hashes file hashes
     * @param fileNotice file's file notice
     * @param comment file's comment
     */
    public SPDX23FileObject(String type, String uid, String author, String name,
                      LicenseCollection licenses, String copyright,
                      HashMap<String, String> hashes, String fileNotice,
                            String comment, String attributionText){
        this.type = type;
        this.uid = uid;
        this.author = author;
        this.name = name;
        this.licenses = licenses;
        this.copyright = copyright;
        this.hashes = hashes;
        this.fileNotice = fileNotice;
        this.comment = comment;
        this.attributionText = attributionText;

    }
}
