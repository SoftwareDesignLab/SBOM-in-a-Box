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
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.serializers.deserializer.CDX14JSONDeserializer;
import org.svip.serializers.deserializer.SPDX23JSONDeserializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for repairing CDX and SPDX SBOMs
 *
 * @author  Justin Jantzi
 */
public class RepairTest {

    private final String NULL_COPYRIGHT_SBOM_CDX = System.getProperty("user.dir") +
            "/src/test/resources/repair/null-copyright-cdx.json";
    private final String CDX_14_JSON_SBOM = System.getProperty("user.dir") +
            "/src/test/resources/serializers/cdx_json/sbom.alpine.json";

    private final String SPDX23_JSON_SBOM = System.getProperty("user.dir") +
            "/src/test/resources/serializers/spdx_json/syft-0.80.0-source-spdx-json.json";
    private final RepairController r = new RepairController();
    private final SPDX23JSONDeserializer spdx23JSONDeserializer = new SPDX23JSONDeserializer();
    private final CDX14JSONDeserializer cdx14JSONDeserializer = new CDX14JSONDeserializer();

    @Test
    public void SPDXRepairTest() throws Exception {
        SPDX23SBOM sbom = spdx23JSONDeserializer.readFromString(Files.readString(Path.of(SPDX23_JSON_SBOM)));
        QualityReport statement = r.generateStatement(sbom);
        SPDX23SBOM repaired = (SPDX23SBOM) r.repairSBOM(sbom, statement.getFixes());
        QualityReport newStatement = r.generateStatement(repaired);
        assertEquals(0, newStatement.getFixAmount());
    }

    @Test
    public void CDXRepairTest() throws Exception {
        CDX14SBOM sbom = cdx14JSONDeserializer.readFromString(Files.readString(Path.of(CDX_14_JSON_SBOM)));
        QualityReport statement = r.generateStatement(sbom);
        CDX14SBOM repaired = (CDX14SBOM) r.repairSBOM(sbom, statement.getFixes());
        QualityReport newStatement = r.generateStatement(repaired);
        assertEquals(0, newStatement.getFixAmount());
    }

    @Test
    public void SPDXNullRepairsTest() throws Exception {
        SPDX23SBOM sbom = spdx23JSONDeserializer.readFromString(Files.readString(Path.of(SPDX23_JSON_SBOM)));
        SPDX23SBOM repairedSBOM = (SPDX23SBOM) r.repairSBOM(sbom, null);
        assertEquals(true, sbom.equals(repairedSBOM));
    }

    @Test
    public void CDXNullRepairsTest() throws Exception {
        CDX14SBOM sbom = cdx14JSONDeserializer.readFromString(Files.readString(Path.of(CDX_14_JSON_SBOM)));
        CDX14SBOM repairedSBOM = (CDX14SBOM) r.repairSBOM(sbom, null);
        assertEquals(true, sbom.equals(repairedSBOM));
    }

    @Test
    public void CDXEmptyRepairsTest() throws Exception {
        CDX14SBOM sbom = cdx14JSONDeserializer.readFromString(Files.readString(Path.of(CDX_14_JSON_SBOM)));
        QualityReport statement = r.generateStatement(sbom);
        Map<Integer, Set<Fix<?>>> fixes = new HashMap<Integer, Set<Fix<?>>>();
        CDX14SBOM repairedSBOM = (CDX14SBOM) r.repairSBOM(sbom, fixes);
        QualityReport newStatement = r.generateStatement(repairedSBOM);
        assertEquals(statement.getFixAmount(), newStatement.getFixAmount());
    }

    @Test
    public void SPDXEmptyRepairsTest() throws Exception {
        SPDX23SBOM sbom = spdx23JSONDeserializer.readFromString(Files.readString(Path.of(SPDX23_JSON_SBOM)));
        QualityReport statement = r.generateStatement(sbom);
        Map<Integer, Set<Fix<?>>>  fixes = new HashMap<Integer, Set<Fix<?>>>();
        SPDX23SBOM repairedSBOM = (SPDX23SBOM) r.repairSBOM(sbom, fixes);
        QualityReport newStatement = r.generateStatement(repairedSBOM);
        assertEquals(statement.getFixAmount(), newStatement.getFixAmount());
    }

    @Test
    public void CDXCopyrightPurlRepairTest() throws Exception {
        CDX14SBOM sbom = cdx14JSONDeserializer.readFromString(Files.readString(Path.of(NULL_COPYRIGHT_SBOM_CDX)));
        QualityReport statement = r.generateStatement(sbom);
        CDX14SBOM repairedSBOM = (CDX14SBOM) r.repairSBOM(sbom, statement.getFixes());
        QualityReport newStatement = r.generateStatement(repairedSBOM);
        assertEquals(0, newStatement.getFixAmount());
    }

}
