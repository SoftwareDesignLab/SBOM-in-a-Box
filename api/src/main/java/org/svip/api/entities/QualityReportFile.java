package org.svip.api.entities;

import jakarta.persistence.*;

/**
 * file: QualityReportFile.java
 * Quality Report Table for the database
 *
 * @author Derek Garcia
 **/
@Entity
@Table(name = "quality_report_file")
public class QualityReportFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition="LONGTEXT")
    private String content;

//    @OneToOne(mappedBy = "qualityReportFile")   // name of field in SBOMFile NOT DB
//    private SBOM sbom;


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

    public QualityReportFile setSBOM(SBOM sbom){
        this.sbom = sbom;
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
}
