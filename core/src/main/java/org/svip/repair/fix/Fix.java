package org.svip.repair.fix;

/**
 * Record to hold fixes for Repairs
 *
 * @param <T>   Type of object to fix
 * @param old   original state
 * @param fixed change to make
 * @author Juan Francisco Patino
 * @author Justin Jantzi
 */
public record Fix<T> (FixType type, T old, T fixed) {
    @Override
    public String toString() {
        return old + " -> " + fixed;
    }

    public FixType getType() {
        return type;
    }

    public T getNew() {
        return fixed;
    }

    /***
     * Gets fixed toString for frontend use
     * @return string
     */
    public String getNewString() {
        return fixed.toString();
    }

    /*
     * Used to determine if a new value is an addition
     * or a replacement of a previous value
     */
    public boolean fromNull() {
        return old == null;
    }
}
