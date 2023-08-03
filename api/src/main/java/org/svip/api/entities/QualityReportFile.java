package org.svip.api.entities;

import jakarta.persistence.*;

/**
 * Quality Report Table for the database
 *
 * @author Derek Garcia
 **/
@Entity
@Table(name = "quality_report_file")
public class QualityReportFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;
}
