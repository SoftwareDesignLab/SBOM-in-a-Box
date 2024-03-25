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

package org.svip.vex.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.shared.metadata.Organization;

import org.svip.vex.model.VEX;
import org.svip.vex.model.VEXType;
import org.svip.vex.vexstatement.VEXStatement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OSVClientTest {
    OSVClient client = new OSVClient();

    @Test
    public void test_generateVEX_valid_VEX_object_CDX_json_sbom() throws Exception {
        Organization test_org1 = new Organization("com.google.guava", "google.com");
        Organization test_org2 = new Organization("The Pallets Projects", "google.com");

        CDX14ComponentObject test_component1 = new CDX14ComponentObject(
                null, null, "com.google.guava", "guava", null, null,
                null, test_org1, "19.0", null, null, null,
                null, null, null, null, null, null);
        CDX14ComponentObject test_component2 = new CDX14ComponentObject(
                null, null, null, "jinja2", null, null,
                null, test_org2, "2.4.1", null, null, null,
                null, null, null, null, null, null);
        Set<Component> test_components = new HashSet<>(List.of(
                test_component1,  test_component2)
        );

        CDX14SBOM test_sbom = new CDX14SBOM("CycloneDX 1.4", "test", null,
                "1.0", "1.4", null, null, null, null,
                test_components, null, null);

        VEX.Builder testVEX = new VEX.Builder();
        String creationTime = String.valueOf(java.time.LocalDateTime.now());
        testVEX.setVEXIdentifier(test_sbom.getName());
        testVEX.setOriginType(VEXType.CYCLONE_DX);
        testVEX.setSpecVersion("1.4");
        testVEX.setDocVersion("1.0");
        testVEX.setTimeFirstIssued(creationTime);
        testVEX.setTimeLastUpdated(creationTime);

        List<VEXStatement> statements = new ArrayList<>();
        statements.addAll(client.getVEXStatements(test_component1));
        statements.addAll(client.getVEXStatements(test_component2));

        for(VEXStatement v :statements){
            testVEX.addVEXStatement(v);
        }

        VEX vex = testVEX.build();
        ObjectMapper objectMapper = new ObjectMapper();
        // pretty print
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(vex);
        System.out.println(json);
    }

    @Test
    public void test_generateVEX_name_version_purl_null_throws_exception_call() throws Exception {
        Organization test_org1 = new Organization("com.google.guava", "google.com");
        Organization test_org2 = new Organization("The Pallets Projects", "google.com");

        CDX14ComponentObject test_component1 = new CDX14ComponentObject(
                null, null, "com.google.guava", null, null, null,
                null, test_org1, null, null, null, null,
                null, null, null, null, null, null);
        CDX14ComponentObject test_component2 = new CDX14ComponentObject(
                null, null, null, null, null, null,
                null, test_org2, null, null, null, null,
                null, null, null, null, null, null);


        assertThrows(Exception.class, () -> client.getVEXStatements(test_component1));
        assertThrows(Exception.class, () -> client.getVEXStatements(test_component2));
    }

    @Test
    public void test_generateVEX_valid_purl_call_CDX_json_sbom() throws Exception {
        Organization test_org1 = new Organization("com.google.guava", "google.com");
        Set<String> test_purl = new HashSet<>(List.of(
                "pkg:maven/com.google.guava/guava@19.0"
        ));

        CDX14ComponentObject test_component1 = new CDX14ComponentObject(
                null, null, "com.google.guava", "guava", null, null,
                null, test_org1, "19.0", null, null, test_purl,
                null, null, null, null, null, null);
        Set<Component> test_components = new HashSet<>(List.of(
                test_component1)
        );

        CDX14SBOM test_sbom = new CDX14SBOM("CycloneDX 1.4", "test", null,
                "1.0", "1.4", null, null, null, null,
                test_components, null, null);

        VEX.Builder testVEX = new VEX.Builder();
        String creationTime = String.valueOf(java.time.LocalDateTime.now());
        testVEX.setVEXIdentifier(test_sbom.getName());
        testVEX.setOriginType(VEXType.CYCLONE_DX);
        testVEX.setSpecVersion("1.4");
        testVEX.setDocVersion("1.0");
        testVEX.setTimeFirstIssued(creationTime);
        testVEX.setTimeLastUpdated(creationTime);

        List<VEXStatement> statements = new ArrayList<>(client.getVEXStatements(test_component1));

        for(VEXStatement v :statements){
            testVEX.addVEXStatement(v);
        }

        VEX vex = testVEX.build();
        ObjectMapper objectMapper = new ObjectMapper();
        // pretty print
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(vex);
        System.out.println(json);
    }

    @Test
    public void test_no_vulnerabilities_with_purl_test() throws Exception {
        Set<String> test_purl = new HashSet<>(List.of(
                "pkg:maven/com.fasterxml.jackson.core/jackson-core@2.13.3?type=jar"
        ));

        CDX14ComponentObject test_component1 = new CDX14ComponentObject(
                null, null, null, "jackson-core", null, null,
                null, null, "2.13.3", null, null, test_purl,
                null, null, null, null, null, null);

        List<VEXStatement> statements = new ArrayList<>(client.getVEXStatements(test_component1));
        assert(statements.isEmpty());
    }

    @Test
    public void test_no_vulnerabilities_with_name_version_test() throws Exception {

        CDX14ComponentObject test_component1 = new CDX14ComponentObject(
                null, null, "com.fasterxml.jackson.core", "jackson-core", null, null,
                null, null, "2.13.3", null, null, null,
                null, null, null, null, null, null);

        List<VEXStatement> statements = new ArrayList<>(client.getVEXStatements(test_component1));
        assert(statements.isEmpty());
    }
}