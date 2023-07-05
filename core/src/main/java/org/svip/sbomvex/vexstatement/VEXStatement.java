package org.svip.sbomvex.vexstatement;

import org.svip.sbomvex.vexstatement.status.Status;

import java.util.Set;

/**
 * file: VEXStatement.java
 * Class that holds a single statement for a VEX Document
 *
 * @author Matthew Morrison
 */
public class VEXStatement {
    /**The statement ID*/
    private String statementID;

    /**The statement version*/
    private String statementVersion;

    /**The statement's first issued date*/
    private String statementFirstIssued;

    /**The statement's last updated date*/
    private String statementLastUpdated;

    /**The statement's status*/
    private Status status;

    /**The statement's set of products*/
    private Set<Product> products;

    /**The statement's vulnerability statement*/
    private Vulnerability vulnerability;

    /**
     * Get the VEX Statement ID
     * @return the statementID
     */
    public String getStatementID() {
        return this.statementID;
    }

    /**
     * Get the VEX Statement version
     * @return the statementVersion
     */
    public String getStatementVersion() {
        return this.statementVersion;
    }

    /**
     * Get the VEX Statement first issued date
     * @return the statementFirstIssued
     */
    public String getStatementFirstIssued() {
        return this.statementFirstIssued;
    }

    /**
     * Get the VEX Statement last updated date
     * @return the statementLastUpdated
     */
    public String getStatementLastUpdated() {
        return this.statementLastUpdated;
    }

    /**
     * Get the VEX Statement status
     * @return the status
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * Get the VEX Statement products
     * @return the products
     */
    public Set<Product> getProducts() {
        return this.products;
    }

    /**
     * Get the VEX Statement vulnerability
     * @return the vulnerability
     */
    public Vulnerability getVulnerability() {
        return this.vulnerability;
    }

    /**
     * Constructor to buiild a new VEX Statement
     * @param statementID the statement ID
     * @param statementVersion the statement version
     * @param statementFirstIssued when the statement was first issued
     * @param statementLastUpdated when the statement was last updated
     * @param status the statement status
     * @param products the statement products
     * @param vulnerability the statement vulnerability
     */
    private VEXStatement(String statementID, String statementVersion,
                         String statementFirstIssued, String statementLastUpdated,
                         Status status, Set<Product> products, Vulnerability vulnerability){
        this.statementID = statementID;
        this.statementVersion = statementVersion;
        this.statementFirstIssued = statementFirstIssued;
        this.statementLastUpdated = statementLastUpdated;
        this.status = status;
        this.products = products;
        this.vulnerability = vulnerability;
    }

}
