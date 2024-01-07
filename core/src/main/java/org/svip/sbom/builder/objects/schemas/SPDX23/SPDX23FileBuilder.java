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

package org.svip.sbom.builder.objects.schemas.SPDX23;


import org.svip.sbom.builder.interfaces.schemas.SPDX23.SPDX23FileBuilder_I;
import org.svip.sbom.model.objects.SPDX23.SPDX23FileObject;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;

/**
 * file: SPDX23FileBuilder.java
 * Builder class for SPDX 2.3 file specifics
 *
 * @author Matthew Morrison
 * @author Thomas Roman
 */
public class SPDX23FileBuilder implements SPDX23FileBuilder_I {

    /**
     * File type
     */
    private String type;

    /**
     * File uid
     */
    private String uid;

    /**
     * File author
     */
    private String author;

    /**
     * File name
     */
    private String name;

    /**
     * File licenses
     */
    private LicenseCollection licenses = new LicenseCollection();

    /**
     * File copyright
     */
    private String copyright;

    /**
     * File hashes
     */
    private HashMap<String, String> hashes = new HashMap<>();

    /**
     * File comment
     */
    private String comment;

    /**
     * File attribution text
     */
    private String attributionText;

    /**
     * File's file notice
     */
    private String fileNotice;

    /**
     * Set the component's type
     *
     * @param type the designated type of component
     * @return an SPDX23FileBuilder
     */
    @Override
    public SPDX23FileBuilder setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Set the component's uid
     *
     * @param uid the uid of the component
     * @return an SPDX23FileBuilder
     */
    @Override
    public SPDX23FileBuilder setUID(String uid) {
        this.uid = uid;
        return this;
    }

    /**
     * Set the component's author
     *
     * @param author the author of the component
     * @return an SPDX23FileBuilder
     */
    @Override
    public SPDX23FileBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    /**
     * Set the component's name
     *
     * @param name the name of the component
     * @return an SPDX23FileBuilder
     */
    @Override
    public SPDX23FileBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set the component's licenses
     *
     * @param licenses a collection of licenses
     * @return an SPDX23FileBuilder
     */
    @Override
    public SPDX23FileBuilder setLicenses(LicenseCollection licenses) {
        this.licenses = licenses;
        return this;
    }

    /**
     * Set the component's copyright info
     *
     * @param copyright the copyright info of the component
     * @return an SPDX23FileBuilder
     */
    @Override
    public SPDX23FileBuilder setCopyright(String copyright) {
        this.copyright = copyright;
        return this;
    }

    /**
     * Add a hash value to the component
     *
     * @param algorithm the algorithm of the hash
     * @param hash      the value of the hash
     * @return an SPDX23FileBuilder
     */
    @Override
    public SPDX23FileBuilder addHash(String algorithm, String hash) {
        this.hashes.put(algorithm, hash);
        return this;
    }

    /**
     * Set the component's comment
     *
     * @param comment the comment for the component
     * @return an SPDX23FileBuilder
     */
    @Override
    public SPDX23FileBuilder setComment(String comment) {
        this.comment = comment;
        return this;
    }

    /**
     * Set the component's attribution text
     *
     * @param attributionText the attribution text of the component
     * @return an SPDX23FileBuilder
     */
    @Override
    public SPDX23FileBuilder setAttributionText(String attributionText) {
        this.attributionText = attributionText;
        return this;
    }

    /**
     * Set the file's file notice
     *
     * @param fileNotice the file notice
     * @return an SPDX23FileBuilder
     */
    @Override
    public SPDX23FileBuilder setFileNotice(String fileNotice) {
        this.fileNotice = fileNotice;
        return this;
    }

    /**
     * Build an SPDX23FileObject
     *
     * @return an SPDX23FileObject
     */
    @Override
    public SPDX23FileObject build() {
        return new SPDX23FileObject(type, uid, author, name, licenses,
                copyright, hashes, fileNotice, comment, attributionText);
    }

    /**
     * Build and flush an SPDX23FileObject
     *
     * @return a Component
     */
    @Override
    public SPDX23FileObject buildAndFlush() {
        // build the component
        SPDX23FileObject fileObject = build();
        // clear all the data in the builder
        this.type = null;
        this.uid = null;
        this.author = null;
        this.name = null;
        this.licenses = new LicenseCollection();
        this.copyright = null;
        this.hashes = new HashMap<>();
        this.fileNotice = null;
        this.comment = null;
        this.attributionText = null;
        return fileObject;
    }
}
