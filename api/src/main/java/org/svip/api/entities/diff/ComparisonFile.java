package org.svip.api.entities.diff;

import jakarta.persistence.*;

/**
 * File: ComparisonFile.java
 * Comparison Table for the database
 *
 * @author Derek Garcia
 **/
@Entity
@Table(name = "comparison")
public class ComparisonFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;
}
