package org.svip.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

/**
 * VEX Table for the database
 *
 * @author Derek Garcia
 **/
@Entity
@Table(name = "vex_file")
public class VEXFile {

    // Schema of VEX
    public enum Schema{
        CYCLONEDX_14,
        CSAF
    }

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
    private Schema schema;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "datasource")
    private Database database;


    @OneToOne(mappedBy = "vex")   // name of field in SBOMFile NOT DB
    private SBOM sbom;
}
