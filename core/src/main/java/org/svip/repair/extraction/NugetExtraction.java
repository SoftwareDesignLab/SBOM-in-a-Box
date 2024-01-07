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

    private static final String URL = "https://api.nuget.org/v3-flatcontainer/%s/%s/%s.nuspec";

    public NugetExtraction(PURL purl) {
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
                        copyright = copyrightElements.item(0).getTextContent();

                    // Find the 'license' element
                    NodeList licenseElements = root.getElementsByTagName("license");

                    if(licenseElements.getLength() > 0)
                        license = licenseElements.item(0).getTextContent();

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
