package org.svip.vex.vexstatement;

/**
 * file: Product.java
 * Record class that defines a single product in a VEX Statement
 * @param productID the product's ID
 * @param supplier the product's supplier
 *
 * @author Matthew Morrison
 */
public record Product(String productID, String supplier) {

    /**
     * Get the product's ID
     * @return the productID
     */
    public String getProductID(){
        return this.productID;
    }

    /**
     * Get the product's supplier
     * @return the supplier
     */
    public String getSupplier(){
        return this.supplier;
    }

    public Product(String productID, String supplier){
        this.productID = productID;
        this.supplier = supplier;
    }
}
