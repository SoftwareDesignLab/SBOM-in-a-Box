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

package org.svip.repair.extraction;

import org.svip.sbom.model.uids.Hash.Algorithm;
import org.svip.sbom.model.uids.PURL;

import java.util.HashMap;
import java.util.Map;

/**
 * <b>File</b>: Extraction.java<br>
 * <b>Description</b>: Abstract core Class for extracting information
 * from package manager metadata
 * @author Justin Jantzi
 */
public abstract class Extraction {

    protected PURL purl;
    protected String copyright;
    protected String license;
    protected Map<Algorithm, String> hashes;

    /**
     * Creates a new object with the purl to use for extraction
     * @param purl the purl to be used
     */
    public Extraction(PURL purl) {
        this.purl = purl;
        this.copyright = "";
        this.license = "";
        this.hashes = new HashMap<>();
    }

    /**
     * Extracts the information based on the different
     * package manager implementation
     */
    public abstract void extract();

    /**
     * Gets the copyright from extracted text if exists
     * @return copyright
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * Gets the license from extracted text if exists
     * @return license
     */
    public String getLicense() {
        return license;
    }

    /**
     * Gets the hashes from maven repository if exists
     * @return hashes {algorithm : hash}
     */
    public Map<Algorithm, String> getHashes() {
        return hashes;
    }
}
