/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
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
* /

package org.svip.vex.vexstatement.status;

/**
 * file: Status.java
 * Class that defines a status for a VEX Statement
 *
 * @author Matthew Morrison
 */
public class Status {
    /**
     * The statement's vulnerability status
     */
    private final VulnStatus vulnStatus;

    /**
     * The statement's justification (NOT_AFFECTED only)
     */
    private final Justification justification;

    /**
     * The statement's action statement (AFFECTED only)
     */
    private final String actionStatement;

    /**
     * The statement's impact statement (AFFECTED and NOT_AFFECTED only)
     */
    private final String impactStatement;

    /**
     * Constructor to build a single VEX Statement status
     *
     * @param vulnStatus      the vulnerability status
     * @param justification   the justification
     * @param actionStatement the vulnerability action statement
     * @param impactStatement the vulnerability impact statement
     */
    public Status(VulnStatus vulnStatus, Justification justification,
                  String actionStatement, String impactStatement) {
        this.vulnStatus = vulnStatus;
        this.justification = justification;
        this.actionStatement = actionStatement;
        this.impactStatement = impactStatement;
    }

    /**
     * Get the status' vulnerability status
     *
     * @return the vulnerability status
     */
    public VulnStatus getVulnStatus() {
        return this.vulnStatus;
    }

    /**
     * Get the statement's justification
     *
     * @return the justification
     */
    public Justification getJustification() {
        return this.justification;
    }

    /**
     * Get the statement's action statement
     *
     * @return the action statement
     */
    public String getActionStatement() {
        return this.actionStatement;
    }

    /**
     * Get the statement's impact statement
     *
     * @return the impact statement
     */
    public String getImpactStatement() {
        return this.actionStatement;
    }
}
