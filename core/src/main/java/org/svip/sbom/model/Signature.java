package org.svip.sbom.model;

import java.util.List;
import java.util.Set;

/**
 * Collection of Hash Algorithms used by Signature in CycloneDX
 *
 * @author Matt London
 */


/**
 * Satisfy the KTY parameter for CDX signatures
 */


/**
 * EC curve name
 */


/**
 * Represents one signature for an SBOM. The SBOM class will hold a set of these signatures
 */
public class Signature {
    /**
     * Algorithm used to sign the SBOM
     */
    private final SignatureAlgorithm algorithm;

    /**
     * Application specific id for signature
     */
    private final String keyId;

    /**
     * Public key parameter
     */
    private final SignatureKTY kty;
    private final SignatureCRV crv;
    private final String x;
    private final String y;

    /**
     * Certificate path
     * Sorted array of X.509 [RFC5280] certificates
     */
    private final List<String> certificatePath;

    /**
     * Excludes, names of any properties that are excluded from this signature
     */
    private final Set<String> excludes;

    /**
     * Signature data, must follow JWA [RFC7518] format
     */
    private final String value;

    public Signature(SignatureAlgorithm algorithm, String keyId, SignatureKTY kty, SignatureCRV crv, String x, String y,
                     List<String> certificatePath, Set<String> excludes, String value) {
        this.algorithm = algorithm;
        this.keyId = keyId;
        this.kty = kty;
        this.crv = crv;
        this.x = x;
        this.y = y;
        this.certificatePath = certificatePath;
        this.excludes = excludes;
        this.value = value;
    }

    ///
    /// Getters and Setters
    ///

    public SignatureAlgorithm getAlgorithm() {
        return algorithm;
    }

    public String getKeyId() {
        return keyId;
    }

    public SignatureKTY getKty() {
        return kty;
    }

    public SignatureCRV getCRV() {
        return crv;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public List<String> getCertificatePath() {
        return certificatePath;
    }

    public Set<String> getExcludes() {
        return excludes;
    }

    public String getValue() {
        return value;
    }
}
