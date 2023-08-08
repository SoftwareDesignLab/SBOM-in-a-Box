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
@Table(name = "vex_file")
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


    @OneToOne(mappedBy = "vexFile")   // name of field in SBOMFile NOT DB
    private SBOM sbom;


    ///
    /// Setters
    ///

    public VEXFile setName(String name){
        this.name = name;
        return this;
    }

    public VEXFile setContent(String content){
        this.content = content;
        return this;
    }

    public VEXFile setSchema(VEXType schema){
        this.schema = schema;
        return this;
    }

    public VEXFile setDatasource(Database database){
        this.datasource = database;
        return this;
    }

    public VEXFile setSBOM(SBOM sbom){
        this.sbom = sbom;
        return this;
    }


    ///
    /// Getters
    ///


    public Long getID() {
        return this.id;
    }

    public String getContent() {
        return this.content;
    }
}
