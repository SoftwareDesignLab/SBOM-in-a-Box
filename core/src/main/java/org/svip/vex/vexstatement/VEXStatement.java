package org.svip.vex.vexstatement;

import org.svip.vex.vexstatement.status.Status;

import java.util.HashSet;
import java.util.Set;

/**
 * file: VEXStatement.java
 * Class that holds a single statement for a VEX Document
 *
 * @author Matthew Morrison
 */
public class VEXStatement {
    /**The statement ID*/
    private final String statementID;

    /**The statement version*/
    private final String statementVersion;

    /**The statement's first issued date*/
    private final String statementFirstIssued;

    /**The statement's last updated date*/
    private final String statementLastUpdated;

    /**The statement's status*/
    private final Status status;

    /**The statement's set of products*/
    private final Set<Product> products;

    /**The statement's vulnerability statement*/
    private final Vulnerability vulnerability;

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


    public static class Builder{
        // Required fields
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
        private Set<Product> products = new HashSet<>();

        /**The statement's vulnerability statement*/
        private Vulnerability vulnerability;

        /**
         * Set the statement ID
         * @param statementID the statement ID
         * @return a Builder
         */
        public Builder setStatementID(String statementID){
            this.statementID = statementID;
            return this;
        }

        /**
         * Set the statement version
         * @param statementVersion the statement version
         * @return a Builder
         */
        public Builder setStatementVersion(String statementVersion){
            this.statementVersion = statementVersion;
            return this;
        }

        /**
         * Set the statement first issued date
         * @param statementFirstIssued the statement first issued date
         * @return a Builder
         */
        public Builder setStatementFirstIssued(String statementFirstIssued){
            this.statementFirstIssued = statementFirstIssued;
            return this;
        }

        /**
         * Set the statement last updated date
         * @param statementLastUpdated the statement last updated date
         * @return a Builder
         */
        public Builder setStatementLastUpdated(String statementLastUpdated){
            this.statementLastUpdated = statementLastUpdated;
             return this;
        }

        /**
         * Set the statement status
         * @param status the status
         * @return a Builder
         */
        public Builder setStatus(Status status){
            this.status = status;
            return this;
        }

        /**
         * Add a product to the statement
         * @param product the product
         * @return a Builder
         */
        public Builder addProduct(Product product){
            this.products.add(product);
            return this;
        }

        /**
         * Set the statement's vulnerability
         * @param vulnerability the vulnerability
         * @return a Builder
         */
        public Builder setVulnerability(Vulnerability vulnerability){
            this.vulnerability = vulnerability;
            return this;
        }

        /**
         * Build a new VEX statement using the builder
         * @return a new VEX Statement
         */
        public VEXStatement build(){
            return new VEXStatement(this);
        }
    }

    /**
     * Constructor to build a new VEX Statement
     * @param builder the Builder static class object for VEX Statement
     */
    private VEXStatement(Builder builder){
        this.statementID = builder.statementID;
        this.statementVersion = builder.statementVersion;
        this.statementFirstIssued = builder.statementFirstIssued;
        this.statementLastUpdated = builder.statementLastUpdated;
        this.status = builder.status;
        this.products = builder.products;
        this.vulnerability = builder.vulnerability;
    }

}
