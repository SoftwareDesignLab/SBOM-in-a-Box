package org.svip.api.entities;

import jakarta.persistence.*;

/**
 * SBOM Table for database
 *
 * @author Derek Garcia
 **/
@Entity
@Table(name = "SBOMs")
public class SBOM {

    // Schema of SBOM
    public enum Schema{
        CYCLONEDX_14,
        SPDX_23
    }

    // File Type of SBOM
    public enum FileType{
        JSON,
        TAG_VALUE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "schema_type")
    private Schema schema;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "file_type")
    private FileType fileType;

    ///
    /// Setters
    ///

    /**
     * Set File Name
     * @param name filename of SBOM
     * @return SBOM
     */
    public SBOM setName(String name){
        this.name = name;
        return this;
    }

    /**
     * Set File Content
     * @param content SBOM string contents
     * @return SBOM
     */
    public SBOM setContent(String content){
        this.content = content;
        return this;
    }

    /**
     * Set SBOM Schema
     * @param schema SBOM schema
     * @return SBOM
     */
    public SBOM setSchema(Schema schema){
        this.schema = schema;
        return this;
    }

    /**
     * Set SBOM File Type
     * @param fileType File Type
     * @return SBOM
     */
    public SBOM setFileType(FileType fileType){
        this.fileType = fileType;
        return this;
    }

    ///
    /// Getters
    ///

    /**
     * @return SBOM ID
     */
    public Long getId() {
        return id;
    }
}
