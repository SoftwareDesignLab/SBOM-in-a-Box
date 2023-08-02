package org.svip.api.model;

import jakarta.persistence.*;

/**
 * @author Derek Garcia
 **/

@Entity
@Table(name = "SBOMS")
public class SBOM {

    // Schema of SBOM
    public enum Schema{
        CYCLONE_DX_14,
        SPDX_23
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Schema schema;

}
