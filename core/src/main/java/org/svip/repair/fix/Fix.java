package org.svip.repair.fix;

/**
 * Record to hold fixes for Repairs
 *
 * @param <T> Type of object to fix
 * @param old original state
 * @param fixed change to make
 *
 * @author Juan Francisco Patino
 */
public record Fix<T>(T old, T fixed) {
    @Override
    public String toString() {
        return old + " -> " + fixed;
    }
}
