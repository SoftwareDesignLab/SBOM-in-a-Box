package org.svip.builders.component;

import org.svip.builders.component.interfaces.SPDX23.SPDX23ComponentBuilder;
import org.svip.builders.component.interfaces.SPDX23.SPDX23FileBuilder_I;
import org.svip.builders.component.interfaces.generics.ComponentBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;

/**
 * file: SPDX23FileBuilder.java
 * Builder class for SPDX 2.3 file specifics
 *
 * @author Matthew Morrison
 */
public class SPDX23FileBuilder implements SPDX23FileBuilder_I{

    /**File type*/
    private String type;

    /**File uid*/
    private String uid;

    /**File author*/
    private String author;

    /**File name*/
    private String name;

    /**File licenses*/
    private LicenseCollection licenses;

    /**File copyright*/
    private String copyright;

    /**File hashes*/
    private HashMap<String, String> hashes;

    /**File comment*/
    private String comment;

    /**File attribution text*/
    private String attributionText;

    /**File's file notice*/
    private String fileNotice;

    /**
     * Set the component's type
     * @param type the designated type of component
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Set the component's uid
     * @param uid the uid of the component
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder setUID(String uid) {
        this.uid = uid;
        return this;
    }

    /**
     * Set the component's author
     * @param author the author of the component
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    /**
     * Set the component's name
     * @param name the name of the component
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set the component's licenses
     * @param licenses a collection of licenses
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder setLicenses(LicenseCollection licenses) {
        this.licenses = licenses;
        return this;
    }

    /**
     * Set the component's copyright info
     * @param copyright the copyright info of the component
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder setCopyright(String copyright) {
        this.copyright = copyright;
        return this;
    }

    /**
     * Add a hash value to the component
     * @param algorithm the algorithm of the hash
     * @param hash the value of the hash
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder addHash(String algorithm, String hash) {
        this.hashes.put(algorithm, hash);
        return this;
    }

    //TODO implement after SBOM refactor
    /**
     * Build an SPDX23FileObject
     * @return a Component
     */
    @Override
    public Component build() {
        return null;
    }

    //TODO implement after SBOM refactor
    /**
     * Build and flush an SPDX23FileObject
     * @return a Component
     */
    @Override
    public Component buildAndFlush() {
        return null;
    }

    /**
     * Set the component's comment
     * @param comment the comment for the component
     * @return an SPDX23ComponentBuilder
     */
    @Override
    public SPDX23ComponentBuilder setComment(String comment) {
        this.comment = comment;
        return this;
    }

    /**
     * Set the component's attribution text
     * @param attributionText the attribution text of the component
     * @return an SPDX23ComponentBuilder
     */
    @Override
    public SPDX23ComponentBuilder setAttributionText(String attributionText) {
        this.attributionText = attributionText;
        return this;
    }

    /**
     * Set the file's file notice
     * @param fileNotice the file notice
     * @return an SPDX23FileBuilder_I
     */
    @Override
    public SPDX23FileBuilder_I setFileNotice(String fileNotice) {
        this.fileNotice = fileNotice;
        return this;
    }
}
