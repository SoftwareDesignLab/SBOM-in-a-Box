package org.svip.sbomvex;

import org.svip.sbomvex.database.mockNetworking.*;

import org.svip.sbom.model.*;
import org.svip.sbomvex.VEXFactory;
import org.svip.sbomvex.model.Vulnerability;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class VEXFactoryTest {

    @Test
    public void given_twoValidComponentsInSBOM_whenApplyVex_theAddVulnerabilitiesToBoth() {
        ArrayList<mockHTTPConversation> knownConversations = new ArrayList<>();

        knownConversations.add(makeTokenRequestConversation());
        knownConversations.add(makeVulnLookupConversationRealData());
        knownConversations.add(makeVulnLookupConversationShort());

        VEXFactory factory = new mockVEXFactory(knownConversations);
        try {
            factory.doLogin("username", "realPassword123!!!");
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }

        Component c1 = new Component("windows", "microsoft", "xp");
        c1.setCpes(new HashSet<>(List.of(new String[]{"cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*"})));

        Component c2 = new Component("product", "publisher", "0.0.0");
        c2.setCpes(new HashSet<>(List.of(new String[]{"thisisacpe"})));

        SBOM sbom = new SBOM("SBOM", "1.0", "1.0", "someSupplier", "1", "2021-01-01", null, new DependencyTree());
        sbom.addComponent(null, c1);
        sbom.addComponent(null, c2);

        assertEquals("username", factory.getUsername()); //if this fails did you remember to login?
        try {
            factory.applyVex(sbom);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }

        Set<Component> finalComponents = sbom.getAllComponents();
        ArrayList<Component> finalComponentList = new ArrayList<>(finalComponents);

        assertEquals(2, finalComponentList.size());

        //check hashes
        int[] validHashes = {1802764288, -1386628427};
        for (Component c : sbom.getAllComponents()) {
            boolean found = false;
            for (int i = 0; i < validHashes.length; i++) {
                if (c.hashCode() == validHashes[i]) {
                    found = true;
                    break;
                }
            }
            if(!found){
                System.err.println("Component with hash " + c.hashCode() + " was not found in the valid hashes list. This component is \n" + c.toString());
                System.err.println("The full vulnerability list was:");
                System.err.println(c.getVulnerabilities().toString());

                System.err.println("The expected component probably was: ");
                if(c.getName().equals("windows"))
                    System.err.println("[Vulnerability{vulnId='2415113', cveId='CVE-2005-0061', description='The kernel of Microsoft Windows 2000, Windows XP SP1 and SP2, and Windows Server 2003 allows local users to gain privileges via certain access requests.', platform='N/A', introducedDate='null', publishedDate='2023-02-08 18:14:43', createdDate='null', lastModifiedDate='2023-02-08 18:14:43', fixedDate='N/A', existsAtMitre=true, existsAtNvd=true, timeGapNvd=0, timeGapMitre=0, statusId=-1, vexFormatIdentifier='SVIP-VEX', vexAuthor='SVIP Auto-Generated', vexAuthorRole='N/A', productIdentifier='cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*', productStatusDetails='Under Review'}, Vulnerability{vulnId='2414478', cveId='CVE-2008-4036', description='Integer overflow in Memory Manager in Microsoft Windows XP SP2 and SP3, Server 2003 SP1 and SP2, Vista Gold and SP1, and Server 2008 allows local users to gain privileges via a crafted application that triggers an erroneous decrement of a variable, related to validation of parameters for Virtual Address Descriptors (VADs) and a \\\"memory allocation mapping error,\\\" aka \\\"Virtual Address Descriptor Elevation of Privilege Vulnerability.\\\"', platform='N/A', introducedDate='null', publishedDate='2023-02-08 18:14:43', createdDate='null', lastModifiedDate='2023-02-08 18:14:43', fixedDate='N/A', existsAtMitre=true, existsAtNvd=true, timeGapNvd=0, timeGapMitre=0, statusId=-1, vexFormatIdentifier='SVIP-VEX', vexAuthor='SVIP Auto-Generated', vexAuthorRole='N/A', productIdentifier='cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*', productStatusDetails='Under Review'}, Vulnerability{vulnId='2414709', cveId='CVE-2008-4114', description='srv.sys in the Server service in Microsoft Windows 2000 SP4, XP SP2 and SP3, Server 2003 SP1 and SP2, Vista Gold and SP1, and Server 2008 allows remote attackers to cause a denial of service (system crash) or possibly have unspecified other impact via an SMB WRITE_ANDX packet with an offset that is inconsistent with the packet size, related to \\\"insufficiently validating the buffer size,\\\" as demonstrated by a request to the \\\\PIPE\\\\lsarpc named pipe, aka \\\"SMB Validation Denial of Service Vulnerability.\\\"', platform='N/A', introducedDate='null', publishedDate='2023-02-08 18:14:43', createdDate='null', lastModifiedDate='2023-02-08 18:14:43', fixedDate='N/A', existsAtMitre=true, existsAtNvd=true, timeGapNvd=0, timeGapMitre=0, statusId=-1, vexFormatIdentifier='SVIP-VEX', vexAuthor='SVIP Auto-Generated', vexAuthorRole='N/A', productIdentifier='cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*', productStatusDetails='Under Review'}, Vulnerability{vulnId='2415056', cveId='CVE-2005-0044', description='The OLE component in Windows 98, 2000, XP, and Server 2003, and Exchange Server 5.0 through 2003, does not properly validate the lengths of messages for certain OLE data, which allows remote attackers to execute arbitrary code, aka the \\\"Input Validation Vulnerability.\\\"', platform='N/A', introducedDate='null', publishedDate='2023-02-08 18:14:43', createdDate='null', lastModifiedDate='2023-02-08 18:14:43', fixedDate='N/A', existsAtMitre=true, existsAtNvd=true, timeGapNvd=0, timeGapMitre=0, statusId=-1, vexFormatIdentifier='SVIP-VEX', vexAuthor='SVIP Auto-Generated', vexAuthorRole='N/A', productIdentifier='cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*', productStatusDetails='Under Review'}, Vulnerability{vulnId='2414701', cveId='CVE-2008-4127', description='Mshtml.dll in Microsoft Internet Explorer 7 Gold 7.0.5730 and 8 Beta 8.0.6001 on Windows XP SP2 allows remote attackers to cause a denial of service (failure of subsequent image rendering) via a crafted PNG file, related to an infinite loop in the CDwnTaskExec::ThreadExec function.', platform='N/A', introducedDate='null', publishedDate='2023-02-08 18:14:43', createdDate='null', lastModifiedDate='2023-02-08 18:14:43', fixedDate='N/A', existsAtMitre=true, existsAtNvd=true, timeGapNvd=0, timeGapMitre=0, statusId=-1, vexFormatIdentifier='SVIP-VEX', vexAuthor='SVIP Auto-Generated', vexAuthorRole='N/A', productIdentifier='cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*', productStatusDetails='Under Review'}, Vulnerability{vulnId='2414513', cveId='CVE-2008-4020', description='Cross-site scripting (XSS) vulnerability in Microsoft Office XP SP3 allows remote attackers to inject arbitrary web script or HTML via a document that contains a \\\"Content-Disposition: attachment\\\" header and is accessed through a cdo: URL, which renders the content instead of raising a File Download dialog box, aka \\\"Vulnerability in Content-Disposition Header Vulnerability.\\\"', platform='N/A', introducedDate='null', publishedDate='2023-02-08 18:14:43', createdDate='null', lastModifiedDate='2023-02-08 18:14:43', fixedDate='N/A', existsAtMitre=true, existsAtNvd=true, timeGapNvd=0, timeGapMitre=0, statusId=-1, vexFormatIdentifier='SVIP-VEX', vexAuthor='SVIP Auto-Generated', vexAuthorRole='N/A', productIdentifier='cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*', productStatusDetails='Under Review'}, Vulnerability{vulnId='2414471', cveId='CVE-2012-1528', description='Integer overflow in Windows Shell in Microsoft Windows XP SP2 and SP3, Windows Server 2003 SP2, Windows Vista SP2, Windows Server 2008 SP2, R2, and R2 SP1, Windows 7 Gold and SP1, Windows 8, and Windows Server 2012 allows local users to gain privileges via a crafted briefcase, aka \\\"Windows Briefcase Integer Overflow Vulnerability.\\\"', platform='N/A', introducedDate='null', publishedDate='2023-02-08 18:14:43', createdDate='null', lastModifiedDate='2023-02-08 18:14:43', fixedDate='N/A', existsAtMitre=true, existsAtNvd=true, timeGapNvd=0, timeGapMitre=0, statusId=-1, vexFormatIdentifier='SVIP-VEX', vexAuthor='SVIP Auto-Generated', vexAuthorRole='N/A', productIdentifier='cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*', productStatusDetails='Under Review'}, Vulnerability{vulnId='2415112', cveId='CVE-2005-0060', description='Buffer overflow in the font processing component of Microsoft Windows 2000, Windows XP SP1 and SP2, and Windows Server 2003 allows local users to gain privileges via a specially-designed application.', platform='N/A', introducedDate='null', publishedDate='2023-02-08 18:14:43', createdDate='null', lastModifiedDate='2023-02-08 18:14:43', fixedDate='N/A', existsAtMitre=true, existsAtNvd=true, timeGapNvd=0, timeGapMitre=0, statusId=-1, vexFormatIdentifier='SVIP-VEX', vexAuthor='SVIP Auto-Generated', vexAuthorRole='N/A', productIdentifier='cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*', productStatusDetails='Under Review'}, Vulnerability{vulnId='2415060', cveId='CVE-2005-0048', description='Microsoft Windows XP SP2 and earlier, 2000 SP3 and SP4, Server 2003, and older operating systems allows remote attackers to cause a denial of service and possibly execute arbitrary code via crafted IP packets with malformed options, aka the \\\"IP Validation Vulnerability.\\\"', platform='N/A', introducedDate='null', publishedDate='2023-02-08 18:14:43', createdDate='null', lastModifiedDate='2023-02-08 18:14:43', fixedDate='N/A', existsAtMitre=true, existsAtNvd=true, timeGapNvd=0, timeGapMitre=0, statusId=-1, vexFormatIdentifier='SVIP-VEX', vexAuthor='SVIP Auto-Generated', vexAuthorRole='N/A', productIdentifier='cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*', productStatusDetails='Under Review'}, Vulnerability{vulnId='2415103', cveId='CVE-2005-0051', description='The Server service (srvsvc.dll) in Windows XP SP1 and SP2 allows remote attackers to obtain sensitive information (users who are accessing resources) via an anonymous logon using a named pipe, which is not properly authenticated, aka the \\\"Named Pipe Vulnerability.\\\"', platform='N/A', introducedDate='null', publishedDate='2023-02-08 18:14:43', createdDate='null', lastModifiedDate='2023-02-08 18:14:43', fixedDate='N/A', existsAtMitre=true, existsAtNvd=true, timeGapNvd=0, timeGapMitre=0, statusId=-1, vexFormatIdentifier='SVIP-VEX', vexAuthor='SVIP Auto-Generated', vexAuthorRole='N/A', productIdentifier='cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*', productStatusDetails='Under Review'}, Vulnerability{vulnId='2415110', cveId='CVE-2005-0058', description='Buffer overflow in the Telephony Application Programming Interface (TAPI) for Microsoft Windows 98, Windows 98 SE, Windows ME, Windows 2000, Windows XP, and Windows Server 2003 allows attackers to elevate privileges or execute arbitrary code via a crafted message.', platform='N/A', introducedDate='null', publishedDate='2023-02-08 18:14:43', createdDate='null', lastModifiedDate='2023-02-08 18:14:43', fixedDate='N/A', existsAtMitre=true, existsAtNvd=true, timeGapNvd=0, timeGapMitre=0, statusId=-1, vexFormatIdentifier='SVIP-VEX', vexAuthor='SVIP Auto-Generated', vexAuthorRole='N/A', productIdentifier='cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*', productStatusDetails='Under Review'}, Vulnerability{vulnId='2414489', cveId='CVE-2012-1537', description='Heap-based buffer overflow in DirectPlay in DirectX 9.0 through 11.1 in Microsoft Windows XP SP2 and SP3, Windows Server 2003 SP2, Windows Vista SP2, Windows Server 2008 SP2, R2, and R2 SP1, Windows 7 Gold and SP1, Windows 8, and Windows Server 2012 allows remote attackers to execute arbitrary code via a crafted Office document, aka \\\"DirectPlay Heap Overflow Vulnerability.\\\"', platform='N/A', introducedDate='null', publishedDate='2023-02-08 18:14:43', createdDate='null', lastModifiedDate='2023-02-08 18:14:43', fixedDate='N/A', existsAtMitre=true, existsAtNvd=true, timeGapNvd=0, timeGapMitre=0, statusId=-1, vexFormatIdentifier='SVIP-VEX', vexAuthor='SVIP Auto-Generated', vexAuthorRole='N/A', productIdentifier='cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*', productStatusDetails='Under Review'}, Vulnerability{vulnId='2414481', cveId='CVE-2008-4038', description='Buffer underflow in Microsoft Windows 2000 SP4, XP SP2 and SP3, Server 2003 SP1 and SP2, Vista Gold and SP1, and Server 2008 allows remote attackers to execute arbitrary code via a Server Message Block (SMB) request that contains a filename with a crafted length, aka \\\"SMB Buffer Underflow Vulnerability.\\\"', platform='N/A', introducedDate='null', publishedDate='2023-02-08 18:14:43', createdDate='null', lastModifiedDate='2023-02-08 18:14:43', fixedDate='N/A', existsAtMitre=true, existsAtNvd=true, timeGapNvd=0, timeGapMitre=0, statusId=-1, vexFormatIdentifier='SVIP-VEX', vexAuthor='SVIP Auto-Generated', vexAuthorRole='N/A', productIdentifier='cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*', productStatusDetails='Under Review'}, Vulnerability{vulnId='2415111', cveId='CVE-2005-0059', description='Buffer overflow in the Message Queuing component of Microsoft Windows 2000 and Windows XP SP1 allows remote attackers to execute arbitrary code via a crafted message.', platform='N/A', introducedDate='null', publishedDate='2023-02-08 18:14:43', createdDate='null', lastModifiedDate='2023-02-08 18:14:43', fixedDate='N/A', existsAtMitre=true, existsAtNvd=true, timeGapNvd=0, timeGapMitre=0, statusId=-1, vexFormatIdentifier='SVIP-VEX', vexAuthor='SVIP Auto-Generated', vexAuthorRole='N/A', productIdentifier='cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*', productStatusDetails='Under Review'}, Vulnerability{vulnId='2414469', cveId='CVE-2012-1527', description='Integer underflow in Windows Shell in Microsoft Windows XP SP2 and SP3, Windows Server 2003 SP2, Windows Vista SP2, Windows Server 2008 SP2, R2, and R2 SP1, Windows 7 Gold and SP1, Windows 8, and Windows Server 2012 allows local users to gain privileges via a crafted briefcase, aka \\\"Windows Briefcase Integer Underflow Vulnerability.\\\"', platform='N/A', introducedDate='null', publishedDate='2023-02-08 18:14:43', createdDate='null', lastModifiedDate='2023-02-08 18:14:43', fixedDate='N/A', existsAtMitre=true, existsAtNvd=true, timeGapNvd=0, timeGapMitre=0, statusId=-1, vexFormatIdentifier='SVIP-VEX', vexAuthor='SVIP Auto-Generated', vexAuthorRole='N/A', productIdentifier='cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*', productStatusDetails='Under Review'}]\n");
                else if(c.getName().equals("product"))
                    System.err.println("[Vulnerability{vulnId='100', cveId='CVE-0000-1000', description='somethingFunny', platform='N/A', introducedDate='null', publishedDate='1970-01-01 00:00:01', createdDate='null', lastModifiedDate='1980-01-01 00:00:01', fixedDate='N/A', existsAtMitre=true, existsAtNvd=true, timeGapNvd=0, timeGapMitre=0, statusId=-1, vexFormatIdentifier='SVIP-VEX', vexAuthor='SVIP Auto-Generated', vexAuthorRole='N/A', productIdentifier='thisisacpe', productStatusDetails='Under Review'}]");
                else
                    System.err.println("Unrecognizable component: " + c.getName());
                fail();
            }
        }
    }

    /**
     * Tests the vulnerabilityLookup method's uniqueness check logic.
     */
    @Test
    public void givenDuplicateComponent_whenVulnLookup_thenDoNotAddDuplicate() {
        ArrayList<mockHTTPConversation> knownConversations = new ArrayList<>();

        knownConversations.add(makeTokenRequestConversation());
        knownConversations.add(makeVulnLookupConversationShort());

        VEXFactory factory = new mockVEXFactory(knownConversations);
        Component c = new Component("product", "publisher", "0.0.0");
        c.setCpes(new HashSet<>(List.of(new String[]{"thisisacpe"})));

        //add duplicate
        c.addVulnerability(new Vulnerability("100", "CVE-0000-1000", "somethingFunny", "N/A", "null", "1970-01-01 00:00:01", "null", "1980-01-01 00:00:01", "N/A", true, true, 0, 0, -1, "SVIP-VEX", "SVIP Auto-Generated", "N/A", "thisisacpe", "Under Review"));

        try {
            factory.doLogin("username", "realPassword123!!!");
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage() + "\nThis is most likely an issue with the login conversation.");
        }
        try {
            factory.applyVexToComponent(c);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }

        //check that nothing was added
        assertEquals(1, c.getVulnerabilities().size());
    }


    /**
     * Sets up two mock HTTP conversations then tests vulnerabilityLookup.
     */
    @Test
    public void givenValidComponent_whenVulnLookup_thenReturnComponentWithVEX() {
        ArrayList<mockHTTPConversation> knownConversations = new ArrayList<>();

        knownConversations.add(makeTokenRequestConversation());
        knownConversations.add(makeVulnLookupConversationRealData());

        VEXFactory factory = new mockVEXFactory(knownConversations);
        Component c = new Component("windows", "microsoft", "xp");
        c.setCpes(new HashSet<>(List.of(new String[]{"cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*"})));

        try {
            factory.doLogin("username", "realPassword123!!!");
            assertEquals("username", factory.getUsername()); //if this fails did you remember to login?
            factory.applyVexToComponent(c);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }

        assertEquals(15, c.getVulnerabilities().size());
        assertEquals(1802764288, c.hashCode());

    }

    @Test
    public void givenValidArguments_whenBuildRequest_thenBuildGoodURI(){
        VEXFactory factory = new VEXFactory();
        ArrayList<String> params = new ArrayList<>();
        params.add("test"); params.add("thisCode");

        HttpRequest request = null;
        try {
            request = factory.buildRequest("fakeResource", "user", "kjswbfkigsaf", params);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
        HttpRequest good = HttpRequest.newBuilder()
                .uri(URI.create("https://thisWillNotResolve.invalid/fakeResource?username=user&token=kjswbfkigsaf&test=thisCode"))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        assertEquals(good, request);
    }

    @Test
    public void given200_whenDoErrorHandling_thenDoNothing(){
        VEXFactory factory = new VEXFactory();
        mockHttpResponse response = new mockHttpResponse(200, "OK: This is a test response");
        try {
            factory.doErrorHandling(response);
        } catch (Exception e) {
            fail("no exception should be thrown for 200");
        }

        //pass the test if no exception is thrown
        return;
    }

    @Test
    public void givenNon200_whenDoErrorHandling_thenRaiseException(){
        VEXFactory factory = new VEXFactory();
        mockHttpResponse response = new mockHttpResponse(404, "something went wrong");
        try {
            factory.doErrorHandling(response);
        } catch (Exception e) {
            return;
        }

        //pass the test if no exception is thrown
        fail("should have thrown an exception");
    }

    /**
     * Uses fake HTTP requests to login to NVIP. Should succeed.
     *
     * The expected credentials for the mockHTTPConversation are username/realPassword123!!!
     */
    @Test
    public void givenValidCreds_whenDoLogin_thenSetToken() {
        ArrayList<mockHTTPConversation> knownConversations = new ArrayList<>();
        knownConversations.add(makeTokenRequestConversation());

        VEXFactory factory = new mockVEXFactory(knownConversations);
        try {
            factory.doLogin("username", "realPassword123!!!");
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }

        assertEquals("51f6d822203a36299351f427ca0dc19837f8fca9c3fc8b661844ad047efe5a4ef333d9f91ac5372a42f12fd4281ec0ed61ab0c72ca5f1196a735c742216bb281", factory.getToken());
        assertEquals("username", factory.getUsername());
    }

    /**
     * Tests URL safe with a bunch of random characters
     */
    @Test
    public void givenRandomEncodableCharacters_whenMakeURLSafe_thenReturnSafeURL() {
        VEXFactory factory = new VEXFactory();

        //random characters
        String actual = factory.urlSafe("  !+-^*s*((b)@#$\\|'  .,<>?/\"    `~<&TestString");
        String expected = "++%21%2B-%5E*s*%28%28b%29%40%23%24%5C%7C%27++.%2C%3C%3E%3F%2F%22++++%60%7E%3C%26TestString";

        assertEquals(expected, actual);
    }

    /**
     * Tests the urlSafe function with a CPE
     */
    @Test
    public void givenCPE_whenMakeURLSafe_thenReturnSafeURL() {
        VEXFactory factory = new VEXFactory();

        String actual = factory.urlSafe("cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*");
        String expected = "cpe%3A2.3%3Ao%3Amicrosoft%3Awindows-nt%3Axp%3Asp2%3Ax64%3A*%3A*%3A*%3A*%3A*";

        assertEquals(expected, actual);
    }

    /**
     * This function creates a mock HTTP conversation which allows doLogin to get a token
     * @return - filled out mockHTTPconversation
     */
    private static mockHTTPConversation makeTokenRequestConversation(){
        ArrayList<String> tokenRequestParams = new ArrayList<>();
        tokenRequestParams.add("userName"); tokenRequestParams.add("username");
        tokenRequestParams.add("passwordHash"); tokenRequestParams.add("realPassword123!!!");
        String tokenRequestExpectedResponse = "{\"userID\":4,\"token\":\"51f6d822203a36299351f427ca0dc19837f8fca9c3fc8b661844ad047efe5a4ef333d9f91ac5372a42f12fd4281ec0ed61ab0c72ca5f1196a735c742216bb281\",\"userName\":\"username\",\"firstName\":\"root\",\"lastName\":\"rooty\",\"roleId\":2,\"expirationDate\":{\"date\":{\"year\":2023,\"month\":2,\"day\":21},\"time\":{\"hour\":16,\"minute\":39,\"second\":15,\"nano\":275000000}}}";
        return new mockHTTPConversation("loginServlet", null, null, tokenRequestParams, new mockHttpResponse(200, tokenRequestExpectedResponse));
    }


    private static mockHTTPConversation makeVulnLookupConversationShort(){
        ArrayList<String> cpeLookupParams = new ArrayList<>();
        cpeLookupParams.add("product"); cpeLookupParams.add("thisisacpe");
        cpeLookupParams.add("limitCount"); cpeLookupParams.add("300");
        String cpeLookupExpectedResponse = """
            [
              {
                "vulnId": 100,
                "cveId": "CVE-0000-1000",
                "description": "somethingFunny",
                "platform": "N/A",
                "patch": "N/A",
                "fixDate": "N/A",
                "publishedDate": "1970-01-01 00:00:01",
                "lastModifiedDate": "1980-01-01 00:00:01",
                "existInMitre": true,
                "existInNvd": true,
                "timeGapNvd": 0,
                "timeGapMitre": 0,
                "sources": [],
                "vdoList": [],
                "cvssScoreList": [
                  {
                    "cveId": "CVE-0000-1000",
                    "baseSeverity": "HIGH",
                    "severityConfidence": 1,
                    "impactScore": "10.0",
                    "impactConfidence": 1
                  }
                ],
                "products": [
                  {
                    "domain": "Product",
                    "cpe": "thisisacpe",
                    "productId": 4980,
                    "version": "0.0.0"
                  }
                ],
                "cpes": [
                  "thisisacpe"
                ],
                "status": "Under Review",
                "type": "Trust Failure",
                "discoveredBy": "N/A",
                "domain": "product",
                "exploitPublishDate": "N/A",
                "exploitUrl": "N/A"
              },
              1
            ]""";
        return new mockHTTPConversation("searchServlet", "username", "51f6d822203a36299351f427ca0dc19837f8fca9c3fc8b661844ad047efe5a4ef333d9f91ac5372a42f12fd4281ec0ed61ab0c72ca5f1196a735c742216bb281", cpeLookupParams, new mockHttpResponse(200, cpeLookupExpectedResponse));
    }

    /**
     * This function creates a mock HTTP conversation which allows vulnerabilityLookup to find vulnerabilities from NVIP
     * @return - filled out mockHTTPconversation
     */
    private static mockHTTPConversation makeVulnLookupConversationRealData(){
        ArrayList<String> cpeLookupParams = new ArrayList<>();
        cpeLookupParams.add("product"); cpeLookupParams.add("cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*");
        cpeLookupParams.add("limitCount"); cpeLookupParams.add("300");
        String cpeLookupExpectedResponse = """
                [
                  {
                    "vulnId": 2415113,
                    "cveId": "CVE-2005-0061",
                    "description": "The kernel of Microsoft Windows 2000, Windows XP SP1 and SP2, and Windows Server 2003 allows local users to gain privileges via certain access requests.",
                    "platform": "N/A",
                    "patch": "N/A",
                    "fixDate": "N/A",
                    "publishedDate": "2023-02-08 18:14:43",
                    "lastModifiedDate": "2023-02-08 18:14:43",
                    "existInMitre": true,
                    "existInNvd": true,
                    "timeGapNvd": 0,
                    "timeGapMitre": 0,
                    "sources": [],
                    "vdoList": [
                      {
                        "cveId": "CVE-2005-0061",
                        "vdoLabel": "Application",
                        "vdoConfidence": 0.431,
                        "vdoNounGroup": "Context"
                      },
                      {
                        "cveId": "CVE-2005-0061",
                        "vdoLabel": "Host OS",
                        "vdoConfidence": 0.259,
                        "vdoNounGroup": "Context"
                      },
                      {
                        "cveId": "CVE-2005-0061",
                        "vdoLabel": "Sandboxed",
                        "vdoConfidence": 0.405,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2005-0061",
                        "vdoLabel": "Physical Security",
                        "vdoConfidence": 0.285,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2005-0061",
                        "vdoLabel": "Limited Rmt",
                        "vdoConfidence": 0.485,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2005-0061",
                        "vdoLabel": "Local",
                        "vdoConfidence": 0.408,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2005-0061",
                        "vdoLabel": "Trust Failure",
                        "vdoConfidence": 0.615,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2005-0061",
                        "vdoLabel": "Read",
                        "vdoConfidence": 0.397,
                        "vdoNounGroup": "LogicalImpact"
                      },
                      {
                        "cveId": "CVE-2005-0061",
                        "vdoLabel": "Resource Removal",
                        "vdoConfidence": 0.302,
                        "vdoNounGroup": "LogicalImpact"
                      }
                    ],
                    "cvssScoreList": [
                      {
                        "cveId": "CVE-2005-0061",
                        "baseSeverity": "HIGH",
                        "severityConfidence": 0.5,
                        "impactScore": "7.4",
                        "impactConfidence": 0.5
                      }
                    ],
                    "products": [
                      {
                        "domain": "Microsoft Windows XP, Service Pack 2 x64",
                        "cpe": "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*",
                        "productId": 74134,
                        "version": "xp"
                      }
                    ],
                    "cpes": [
                      "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*"
                    ],
                    "status": "Under Review",
                    "type": "Trust Failure",
                    "discoveredBy": "N/A",
                    "domain": "microsoft-windows-nt",
                    "exploitPublishDate": "N/A",
                    "exploitUrl": "N/A"
                  },
                  {
                    "vulnId": 2415112,
                    "cveId": "CVE-2005-0060",
                    "description": "Buffer overflow in the font processing component of Microsoft Windows 2000, Windows XP SP1 and SP2, and Windows Server 2003 allows local users to gain privileges via a specially-designed application.",
                    "platform": "N/A",
                    "patch": "N/A",
                    "fixDate": "N/A",
                    "publishedDate": "2023-02-08 18:14:43",
                    "lastModifiedDate": "2023-02-08 18:14:43",
                    "existInMitre": true,
                    "existInNvd": true,
                    "timeGapNvd": 0,
                    "timeGapMitre": 0,
                    "sources": [],
                    "vdoList": [
                      {
                        "cveId": "CVE-2005-0060",
                        "vdoLabel": "Application",
                        "vdoConfidence": 0.697,
                        "vdoNounGroup": "Context"
                      },
                      {
                        "cveId": "CVE-2005-0060",
                        "vdoLabel": "ASLR",
                        "vdoConfidence": 0.698,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2005-0060",
                        "vdoLabel": "Limited Rmt",
                        "vdoConfidence": 0.475,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2005-0060",
                        "vdoLabel": "Local",
                        "vdoConfidence": 0.411,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2005-0060",
                        "vdoLabel": "Trust Failure",
                        "vdoConfidence": 0.619,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2005-0060",
                        "vdoLabel": "Write",
                        "vdoConfidence": 0.382,
                        "vdoNounGroup": "LogicalImpact"
                      },
                      {
                        "cveId": "CVE-2005-0060",
                        "vdoLabel": "Resource Removal",
                        "vdoConfidence": 0.296,
                        "vdoNounGroup": "LogicalImpact"
                      }
                    ],
                    "cvssScoreList": [
                      {
                        "cveId": "CVE-2005-0060",
                        "baseSeverity": "MEDIUM",
                        "severityConfidence": 0.5,
                        "impactScore": "6.5",
                        "impactConfidence": 0.5
                      }
                    ],
                    "products": [
                      {
                        "domain": "Microsoft Windows XP, Service Pack 2 x64",
                        "cpe": "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*",
                        "productId": 74134,
                        "version": "xp"
                      }
                    ],
                    "cpes": [
                      "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*"
                    ],
                    "status": "Under Review",
                    "type": "Trust Failure",
                    "discoveredBy": "N/A",
                    "domain": "microsoft-windows-nt",
                    "exploitPublishDate": "N/A",
                    "exploitUrl": "N/A"
                  },
                  {
                    "vulnId": 2415111,
                    "cveId": "CVE-2005-0059",
                    "description": "Buffer overflow in the Message Queuing component of Microsoft Windows 2000 and Windows XP SP1 allows remote attackers to execute arbitrary code via a crafted message.",
                    "platform": "N/A",
                    "patch": "N/A",
                    "fixDate": "N/A",
                    "publishedDate": "2023-02-08 18:14:43",
                    "lastModifiedDate": "2023-02-08 18:14:43",
                    "existInMitre": true,
                    "existInNvd": true,
                    "timeGapNvd": 0,
                    "timeGapMitre": 0,
                    "sources": [],
                    "vdoList": [
                      {
                        "cveId": "CVE-2005-0059",
                        "vdoLabel": "Application",
                        "vdoConfidence": 0.712,
                        "vdoNounGroup": "Context"
                      },
                      {
                        "cveId": "CVE-2005-0059",
                        "vdoLabel": "ASLR",
                        "vdoConfidence": 0.749,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2005-0059",
                        "vdoLabel": "Remote",
                        "vdoConfidence": 0.704,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2005-0059",
                        "vdoLabel": "Code Execution",
                        "vdoConfidence": 0.669,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2005-0059",
                        "vdoLabel": "Resource Removal",
                        "vdoConfidence": 0.626,
                        "vdoNounGroup": "LogicalImpact"
                      }
                    ],
                    "cvssScoreList": [
                      {
                        "cveId": "CVE-2005-0059",
                        "baseSeverity": "MEDIUM",
                        "severityConfidence": 0.5,
                        "impactScore": "6.5",
                        "impactConfidence": 0.5
                      }
                    ],
                    "products": [
                      {
                        "domain": "Microsoft Windows XP, Service Pack 2 x64",
                        "cpe": "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*",
                        "productId": 74134,
                        "version": "xp"
                      }
                    ],
                    "cpes": [
                      "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*"
                    ],
                    "status": "Under Review",
                    "type": "Code Execution",
                    "discoveredBy": "N/A",
                    "domain": "microsoft-windows-nt",
                    "exploitPublishDate": "N/A",
                    "exploitUrl": "N/A"
                  },
                  {
                    "vulnId": 2415110,
                    "cveId": "CVE-2005-0058",
                    "description": "Buffer overflow in the Telephony Application Programming Interface (TAPI) for Microsoft Windows 98, Windows 98 SE, Windows ME, Windows 2000, Windows XP, and Windows Server 2003 allows attackers to elevate privileges or execute arbitrary code via a crafted message.",
                    "platform": "N/A",
                    "patch": "N/A",
                    "fixDate": "N/A",
                    "publishedDate": "2023-02-08 18:14:43",
                    "lastModifiedDate": "2023-02-08 18:14:43",
                    "existInMitre": true,
                    "existInNvd": true,
                    "timeGapNvd": 0,
                    "timeGapMitre": 0,
                    "sources": [],
                    "vdoList": [
                      {
                        "cveId": "CVE-2005-0058",
                        "vdoLabel": "Application",
                        "vdoConfidence": 0.722,
                        "vdoNounGroup": "Context"
                      },
                      {
                        "cveId": "CVE-2005-0058",
                        "vdoLabel": "ASLR",
                        "vdoConfidence": 0.527,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2005-0058",
                        "vdoLabel": "Physical Security",
                        "vdoConfidence": 0.247,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2005-0058",
                        "vdoLabel": "Limited Rmt",
                        "vdoConfidence": 0.493,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2005-0058",
                        "vdoLabel": "Local",
                        "vdoConfidence": 0.398,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2005-0058",
                        "vdoLabel": "Trust Failure",
                        "vdoConfidence": 0.62,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2005-0058",
                        "vdoLabel": "Write",
                        "vdoConfidence": 0.333,
                        "vdoNounGroup": "LogicalImpact"
                      },
                      {
                        "cveId": "CVE-2005-0058",
                        "vdoLabel": "Resource Removal",
                        "vdoConfidence": 0.297,
                        "vdoNounGroup": "LogicalImpact"
                      }
                    ],
                    "cvssScoreList": [
                      {
                        "cveId": "CVE-2005-0058",
                        "baseSeverity": "MEDIUM",
                        "severityConfidence": 0.5,
                        "impactScore": "6.5",
                        "impactConfidence": 0.5
                      }
                    ],
                    "products": [
                      {
                        "domain": "Microsoft Windows XP, Service Pack 2 x64",
                        "cpe": "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*",
                        "productId": 74134,
                        "version": "xp"
                      }
                    ],
                    "cpes": [
                      "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*"
                    ],
                    "status": "Under Review",
                    "type": "Trust Failure",
                    "discoveredBy": "N/A",
                    "domain": "microsoft-windows-nt",
                    "exploitPublishDate": "N/A",
                    "exploitUrl": "N/A"
                  },
                  {
                    "vulnId": 2415103,
                    "cveId": "CVE-2005-0051",
                    "description": "The Server service (srvsvc.dll) in Windows XP SP1 and SP2 allows remote attackers to obtain sensitive information (users who are accessing resources) via an anonymous logon using a named pipe, which is not properly authenticated, aka the \\\\\\"Named Pipe Vulnerability.\\\\\\"",
                    "platform": "N/A",
                    "patch": "N/A",
                    "fixDate": "N/A",
                    "publishedDate": "2023-02-08 18:14:43",
                    "lastModifiedDate": "2023-02-08 18:14:43",
                    "existInMitre": true,
                    "existInNvd": true,
                    "timeGapNvd": 0,
                    "timeGapMitre": 0,
                    "sources": [],
                    "vdoList": [
                      {
                        "cveId": "CVE-2005-0051",
                        "vdoLabel": "Application",
                        "vdoConfidence": 0.657,
                        "vdoNounGroup": "Context"
                      },
                      {
                        "cveId": "CVE-2005-0051",
                        "vdoLabel": "MultiFactor Authentication",
                        "vdoConfidence": 0.475,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2005-0051",
                        "vdoLabel": "Sandboxed",
                        "vdoConfidence": 0.327,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2005-0051",
                        "vdoLabel": "Remote",
                        "vdoConfidence": 0.679,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2005-0051",
                        "vdoLabel": "Trust Failure",
                        "vdoConfidence": 0.392,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2005-0051",
                        "vdoLabel": "Man-in-the-Middle",
                        "vdoConfidence": 0.277,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2005-0051",
                        "vdoLabel": "Resource Removal",
                        "vdoConfidence": 0.541,
                        "vdoNounGroup": "LogicalImpact"
                      },
                      {
                        "cveId": "CVE-2005-0051",
                        "vdoLabel": "Read",
                        "vdoConfidence": 0.165,
                        "vdoNounGroup": "LogicalImpact"
                      }
                    ],
                    "cvssScoreList": [
                      {
                        "cveId": "CVE-2005-0051",
                        "baseSeverity": "MEDIUM",
                        "severityConfidence": 0.5,
                        "impactScore": "6.1",
                        "impactConfidence": 0.5
                      }
                    ],
                    "products": [
                      {
                        "domain": "Microsoft Windows XP, Service Pack 2 x64",
                        "cpe": "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*",
                        "productId": 74134,
                        "version": "xp"
                      }
                    ],
                    "cpes": [
                      "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*"
                    ],
                    "status": "Under Review",
                    "type": "Trust Failure, Man-in-the-Middle",
                    "discoveredBy": "N/A",
                    "domain": "microsoft-windows-nt",
                    "exploitPublishDate": "N/A",
                    "exploitUrl": "N/A"
                  },
                  {
                    "vulnId": 2415060,
                    "cveId": "CVE-2005-0048",
                    "description": "Microsoft Windows XP SP2 and earlier, 2000 SP3 and SP4, Server 2003, and older operating systems allows remote attackers to cause a denial of service and possibly execute arbitrary code via crafted IP packets with malformed options, aka the \\\\\\"IP Validation Vulnerability.\\\\\\"",
                    "platform": "N/A",
                    "patch": "N/A",
                    "fixDate": "N/A",
                    "publishedDate": "2023-02-08 18:14:43",
                    "lastModifiedDate": "2023-02-08 18:14:43",
                    "existInMitre": true,
                    "existInNvd": true,
                    "timeGapNvd": 0,
                    "timeGapMitre": 0,
                    "sources": [],
                    "vdoList": [
                      {
                        "cveId": "CVE-2005-0048",
                        "vdoLabel": "Application",
                        "vdoConfidence": 0.682,
                        "vdoNounGroup": "Context"
                      },
                      {
                        "cveId": "CVE-2005-0048",
                        "vdoLabel": "Sandboxed",
                        "vdoConfidence": 0.405,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2005-0048",
                        "vdoLabel": "Physical Security",
                        "vdoConfidence": 0.234,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2005-0048",
                        "vdoLabel": "Limited Rmt",
                        "vdoConfidence": 0.474,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2005-0048",
                        "vdoLabel": "Local",
                        "vdoConfidence": 0.421,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2005-0048",
                        "vdoLabel": "Trust Failure",
                        "vdoConfidence": 0.61,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2005-0048",
                        "vdoLabel": "Resource Removal",
                        "vdoConfidence": 0.411,
                        "vdoNounGroup": "LogicalImpact"
                      },
                      {
                        "cveId": "CVE-2005-0048",
                        "vdoLabel": "Read",
                        "vdoConfidence": 0.297,
                        "vdoNounGroup": "LogicalImpact"
                      }
                    ],
                    "cvssScoreList": [
                      {
                        "cveId": "CVE-2005-0048",
                        "baseSeverity": "HIGH",
                        "severityConfidence": 0.5,
                        "impactScore": "7.4",
                        "impactConfidence": 0.5
                      }
                    ],
                    "products": [
                      {
                        "domain": "Microsoft Windows XP, Service Pack 2 x64",
                        "cpe": "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*",
                        "productId": 74134,
                        "version": "xp"
                      }
                    ],
                    "cpes": [
                      "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*"
                    ],
                    "status": "Under Review",
                    "type": "Trust Failure",
                    "discoveredBy": "N/A",
                    "domain": "microsoft-windows-nt",
                    "exploitPublishDate": "N/A",
                    "exploitUrl": "N/A"
                  },
                  {
                    "vulnId": 2415056,
                    "cveId": "CVE-2005-0044",
                    "description": "The OLE component in Windows 98, 2000, XP, and Server 2003, and Exchange Server 5.0 through 2003, does not properly validate the lengths of messages for certain OLE data, which allows remote attackers to execute arbitrary code, aka the \\\\\\"Input Validation Vulnerability.\\\\\\"",
                    "platform": "N/A",
                    "patch": "N/A",
                    "fixDate": "N/A",
                    "publishedDate": "2023-02-08 18:14:43",
                    "lastModifiedDate": "2023-02-08 18:14:43",
                    "existInMitre": true,
                    "existInNvd": true,
                    "timeGapNvd": 0,
                    "timeGapMitre": 0,
                    "sources": [],
                    "vdoList": [
                      {
                        "cveId": "CVE-2005-0044",
                        "vdoLabel": "Application",
                        "vdoConfidence": 0.687,
                        "vdoNounGroup": "Context"
                      },
                      {
                        "cveId": "CVE-2005-0044",
                        "vdoLabel": "Sandboxed",
                        "vdoConfidence": 0.335,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2005-0044",
                        "vdoLabel": "Physical Security",
                        "vdoConfidence": 0.286,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2005-0044",
                        "vdoLabel": "Limited Rmt",
                        "vdoConfidence": 0.485,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2005-0044",
                        "vdoLabel": "Local",
                        "vdoConfidence": 0.408,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2005-0044",
                        "vdoLabel": "Trust Failure",
                        "vdoConfidence": 0.617,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2005-0044",
                        "vdoLabel": "Read",
                        "vdoConfidence": 0.361,
                        "vdoNounGroup": "LogicalImpact"
                      },
                      {
                        "cveId": "CVE-2005-0044",
                        "vdoLabel": "Resource Removal",
                        "vdoConfidence": 0.34,
                        "vdoNounGroup": "LogicalImpact"
                      }
                    ],
                    "cvssScoreList": [
                      {
                        "cveId": "CVE-2005-0044",
                        "baseSeverity": "HIGH",
                        "severityConfidence": 0.5,
                        "impactScore": "7.4",
                        "impactConfidence": 0.5
                      }
                    ],
                    "products": [
                      {
                        "domain": "Microsoft Windows XP, Service Pack 2 x64",
                        "cpe": "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*",
                        "productId": 74134,
                        "version": "xp"
                      }
                    ],
                    "cpes": [
                      "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*"
                    ],
                    "status": "Under Review",
                    "type": "Trust Failure",
                    "discoveredBy": "N/A",
                    "domain": "microsoft-windows-nt",
                    "exploitPublishDate": "N/A",
                    "exploitUrl": "N/A"
                  },
                  {
                    "vulnId": 2414709,
                    "cveId": "CVE-2008-4114",
                    "description": "srv.sys in the Server service in Microsoft Windows 2000 SP4, XP SP2 and SP3, Server 2003 SP1 and SP2, Vista Gold and SP1, and Server 2008 allows remote attackers to cause a denial of service (system crash) or possibly have unspecified other impact via an SMB WRITE_ANDX packet with an offset that is inconsistent with the packet size, related to \\\\\\"insufficiently validating the buffer size,\\\\\\" as demonstrated by a request to the \\\\\\\\PIPE\\\\\\\\lsarpc named pipe, aka \\\\\\"SMB Validation Denial of Service Vulnerability.\\\\\\"",
                    "platform": "N/A",
                    "patch": "N/A",
                    "fixDate": "N/A",
                    "publishedDate": "2023-02-08 18:14:43",
                    "lastModifiedDate": "2023-02-08 18:14:43",
                    "existInMitre": true,
                    "existInNvd": true,
                    "timeGapNvd": 0,
                    "timeGapMitre": 0,
                    "sources": [],
                    "vdoList": [
                      {
                        "cveId": "CVE-2008-4114",
                        "vdoLabel": "Application",
                        "vdoConfidence": 0.682,
                        "vdoNounGroup": "Context"
                      },
                      {
                        "cveId": "CVE-2008-4114",
                        "vdoLabel": "Sandboxed",
                        "vdoConfidence": 0.35,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2008-4114",
                        "vdoLabel": "Physical Security",
                        "vdoConfidence": 0.284,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2008-4114",
                        "vdoLabel": "Limited Rmt",
                        "vdoConfidence": 0.493,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2008-4114",
                        "vdoLabel": "Local",
                        "vdoConfidence": 0.4,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2008-4114",
                        "vdoLabel": "Trust Failure",
                        "vdoConfidence": 0.624,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2008-4114",
                        "vdoLabel": "Resource Removal",
                        "vdoConfidence": 0.395,
                        "vdoNounGroup": "LogicalImpact"
                      },
                      {
                        "cveId": "CVE-2008-4114",
                        "vdoLabel": "Read",
                        "vdoConfidence": 0.229,
                        "vdoNounGroup": "LogicalImpact"
                      }
                    ],
                    "cvssScoreList": [
                      {
                        "cveId": "CVE-2008-4114",
                        "baseSeverity": "HIGH",
                        "severityConfidence": 0.5,
                        "impactScore": "7.4",
                        "impactConfidence": 0.5
                      }
                    ],
                    "products": [
                      {
                        "domain": "Microsoft Windows XP, Service Pack 2 x64",
                        "cpe": "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*",
                        "productId": 74134,
                        "version": "xp"
                      }
                    ],
                    "cpes": [
                      "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*"
                    ],
                    "status": "Under Review",
                    "type": "Trust Failure",
                    "discoveredBy": "N/A",
                    "domain": "microsoft-windows-nt",
                    "exploitPublishDate": "N/A",
                    "exploitUrl": "N/A"
                  },
                  {
                    "vulnId": 2414701,
                    "cveId": "CVE-2008-4127",
                    "description": "Mshtml.dll in Microsoft Internet Explorer 7 Gold 7.0.5730 and 8 Beta 8.0.6001 on Windows XP SP2 allows remote attackers to cause a denial of service (failure of subsequent image rendering) via a crafted PNG file, related to an infinite loop in the CDwnTaskExec::ThreadExec function.",
                    "platform": "N/A",
                    "patch": "N/A",
                    "fixDate": "N/A",
                    "publishedDate": "2023-02-08 18:14:43",
                    "lastModifiedDate": "2023-02-08 18:14:43",
                    "existInMitre": true,
                    "existInNvd": true,
                    "timeGapNvd": 0,
                    "timeGapMitre": 0,
                    "sources": [],
                    "vdoList": [
                      {
                        "cveId": "CVE-2008-4127",
                        "vdoLabel": "Application",
                        "vdoConfidence": 0.679,
                        "vdoNounGroup": "Context"
                      },
                      {
                        "cveId": "CVE-2008-4127",
                        "vdoLabel": "Sandboxed",
                        "vdoConfidence": 0.389,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2008-4127",
                        "vdoLabel": "ASLR",
                        "vdoConfidence": 0.375,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2008-4127",
                        "vdoLabel": "Remote",
                        "vdoConfidence": 0.729,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2008-4127",
                        "vdoLabel": "Authentication Bypass",
                        "vdoConfidence": 0.458,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2008-4127",
                        "vdoLabel": "Trust Failure",
                        "vdoConfidence": 0.348,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2008-4127",
                        "vdoLabel": "Service Interrupt",
                        "vdoConfidence": 0.657,
                        "vdoNounGroup": "LogicalImpact"
                      }
                    ],
                    "cvssScoreList": [
                      {
                        "cveId": "CVE-2008-4127",
                        "baseSeverity": "HIGH",
                        "severityConfidence": 0.5,
                        "impactScore": "8.6",
                        "impactConfidence": 0.5
                      }
                    ],
                    "products": [
                      {
                        "domain": "Microsoft Windows XP, Service Pack 2 x64",
                        "cpe": "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*",
                        "productId": 74134,
                        "version": "xp"
                      }
                    ],
                    "cpes": [
                      "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*"
                    ],
                    "status": "Under Review",
                    "type": "Authentication Bypass, Trust Failure",
                    "discoveredBy": "N/A",
                    "domain": "microsoft-windows-nt",
                    "exploitPublishDate": "N/A",
                    "exploitUrl": "N/A"
                  },
                  {
                    "vulnId": 2414513,
                    "cveId": "CVE-2008-4020",
                    "description": "Cross-site scripting (XSS) vulnerability in Microsoft Office XP SP3 allows remote attackers to inject arbitrary web script or HTML via a document that contains a \\\\\\"Content-Disposition: attachment\\\\\\" header and is accessed through a cdo: URL, which renders the content instead of raising a File Download dialog box, aka \\\\\\"Vulnerability in Content-Disposition Header Vulnerability.\\\\\\"",
                    "platform": "N/A",
                    "patch": "N/A",
                    "fixDate": "N/A",
                    "publishedDate": "2023-02-08 18:14:43",
                    "lastModifiedDate": "2023-02-08 18:14:43",
                    "existInMitre": true,
                    "existInNvd": true,
                    "timeGapNvd": 0,
                    "timeGapMitre": 0,
                    "sources": [],
                    "vdoList": [
                      {
                        "cveId": "CVE-2008-4020",
                        "vdoLabel": "Application",
                        "vdoConfidence": 0.699,
                        "vdoNounGroup": "Context"
                      },
                      {
                        "cveId": "CVE-2008-4020",
                        "vdoLabel": "Sandboxed",
                        "vdoConfidence": 0.362,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2008-4020",
                        "vdoLabel": "ASLR",
                        "vdoConfidence": 0.283,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2008-4020",
                        "vdoLabel": "Remote",
                        "vdoConfidence": 0.697,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2008-4020",
                        "vdoLabel": "Authentication Bypass",
                        "vdoConfidence": 0.429,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2008-4020",
                        "vdoLabel": "Trust Failure",
                        "vdoConfidence": 0.312,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2008-4020",
                        "vdoLabel": "Resource Removal",
                        "vdoConfidence": 0.637,
                        "vdoNounGroup": "LogicalImpact"
                      }
                    ],
                    "cvssScoreList": [
                      {
                        "cveId": "CVE-2008-4020",
                        "baseSeverity": "HIGH",
                        "severityConfidence": 0.5,
                        "impactScore": "6.8",
                        "impactConfidence": 0.5
                      }
                    ],
                    "products": [
                      {
                        "domain": "Microsoft Windows XP, Service Pack 2 x64",
                        "cpe": "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*",
                        "productId": 74134,
                        "version": "xp"
                      }
                    ],
                    "cpes": [
                      "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*"
                    ],
                    "status": "Under Review",
                    "type": "Authentication Bypass, Trust Failure",
                    "discoveredBy": "N/A",
                    "domain": "microsoft-windows-nt",
                    "exploitPublishDate": "N/A",
                    "exploitUrl": "N/A"
                  },
                  {
                    "vulnId": 2414489,
                    "cveId": "CVE-2012-1537",
                    "description": "Heap-based buffer overflow in DirectPlay in DirectX 9.0 through 11.1 in Microsoft Windows XP SP2 and SP3, Windows Server 2003 SP2, Windows Vista SP2, Windows Server 2008 SP2, R2, and R2 SP1, Windows 7 Gold and SP1, Windows 8, and Windows Server 2012 allows remote attackers to execute arbitrary code via a crafted Office document, aka \\\\\\"DirectPlay Heap Overflow Vulnerability.\\\\\\"",
                    "platform": "N/A",
                    "patch": "N/A",
                    "fixDate": "N/A",
                    "publishedDate": "2023-02-08 18:14:43",
                    "lastModifiedDate": "2023-02-08 18:14:43",
                    "existInMitre": true,
                    "existInNvd": true,
                    "timeGapNvd": 0,
                    "timeGapMitre": 0,
                    "sources": [],
                    "vdoList": [
                      {
                        "cveId": "CVE-2012-1537",
                        "vdoLabel": "Application",
                        "vdoConfidence": 0.469,
                        "vdoNounGroup": "Context"
                      },
                      {
                        "cveId": "CVE-2012-1537",
                        "vdoLabel": "Physical Hardware",
                        "vdoConfidence": 0.325,
                        "vdoNounGroup": "Context"
                      },
                      {
                        "cveId": "CVE-2012-1537",
                        "vdoLabel": "ASLR",
                        "vdoConfidence": 0.763,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2012-1537",
                        "vdoLabel": "Limited Rmt",
                        "vdoConfidence": 0.479,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2012-1537",
                        "vdoLabel": "Local",
                        "vdoConfidence": 0.409,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2012-1537",
                        "vdoLabel": "Trust Failure",
                        "vdoConfidence": 0.615,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2012-1537",
                        "vdoLabel": "Write",
                        "vdoConfidence": 0.373,
                        "vdoNounGroup": "LogicalImpact"
                      },
                      {
                        "cveId": "CVE-2012-1537",
                        "vdoLabel": "Resource Removal",
                        "vdoConfidence": 0.296,
                        "vdoNounGroup": "LogicalImpact"
                      }
                    ],
                    "cvssScoreList": [
                      {
                        "cveId": "CVE-2012-1537",
                        "baseSeverity": "MEDIUM",
                        "severityConfidence": 0.5,
                        "impactScore": "6.5",
                        "impactConfidence": 0.5
                      }
                    ],
                    "products": [
                      {
                        "domain": "Microsoft Windows XP, Service Pack 2 x64",
                        "cpe": "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*",
                        "productId": 74134,
                        "version": "xp"
                      }
                    ],
                    "cpes": [
                      "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*"
                    ],
                    "status": "Under Review",
                    "type": "Trust Failure",
                    "discoveredBy": "N/A",
                    "domain": "microsoft-windows-nt",
                    "exploitPublishDate": "N/A",
                    "exploitUrl": "N/A"
                  },
                  {
                    "vulnId": 2414481,
                    "cveId": "CVE-2008-4038",
                    "description": "Buffer underflow in Microsoft Windows 2000 SP4, XP SP2 and SP3, Server 2003 SP1 and SP2, Vista Gold and SP1, and Server 2008 allows remote attackers to execute arbitrary code via a Server Message Block (SMB) request that contains a filename with a crafted length, aka \\\\\\"SMB Buffer Underflow Vulnerability.\\\\\\"",
                    "platform": "N/A",
                    "patch": "N/A",
                    "fixDate": "N/A",
                    "publishedDate": "2023-02-08 18:14:43",
                    "lastModifiedDate": "2023-02-08 18:14:43",
                    "existInMitre": true,
                    "existInNvd": true,
                    "timeGapNvd": 0,
                    "timeGapMitre": 0,
                    "sources": [],
                    "vdoList": [
                      {
                        "cveId": "CVE-2008-4038",
                        "vdoLabel": "Application",
                        "vdoConfidence": 0.697,
                        "vdoNounGroup": "Context"
                      },
                      {
                        "cveId": "CVE-2008-4038",
                        "vdoLabel": "ASLR",
                        "vdoConfidence": 0.475,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2008-4038",
                        "vdoLabel": "Physical Security",
                        "vdoConfidence": 0.175,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2008-4038",
                        "vdoLabel": "Limited Rmt",
                        "vdoConfidence": 0.485,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2008-4038",
                        "vdoLabel": "Local",
                        "vdoConfidence": 0.406,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2008-4038",
                        "vdoLabel": "Trust Failure",
                        "vdoConfidence": 0.615,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2008-4038",
                        "vdoLabel": "Read",
                        "vdoConfidence": 0.409,
                        "vdoNounGroup": "LogicalImpact"
                      },
                      {
                        "cveId": "CVE-2008-4038",
                        "vdoLabel": "Resource Removal",
                        "vdoConfidence": 0.301,
                        "vdoNounGroup": "LogicalImpact"
                      }
                    ],
                    "cvssScoreList": [
                      {
                        "cveId": "CVE-2008-4038",
                        "baseSeverity": "MEDIUM",
                        "severityConfidence": 0.5,
                        "impactScore": "6.5",
                        "impactConfidence": 0.5
                      }
                    ],
                    "products": [
                      {
                        "domain": "Microsoft Windows XP, Service Pack 2 x64",
                        "cpe": "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*",
                        "productId": 74134,
                        "version": "xp"
                      }
                    ],
                    "cpes": [
                      "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*"
                    ],
                    "status": "Under Review",
                    "type": "Trust Failure",
                    "discoveredBy": "N/A",
                    "domain": "microsoft-windows-nt",
                    "exploitPublishDate": "N/A",
                    "exploitUrl": "N/A"
                  },
                  {
                    "vulnId": 2414478,
                    "cveId": "CVE-2008-4036",
                    "description": "Integer overflow in Memory Manager in Microsoft Windows XP SP2 and SP3, Server 2003 SP1 and SP2, Vista Gold and SP1, and Server 2008 allows local users to gain privileges via a crafted application that triggers an erroneous decrement of a variable, related to validation of parameters for Virtual Address Descriptors (VADs) and a \\\\\\"memory allocation mapping error,\\\\\\" aka \\\\\\"Virtual Address Descriptor Elevation of Privilege Vulnerability.\\\\\\"",
                    "platform": "N/A",
                    "patch": "N/A",
                    "fixDate": "N/A",
                    "publishedDate": "2023-02-08 18:14:43",
                    "lastModifiedDate": "2023-02-08 18:14:43",
                    "existInMitre": true,
                    "existInNvd": true,
                    "timeGapNvd": 0,
                    "timeGapMitre": 0,
                    "sources": [],
                    "vdoList": [
                      {
                        "cveId": "CVE-2008-4036",
                        "vdoLabel": "Application",
                        "vdoConfidence": 0.672,
                        "vdoNounGroup": "Context"
                      },
                      {
                        "cveId": "CVE-2008-4036",
                        "vdoLabel": "Sandboxed",
                        "vdoConfidence": 0.421,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2008-4036",
                        "vdoLabel": "Physical Security",
                        "vdoConfidence": 0.282,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2008-4036",
                        "vdoLabel": "Limited Rmt",
                        "vdoConfidence": 0.48,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2008-4036",
                        "vdoLabel": "Local",
                        "vdoConfidence": 0.406,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2008-4036",
                        "vdoLabel": "Trust Failure",
                        "vdoConfidence": 0.37,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2008-4036",
                        "vdoLabel": "Man-in-the-Middle",
                        "vdoConfidence": 0.304,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2008-4036",
                        "vdoLabel": "Read",
                        "vdoConfidence": 0.31,
                        "vdoNounGroup": "LogicalImpact"
                      },
                      {
                        "cveId": "CVE-2008-4036",
                        "vdoLabel": "Resource Removal",
                        "vdoConfidence": 0.296,
                        "vdoNounGroup": "LogicalImpact"
                      }
                    ],
                    "cvssScoreList": [
                      {
                        "cveId": "CVE-2008-4036",
                        "baseSeverity": "MEDIUM",
                        "severityConfidence": 0.5,
                        "impactScore": "6.1",
                        "impactConfidence": 0.5
                      }
                    ],
                    "products": [
                      {
                        "domain": "Microsoft Windows XP, Service Pack 2 x64",
                        "cpe": "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*",
                        "productId": 74134,
                        "version": "xp"
                      }
                    ],
                    "cpes": [
                      "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*"
                    ],
                    "status": "Under Review",
                    "type": "Trust Failure, Man-in-the-Middle",
                    "discoveredBy": "N/A",
                    "domain": "microsoft-windows-nt",
                    "exploitPublishDate": "N/A",
                    "exploitUrl": "N/A"
                  },
                  {
                    "vulnId": 2414471,
                    "cveId": "CVE-2012-1528",
                    "description": "Integer overflow in Windows Shell in Microsoft Windows XP SP2 and SP3, Windows Server 2003 SP2, Windows Vista SP2, Windows Server 2008 SP2, R2, and R2 SP1, Windows 7 Gold and SP1, Windows 8, and Windows Server 2012 allows local users to gain privileges via a crafted briefcase, aka \\\\\\"Windows Briefcase Integer Overflow Vulnerability.\\\\\\"",
                    "platform": "N/A",
                    "patch": "N/A",
                    "fixDate": "N/A",
                    "publishedDate": "2023-02-08 18:14:43",
                    "lastModifiedDate": "2023-02-08 18:14:43",
                    "existInMitre": true,
                    "existInNvd": true,
                    "timeGapNvd": 0,
                    "timeGapMitre": 0,
                    "sources": [],
                    "vdoList": [
                      {
                        "cveId": "CVE-2012-1528",
                        "vdoLabel": "Application",
                        "vdoConfidence": 0.692,
                        "vdoNounGroup": "Context"
                      },
                      {
                        "cveId": "CVE-2012-1528",
                        "vdoLabel": "Sandboxed",
                        "vdoConfidence": 0.402,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2012-1528",
                        "vdoLabel": "Physical Security",
                        "vdoConfidence": 0.282,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2012-1528",
                        "vdoLabel": "Limited Rmt",
                        "vdoConfidence": 0.482,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2012-1528",
                        "vdoLabel": "Local",
                        "vdoConfidence": 0.411,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2012-1528",
                        "vdoLabel": "Trust Failure",
                        "vdoConfidence": 0.529,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2012-1528",
                        "vdoLabel": "Authentication Bypass",
                        "vdoConfidence": 0.292,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2012-1528",
                        "vdoLabel": "Resource Removal",
                        "vdoConfidence": 0.343,
                        "vdoNounGroup": "LogicalImpact"
                      },
                      {
                        "cveId": "CVE-2012-1528",
                        "vdoLabel": "Read",
                        "vdoConfidence": 0.287,
                        "vdoNounGroup": "LogicalImpact"
                      }
                    ],
                    "cvssScoreList": [
                      {
                        "cveId": "CVE-2012-1528",
                        "baseSeverity": "HIGH",
                        "severityConfidence": 0.5,
                        "impactScore": "7.4",
                        "impactConfidence": 0.5
                      }
                    ],
                    "products": [
                      {
                        "domain": "Microsoft Windows XP, Service Pack 2 x64",
                        "cpe": "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*",
                        "productId": 74134,
                        "version": "xp"
                      }
                    ],
                    "cpes": [
                      "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*"
                    ],
                    "status": "Under Review",
                    "type": "Trust Failure, Authentication Bypass",
                    "discoveredBy": "N/A",
                    "domain": "microsoft-windows-nt",
                    "exploitPublishDate": "N/A",
                    "exploitUrl": "N/A"
                  },
                  {
                    "vulnId": 2414469,
                    "cveId": "CVE-2012-1527",
                    "description": "Integer underflow in Windows Shell in Microsoft Windows XP SP2 and SP3, Windows Server 2003 SP2, Windows Vista SP2, Windows Server 2008 SP2, R2, and R2 SP1, Windows 7 Gold and SP1, Windows 8, and Windows Server 2012 allows local users to gain privileges via a crafted briefcase, aka \\\\\\"Windows Briefcase Integer Underflow Vulnerability.\\\\\\"",
                    "platform": "N/A",
                    "patch": "N/A",
                    "fixDate": "N/A",
                    "publishedDate": "2023-02-08 18:14:43",
                    "lastModifiedDate": "2023-02-08 18:14:43",
                    "existInMitre": true,
                    "existInNvd": true,
                    "timeGapNvd": 0,
                    "timeGapMitre": 0,
                    "sources": [],
                    "vdoList": [
                      {
                        "cveId": "CVE-2012-1527",
                        "vdoLabel": "Application",
                        "vdoConfidence": 0.687,
                        "vdoNounGroup": "Context"
                      },
                      {
                        "cveId": "CVE-2012-1527",
                        "vdoLabel": "Sandboxed",
                        "vdoConfidence": 0.405,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2012-1527",
                        "vdoLabel": "Physical Security",
                        "vdoConfidence": 0.286,
                        "vdoNounGroup": "Mitigation"
                      },
                      {
                        "cveId": "CVE-2012-1527",
                        "vdoLabel": "Limited Rmt",
                        "vdoConfidence": 0.485,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2012-1527",
                        "vdoLabel": "Local",
                        "vdoConfidence": 0.409,
                        "vdoNounGroup": "AttackTheater"
                      },
                      {
                        "cveId": "CVE-2012-1527",
                        "vdoLabel": "Trust Failure",
                        "vdoConfidence": 0.529,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2012-1527",
                        "vdoLabel": "Authentication Bypass",
                        "vdoConfidence": 0.292,
                        "vdoNounGroup": "ImpactMethod"
                      },
                      {
                        "cveId": "CVE-2012-1527",
                        "vdoLabel": "Resource Removal",
                        "vdoConfidence": 0.428,
                        "vdoNounGroup": "LogicalImpact"
                      },
                      {
                        "cveId": "CVE-2012-1527",
                        "vdoLabel": "Read",
                        "vdoConfidence": 0.281,
                        "vdoNounGroup": "LogicalImpact"
                      }
                    ],
                    "cvssScoreList": [
                      {
                        "cveId": "CVE-2012-1527",
                        "baseSeverity": "HIGH",
                        "severityConfidence": 0.5,
                        "impactScore": "7.4",
                        "impactConfidence": 0.5
                      }
                    ],
                    "products": [
                      {
                        "domain": "Microsoft Windows XP, Service Pack 2 x64",
                        "cpe": "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*",
                        "productId": 74134,
                        "version": "xp"
                      }
                    ],
                    "cpes": [
                      "cpe:2.3:o:microsoft:windows-nt:xp:sp2:x64:*:*:*:*:*"
                    ],
                    "status": "Under Review",
                    "type": "Trust Failure, Authentication Bypass",
                    "discoveredBy": "N/A",
                    "domain": "microsoft-windows-nt",
                    "exploitPublishDate": "N/A",
                    "exploitUrl": "N/A"
                  },
                  15
                ]
                """;
        return new mockHTTPConversation("searchServlet", "username", "51f6d822203a36299351f427ca0dc19837f8fca9c3fc8b661844ad047efe5a4ef333d9f91ac5372a42f12fd4281ec0ed61ab0c72ca5f1196a735c742216bb281", cpeLookupParams, new mockHttpResponse(200, cpeLookupExpectedResponse));
    }
}