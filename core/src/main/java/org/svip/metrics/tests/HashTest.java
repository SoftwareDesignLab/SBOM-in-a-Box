package org.svip.metrics.tests;

import jregex.Pattern;
import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.ResultFactory;
import org.svip.metrics.resultfactory.enumerations.INFO;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;
import org.svip.repair.extraction.Extraction;
import org.svip.repair.extraction.MavenExtraction;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.uids.Hash;
import org.svip.sbom.model.uids.PURL;
import org.svip.utils.Debug;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.svip.sbom.model.uids.Hash.Algorithm.MD5;
import static org.svip.sbom.model.uids.Hash.Algorithm.SHA1;

/**
 * file: HashTest.java
 * Series of tests for hash string and hash objects
 *
 * @author Matthew Morrison
 */
public class HashTest extends MetricTest {
    private final ResultFactory resultFactory;
    private final Component component;


    /**
     * Constructor to create a new MetricTest
     *
     * @param attributes the list of attributes used
     */
    public HashTest(Component component, ATTRIBUTE... attributes) {
        super(attributes);
        String TEST_NAME = "HashTest";
        resultFactory = new ResultFactory(TEST_NAME, attributes);
        this.component = component;
    }

    /**
     * Test a single hash if it is valid
     *
     * @param field the hash algorithm to test
     * @param value the hash value to test
     * @return a Result if the hash data is valid or not
     */
    @Override
    public Set<Result> test(String field, String value) {
        Set<Result> results = new HashSet<>();
        // hash  is not a null value and does exist, tests can run
        if (value != null && field != null) {
            results.add(validHashResult(field, value));

        }
        // Hash has a null algo or value, tests cannot be run
        // return missing Result
        else {
            Result r = resultFactory.error(field, INFO.NULL,
                    value, component.getName());
            results.add(r);
        }
        return results;
    }


    /**
     * Test the hash if it is a valid schema and type
     *
     * @param field the hash algorithm
     * @param value the hash value
     * @return a Result if the hash is valid or not
     */
    private Result validHashResult(String field, String value) {
        var rf = new ResultFactory("Valid Hash", ATTRIBUTE.COMPLETENESS, ATTRIBUTE.UNIQUENESS, ATTRIBUTE.MINIMUM_ELEMENTS);
        try {
            // create new hash object
            Hash hash = new Hash(field, value);

            // Check if hash algorithm is unknown
            if (hash.getAlgorithm() == Hash.Algorithm.UNKNOWN) {
                return rf.fail(field, INFO.INVALID,
                        value, component.getName());
            }

            // Check if hash is valid
            if (!isValidHash(component, hash)) {
                return rf.fail(field, INFO.INVALID,
                        value, component.getName());
            } else {
                return rf.pass(field, INFO.VALID,
                        value, component.getName());
            }

        }
        // failed to create a new Hash object, test automatically fails
        catch (Exception e) {
            return rf.fail(field, INFO.INVALID,
                    value, component.getName());
        }
    }

    /**
     * Validates a hash function to make sure the length and content are correct
     *
     * @param component SBOMFile component
     * @param hash Hash
     * @return True if hash passes, false otherwise
     */
    private boolean isValidHash(Component component, Hash hash) {
        // Validate MD5 and SHA1 hashes against Maven Repository
        String purlString;
        if (component instanceof SBOMPackage sbomPackage
                && (hash.getAlgorithm().equals(MD5) || hash.getAlgorithm().equals(SHA1))
                && (purlString = sbomPackage.getPURLs().stream().findFirst().get()).startsWith("pkg:maven")) {

            // Create PURL object
            PURL purl;
            try {
                purl = new PURL(purlString);
            } catch (Exception e) {
                Debug.log(Debug.LOG_TYPE.WARN, e.getMessage());
                return false;
            }

            // Perform extraction of MD5 and SHA1 hashes from Maven Repository
            Extraction extraction = new MavenExtraction(purl);
            extraction.extract();

            // Get hashes
            Map<Hash.Algorithm, String> validHashes = extraction.getHashes();
            Map<String, String> componentHashes = sbomPackage.getHashes();
            // Compare hash
            return validHashes.get(hash.getAlgorithm()).equals(componentHashes.get(hash.getAlgorithm().toString()));

        } else {
            // If component is not from Maven Repository, then test against supported hashes using regex
            Pattern p = new Pattern(
                    switch (hash.getAlgorithm()) {
                        case ADLER32 -> "^[a-fA-F0-9]{8}$";
                        case SHA1 -> "^[a-fA-F0-9]{40}$";
                        case SHA224 -> "^[a-fA-F0-9]{56}$";
                        case SHA256, SHA3256, BLAKE2b256, BLAKE3 -> "^[a-fA-F0-9]{64}$";
                        case SHA384, SHA3384, BLAKE2b384 -> "^[a-fA-F0-9]{96}$";
                        case SHA512, SHA3512, BLAKE2b512 -> "^[a-fA-F0-9]{128}$";
                        case MD2, MD4, MD5 -> "^[a-fA-F0-9]{32}$";
                        case MD6 -> "^([a-fA-F0-9]{32}|[a-fA-F0-9]{64}|[a-fA-F0-9]{128})$";
                        default -> "(?!)"; // Regex will always fail
                    }, Pattern.MULTILINE);
            return p.matches(hash.getValue());
        }
    }
}
