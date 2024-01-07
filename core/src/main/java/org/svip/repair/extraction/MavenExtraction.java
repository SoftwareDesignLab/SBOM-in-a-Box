/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
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
 */

package org.svip.repair.extraction;

import org.svip.generation.parsers.utils.QueryWorker;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.uids.Hash.Algorithm;
import org.svip.sbom.model.uids.PURL;
import org.svip.utils.Debug;

import java.util.Optional;
import java.util.Set;

import static org.svip.utils.Debug.log;

/**
 * file: MavenExtraction.java
 * Description: Package-manager specific implementation of the Extraction (Maven)
 *
 * @author Jordan Wong
 */
public class MavenExtraction extends Extraction {

    private static final String URL = "https://repo1.maven.org/maven2/%s/%s/%s/%s.%s";
    private static final Set<Algorithm> HASH_ALGORITHMS = Set.of(Algorithm.MD5, Algorithm.SHA1);

    public MavenExtraction(PURL purl) {
        super(purl);
    }

    @Override
    public void extract() {
        if (!purl.getType().equals("maven") || purl.getName().equals("") || purl.getVersion().equals("")) {
            Debug.log(Debug.LOG_TYPE.WARN, "Can't extract due to incorrect type or missing name or version");
            return;
        }

        String formattedURL = String.format(this.URL,
            purl.getNamespace().get(0).replaceAll("\\.", "/"),
            purl.getName(),
            purl.getVersion(),
            String.join("-", purl.getName(), purl.getVersion()),
            purl.getQualifiers().get("type")
        ).toLowerCase();

        QueryWorker qw = new QueryWorker(null, formattedURL) {
            @Override
            public void run() {
                for (Algorithm algorithm : HASH_ALGORITHMS) {
                    String response = getUrlContents(
                            queryURL(url + "." + algorithm.toString().toLowerCase(), false));
                    hashes.put(algorithm, response.trim());
                }
            }
        };

        Thread t = new Thread(qw);
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            log(Debug.LOG_TYPE.ERROR, "Thread interrupted while waiting for queryWorker.");
        }

    }

    public static boolean isExtractable(Algorithm algorithm, Component component) {
        if (!(component instanceof SBOMPackage sbomPackage && HASH_ALGORITHMS.contains(algorithm)))
            return false;

        Optional<String> purlString = sbomPackage.getPURLs().stream().findFirst();
        if (purlString.isEmpty())
            return false;

        PURL purl;
        try {
            purl = new PURL(purlString.get());
        } catch (Exception e) {
            return false;
        }

        return purl.getType().equals("maven");
    }
}
