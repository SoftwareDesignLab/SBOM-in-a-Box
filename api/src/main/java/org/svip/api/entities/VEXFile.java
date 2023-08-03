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
}
