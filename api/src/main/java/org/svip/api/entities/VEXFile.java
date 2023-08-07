package org.svip.api.entities;

import jakarta.persistence.*;

/**
 * VEX Table for the database
 *
 * @author Derek Garcia
 **/
@Entity
@Table(name = "vex_file")
public class VEXFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition="LONGTEXT")
    private String content;

    @OneToOne(mappedBy = "vex")   // name of field in SBOMFile NOT DB
    private SBOM sbom;
}
