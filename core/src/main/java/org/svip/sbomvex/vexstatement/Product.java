package org.svip.sbomvex.vexstatement;

/**
 * file: Product.java
 * A record class that holds information of one product
 * in a VEX statement
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
