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

package org.svip.vex.model;

import org.svip.vex.vexstatement.VEXStatement;

import java.util.HashSet;
import java.util.Set;

/**
 * file: VEX.java
 * Class that builds a VEX document
 *
 * @author Matthew Morrison
 */
public class VEX {

    /**
     * The document's VEX Identifier
     */
    private final String vexIdentifier;

    /**
     * The document's origin type
     */
    private final VEXType originType;

    /**
     * The document's spec version
     */
    private final String specVersion;

    /**
     * The document's version
     */
    private final String docVersion;

    /**
     * The document's first issued time
     */
    private final String timeFirstIssued;

    /**
     * The document's last updated time
     */
    private final String timeLastUpdated;

    /**
     * The document's VEX Statements
     */
    private final Set<VEXStatement> vexStatements;

    /**
     * Get the document's VEX Identifier
     *
     * @return the vexIdentifier
     */
    public String getVexIdentifier() {
        return this.vexIdentifier;
    }

    /**
     * Get the document's origin type
     *
     * @return the originType
     */
    public VEXType getOriginType() {
        return this.originType;
    }

    /**
     * Get the document's spec version
     *
     * @return the specVersion
     */
    public String getSpecVersion() {
        return this.specVersion;
    }

    /**
     * Get the document's version
     *
     * @return the docVersion
     */
    public String getDocVersion() {
        return this.docVersion;
    }

    /**
     * Get the document's first issued time
     *
     * @return the timeFirstIssued
     */
    public String getTimeFirstIssued() {
        return this.timeFirstIssued;
    }

    /**
     * Get the document's last updated time
     *
     * @return the timeLastUpdated
     */
    public String getTimeLastUpdated() {
        return this.timeLastUpdated;
    }

    /**
     * Get the VEX Statements of this VEX document
     *
     * @return the document's vexStatements
     */
    public Set<VEXStatement> getVEXStatements() {
        return this.vexStatements;
    }

    public static class Builder {

        // required fields

        /**
         * The document's VEX Identifier
         */
        private String vexIdentifier;

        /**
         * The document's origin type
         */
        private VEXType originType;

        /**
         * The document's spec version
         */
        private String specVersion;

        /**
         * The document's version
         */
        private String docVersion;

        /**
         * The document's first issued time
         */
        private String timeFirstIssued;

        /**
         * The document's last updated time
         */
        private String timeLastUpdated;

        private final Set<VEXStatement> vexStatements = new HashSet<>();

        /**
         * Set the document's VEX Identifier
         *
         * @param vexIdentifier the VEX Identifier
         * @return a Builder
         */
        public Builder setVEXIdentifier(String vexIdentifier) {
            this.vexIdentifier = vexIdentifier;
            return this;
        }

        /**
         * Set the document's origin type
         *
         * @param originType the origin type
         * @return a Builder
         */
        public Builder setOriginType(VEXType originType) {
            this.originType = originType;
            return this;
        }

        /**
         * Set the document's spec version
         *
         * @param specVersion the spec version
         * @return a Builder
         */
        public Builder setSpecVersion(String specVersion) {
            this.specVersion = specVersion;
            return this;
        }

        /**
         * Set the document's version
         *
         * @param docVersion the document version
         * @return a Builder
         */
        public Builder setDocVersion(String docVersion) {
            this.docVersion = docVersion;
            return this;
        }

        /**
         * Set the document's first issued time
         *
         * @param timeFirstIssued the time first issued
         * @return a Builder
         */
        public Builder setTimeFirstIssued(String timeFirstIssued) {
            this.timeFirstIssued = timeFirstIssued;
            return this;
        }

        /**
         * Set the document's last updated time
         *
         * @param timeLastUpdated the time last updated
         * @return a Builder
         */
        public Builder setTimeLastUpdated(String timeLastUpdated) {
            this.timeLastUpdated = timeLastUpdated;
            return this;
        }

        /**
         * Add a VEX Statement to the document
         *
         * @param vexStatement the VEX Statement to add
         * @return a Builder
         */
        public Builder addVEXStatement(VEXStatement vexStatement) {
            this.vexStatements.add(vexStatement);
            return this;
        }

        /**
         * Build a new VEX document
         *
         * @return a VEX object
         */
        public VEX build() {
            return new VEX(this);
        }
    }

    /**
     * Constructor to build the VEX Document
     *
     * @param builder the Builder
     */
    //TODO add vexStatement when VEXStatement is implemented
    public VEX(Builder builder) {
        this.vexIdentifier = builder.vexIdentifier;
        this.originType = builder.originType;
        this.specVersion = builder.specVersion;
        this.docVersion = builder.docVersion;
        this.timeFirstIssued = builder.timeFirstIssued;
        this.timeLastUpdated = builder.timeLastUpdated;
        this.vexStatements = builder.vexStatements;
    }
}
