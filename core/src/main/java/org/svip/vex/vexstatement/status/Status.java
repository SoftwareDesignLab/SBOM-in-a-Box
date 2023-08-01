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
