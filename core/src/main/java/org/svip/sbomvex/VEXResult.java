package org.svip.sbomvex;

import org.svip.sbomvex.model.VEX;

import java.util.HashMap;

/**
 * file: VEXResult.java
 * Class that holds the results from the API vex method to create
 * a VEX object and any errors with components
 *
 * @author Matthew Morrison
 */
public record VEXResult(VEX vex, HashMap<String, String> error) {

     /**Getters*/
    public VEX vex() {
        return this.vex;
    }

    public HashMap<String, String> error() {
        return error;
    }
}
