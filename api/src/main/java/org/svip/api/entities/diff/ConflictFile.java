package org.svip.api.entities.diff;

import jakarta.persistence.*;

/**
 * File: ConflictFile.java
 * Conflicts to be stored in the database
 *
 * @author Derek Garcia
 **/
@Entity
@Table(name = "conflict")
public class ConflictFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;
}
