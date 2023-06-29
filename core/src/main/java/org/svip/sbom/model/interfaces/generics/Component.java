package org.svip.sbom.model.interfaces.generics;

import org.svip.sbom.model.util.LicenseCollection;

import java.util.Map;

/**
 * file: Component.java
 * Generic component details that many componentss share
 *
 * @author Derek Garcia
 */
public interface Component {
    /**
     * @return Type of component
     */
    String getType();

    /**
     * @return Unique identifier of the component
     */
    String getUID();

    /**
     * @return Author of the component
     */
    String getAuthor();


    /**
     * @return Name of the component
     */
    String getName();

    /**
     * @return component Licenses
     */
    LicenseCollection getLicenses();


    /**
     * @return Get component copyright
     */
    String getCopyright();

    /**
     * @return Get hashes
     */
    Map<String, String> getHashes();

}
