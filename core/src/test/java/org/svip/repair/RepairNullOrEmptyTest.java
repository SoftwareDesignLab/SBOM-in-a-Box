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

package org.svip.repair;

import org.junit.jupiter.api.Test;
import org.svip.metrics.pipelines.QualityReport;
import org.svip.metrics.resultfactory.Result;
import org.svip.repair.fix.Fix;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.serializers.deserializer.CDX14JSONDeserializer;
import org.svip.serializers.deserializer.SPDX23JSONDeserializer;
import org.svip.serializers.deserializer.SPDX23TagValueDeserializer;
import org.svip.utils.Debug;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for EmptyOrNullFixes class
 *
 * @author Justin Jantzi
 */
public class RepairNullOrEmptyTest {
    private final String NULL_COPYRIGHT_SBOM_SPDX = System.getProperty("user.dir") +
            "/src/test/resources/repair/null-copyright.spdx";

    private final String NULL_COPYRIGHT_SBOM_CDX = System.getProperty("user.dir") +
            "/src/test/resources/repair/null-copyright-cdx.json";

    private final String MICROSOFT_COPYRIGHT_FIX = "© Microsoft Corporation. All rights reserved.";

    private final String PACKAGE_NAME = "System.Text.Json";

    private final Integer PACKAGE_HASHCODE = 450508447;

    private final RepairController r = new RepairController();

    @Test
    public void NullCopyrightSPDX23() throws Exception {
        SPDX23TagValueDeserializer spdx23JSONDeserializer = new SPDX23TagValueDeserializer();
        SPDX23SBOM sbom = spdx23JSONDeserializer.readFromString(Files.readString(Path.of(NULL_COPYRIGHT_SBOM_SPDX)));
        QualityReport statement = r.generateStatement(sbom);
        List<Result> results = statement.getResults().get(PACKAGE_HASHCODE);
        assertEquals(MICROSOFT_COPYRIGHT_FIX, results.get(results.size() - 1).getFixes().get(0).getNew());
    }

    @Test
    public void NullCopyrightCDX() throws Exception {
        CDX14JSONDeserializer  cdx14JSONDeserializer = new CDX14JSONDeserializer();
        CDX14SBOM sbom = cdx14JSONDeserializer.readFromString(Files.readString(Path.of(NULL_COPYRIGHT_SBOM_CDX)));
        QualityReport statement = r.generateStatement(sbom);
        List<Result> results = statement.getResults().get(PACKAGE_HASHCODE);
        assertEquals(MICROSOFT_COPYRIGHT_FIX, results.get(results.size() - 1).getFixes().get(0).getNew());
    }
}
