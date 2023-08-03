package org.svip.api.entities;

import jakarta.persistence.*;

/**
 * Diff Report Table for the database
 *
 * @author Derek Garcia
 **/
@Entity
@Table(name = "diff_report_file")
public class DiffReportFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;
}
