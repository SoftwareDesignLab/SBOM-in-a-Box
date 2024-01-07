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

package org.svip.api.entities;

import jakarta.persistence.*;

/**
 * file: QualityReportFile.java
 * Quality Report Table for the database
 *
 * @author Derek Garcia
 **/
@Entity
@Table(name = "quality_report")
public class QualityReportFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition="LONGTEXT")
    private String content;

    @OneToOne(mappedBy = "qualityReportFile")   // name of field in SBOMFile NOT DB
    private SBOMFile sbomFile;


    ///
    /// Setters
    ///

    /**
     * @param name Name / UID of qa
     * @return QualityReportFile
     */
    public QualityReportFile setName(String name){
        this.name = name;
        return this;
    }

    /**
     * todo store properly rather than massive string
     *
     * @param content content of qa
     * @return QualityReportFile
     */
    public QualityReportFile setContent(String content){
        this.content = content;
        return this;
    }

    public QualityReportFile setSBOMFile(SBOMFile sbomFile){
        this.sbomFile = sbomFile;
        return this;
    }

    ///
    /// Getters
    ///

    /**
     * @return id of qa
     */
    public Long getID(){
        return this.id;
    }

    /**
     * @return content of qa
     */
    public String getContent(){
        return this.content;
    }

    /**
     * @return SBOM of QA
     */
    public SBOMFile getSBOMFile(){
        return this.sbomFile;
    }
}
