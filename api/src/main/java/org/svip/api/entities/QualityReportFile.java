package org.svip.api.entities;

import jakarta.persistence.*;

/**
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

    @OneToOne(mappedBy = "quality_report_file")
    private SBOM sbom;


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
}
