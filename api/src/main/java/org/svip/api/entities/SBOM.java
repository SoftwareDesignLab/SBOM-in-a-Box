package org.svip.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.svip.serializers.deserializer.CDX14JSONDeserializer;
import org.svip.serializers.deserializer.Deserializer;
import org.svip.serializers.deserializer.SPDX23JSONDeserializer;
import org.svip.serializers.deserializer.SPDX23TagValueDeserializer;

/**
 * SBOM Table for the database
 * TODO rename SBOMFile
 * @author Derek Garcia
 **/
@Entity
@Table(name = "sbom_file")
public class SBOM {

    // Schema of SBOM
    public enum Schema{
        CYCLONEDX_14,
        SPDX_23
    }

    // File Type of SBOM
    public enum FileType{
        JSON,
        TAG_VALUE
    }

    ///
    /// Metadata
    ///

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @JsonProperty
    private Long id;

    @Column(nullable = false)
    @JsonProperty("fileName")
    private String name;

    @Column(nullable = false, columnDefinition="LONGTEXT")
    @JsonProperty("contents")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "schema_type")
    @JsonProperty
    private Schema schema;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "file_type")
    @JsonProperty
    private FileType fileType;

    ///
    /// Relationships
    ///
//    @OneToOne
//    @JoinColumn(name = "qa_id", referencedColumnName = "id")
//    private QualityReportFile qualityReportFile;

    ///
    /// Setters
    ///

    /**
     * Set File Name
     * @param name filename of SBOM
     * @return SBOM
     */
    public SBOM setName(String name){
        this.name = name;
        return this;
    }

    /**
     * Set File Content
     * @param content SBOM string contents
     * @return SBOM
     */
    public SBOM setContent(String content){
        this.content = content;
        return this;
    }

    /**
     * Set SBOM Schema
     * @param d deserializer to infer schema from
     * @return SBOM
     */
    public SBOM setSchema(Deserializer d){
        // todo better method to determine schema

        if(d instanceof CDX14JSONDeserializer)
            this.schema = Schema.CYCLONEDX_14;

        if(d instanceof SPDX23JSONDeserializer || d instanceof SPDX23TagValueDeserializer)
            this.schema = Schema.SPDX_23;

        return this;
    }

    /**
     * Set SBOM File Type
     * @param d deserializer to infer file type from
     * @return SBOM
     */
    public SBOM setFileType(Deserializer d){
        // todo better method to determine schema

        if(d instanceof CDX14JSONDeserializer || d instanceof SPDX23JSONDeserializer)
            this.fileType = FileType.JSON;

        if(d instanceof SPDX23TagValueDeserializer)
            this.fileType = FileType.TAG_VALUE;

        return this;
    }

    ///
    /// Getters
    ///

    /**
     * @return SBOM ID
     */
    public Long getId() {
        return this.id;
    }

    /**
     * @return SBOM name
     */
    public String getName(){
        return this.name;
    }

    /**
     * @return SBOM Content
     */
    public String getContent(){
        return this.content;
    }
}
