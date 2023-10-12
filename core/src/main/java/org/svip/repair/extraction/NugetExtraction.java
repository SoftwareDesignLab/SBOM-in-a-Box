package org.svip.repair.extraction;

import org.svip.generation.parsers.utils.QueryWorker;
import org.svip.sbom.model.uids.PURL;
import org.svip.utils.Debug;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.HashMap;

import static org.svip.utils.Debug.log;

/**
 * file: NugetExtraction.java
 * Description: Package-manager specific implementation of the Extraction (Nuget)
 *
 * @author Justin Jantzi
 */
public class NugetExtraction extends Extraction {

    private String URL = "https://api.nuget.org/v3-flatcontainer/%s/%s/%s.nuspec";

    protected NugetExtraction(PURL purl) {
        super(purl);
    }

    @Override
    public void extract() {

        if(purl.getName() == "" || purl.getVersion() == "") {
            Debug.log(Debug.LOG_TYPE.WARN, "Can't extract due to name or version missing ");
            return;
        }

        String formattedURL = String.format(this.URL,
            purl.getName().toLowerCase(),
            purl.getVersion().toLowerCase(),
            purl.getName().toLowerCase()
        );

        QueryWorker qw = new QueryWorker(null, formattedURL) {
            @Override
            public void run() {
                final String response = getUrlContents(queryURL(url, false));

                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(new InputSource(new StringReader(response)));

                    // Get the root element of the XML document
                    Element root = document.getDocumentElement();

                    // Find the 'copyright' element
                    NodeList copyrightElements = root.getElementsByTagName("copyright");

                    if(copyrightElements.getLength() > 0)
                        results.put("copyright", copyrightElements.item(0).getTextContent());

                    // Find the 'license' element
                    NodeList licenseElements = root.getElementsByTagName("license");

                    if(licenseElements.getLength() > 0)
                        results.put("license", licenseElements.item(0).getTextContent());

                } catch(Exception ex) {
                    Debug.log(Debug.LOG_TYPE.WARN, "Failed parsing nuget api response to XML for extraction");
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
}
