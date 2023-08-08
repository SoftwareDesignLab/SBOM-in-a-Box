package org.svip.api.entities.diff;

import jakarta.persistence.*;

/**
 * File: MissingFile.java
 * Missing Component names to be stored in the database
 *
 * @author Derek Garcia
 **/
@Entity
@Table(name = "missing")
public class MissingFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;
}
