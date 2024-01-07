/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.repair.extraction.MavenExtraction;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.uids.Hash;
import org.svip.sbom.model.uids.Hash.Algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * file: HashFixes.java
 * Fixes class to generate suggested component hash repairs
 *
 * @author Jordan Wong
 */
public class HashFixes implements Fixes<Hash> {

    /**
     * Get a list of possible fixes for invalid hashes.
     *
     * @param result        object from quality report
     * @param sbom          sbom from quality report
     * @param componentName key from quality report map most directly relating to the repair type
     * @param componentHashCode hash code of the component
     * @return list of hash fixes
     */
    @Override
    public List<Fix<Hash>> fix(Result result, SBOM sbom, String componentName, Integer componentHashCode) {

        // Get algorithm and hash value from result message
        String[] details = result.getDetails().split(" ");
        String algorithm = details[details.length - 1];
        String value = details[0];

        // Create Hash object
        Hash hash = new Hash(algorithm, value);

        // Retrieve component from SBOM by componentName
        Component component = sbom.getComponents().stream()
                .filter(c -> c.getName().equalsIgnoreCase(componentName))
                .findFirst()
                .get();

        if (MavenExtraction.isExtractable(hash.getAlgorithm(), component)) {
            // Retrieve valid hash from Maven Repository
            Hash validHash = new Hash(hash.getAlgorithm(), hash.getValidValue(component));
            return List.of(new Fix<>(FixType.COMPONENT_HASH, hash, validHash));
        } else {
            // Get all possible algorithms that match the hash
            List<Algorithm> validAlgorithms = hash.getValidAlgorithms(sbom instanceof SPDX23SBOM);

            // Suggest deleting the hash as a fix if hash does not match any algorithm
            if (validAlgorithms.isEmpty()) {
                return List.of(new Fix<>(FixType.COMPONENT_HASH, hash, null));
            }

            // Return the list of fixes of possible matching hash algorithms
            List<Fix<Hash>> fixes = new ArrayList<>();
            for (Algorithm validAlgorithm : validAlgorithms) {
                Hash fixedHash = new Hash(validAlgorithm, value);
                fixes.add(new Fix<>(FixType.COMPONENT_HASH, hash, fixedHash));
            }
            return fixes;
        }
        
    }

}
