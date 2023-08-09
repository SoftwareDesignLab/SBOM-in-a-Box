package org.svip.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.svip.vex.model.VEXType;

/**
 * VEX Table for the database
 *
 * @author Derek Garcia
 **/
@Entity
@Table(name = "vex")
public class VEXFile {

    // Database used for generation
    public enum Database{
        NVD,
        OSV
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition="LONGTEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "schema_type")
    private VEXType schema;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "datasource")
    private Database datasource;

    ///
    /// Relationships
    ///
    @OneToOne(mappedBy = "vexFile")   // name of field in SBOMFile NOT DB
    private SBOM sbom;


    ///
    /// Setters
    ///

    /**
     * Set Vex File Name
     * @param name name
     * @return VEXFile
     */
    public VEXFile setName(String name){
        this.name = name;
        return this;
    }

    /**
     * Set Vex File Content
     * @param content vex json contents
     * @return VEXFile
     */
    public VEXFile setContent(String content){
        this.content = content;
        return this;
    }

    /**
     * Set VEX Schema
     * @param schema schema
     * @return VEXFile
     */
    public VEXFile setSchema(VEXType schema){
        this.schema = schema;
        return this;
    }

    /**
     * Set VEX Datasource
     * @param database origin database
     * @return VEXFile
     */
    public VEXFile setDatasource(Database database){
        this.datasource = database;
        return this;
    }

    /**
     * Set the parent SBOM
     * @param sbom sbom vex was generated for
     * @return VEXFile
     */
    public VEXFile setSBOM(SBOM sbom){
        this.sbom = sbom;
        return this;
    }


    ///
    /// Getters
    ///

    /**
     * @return VEXFile ID
     */
    public Long getID() {
        return this.id;
    }

    /**
     * @return VEXFile JSON Contents
     */
    public String getContent() {
        return this.content;
    }
}
