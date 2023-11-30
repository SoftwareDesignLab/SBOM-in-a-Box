package org.svip.api.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import org.svip.serializers.SerializerFactory;
import org.svip.api.entities.diff.ComparisonFile;
import org.svip.serializers.deserializer.*;

import java.util.HashSet;
import java.util.Set;

/**
 * file: SBOMFile.java
 *
 * SBOM Table for the database
 *
 * @author Derek Garcia
 **/
@Entity
@Table(name = "sbom_file")
public class SBOMFile {

    // Schema of SBOM
    public enum Schema{
        CYCLONEDX_14,
        SPDX_23
    }

    // File Type of SBOM
    public enum FileType{
        JSON,
        XML,
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

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)   // delete all qa on sbom deletion
    @JoinColumn(name = "qa_id", referencedColumnName = "id")
    private QualityReportFile qualityReportFile;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)   // delete all vex on sbom deletion
    @JoinColumn(name = "vex_id", referencedColumnName = "id")
    private VEXFile vexFile;

    // Collection of comparisons where this was the target
    @OneToMany(mappedBy = "targetSBOMFile", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<ComparisonFile> comparisonsAsTarget = new HashSet<>();

    // Collection of comparisons where this was the other
    @OneToMany(mappedBy = "otherSBOMFile", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<ComparisonFile> comparisonsAsOther = new HashSet<>();

    /**
     * Convert SBOMFile to SBOM Object
     *
     * @return deserialized SBOM Object
     * @throws JsonProcessingException SBOM failed to be deserialized
     */
    public org.svip.sbom.model.interfaces.generics.SBOM toSBOMObject() throws JsonProcessingException {
        // Attempt to deserialize and return the object
        Deserializer d = SerializerFactory.createDeserializer(this.content);
        return d.readFromString(this.getContent());
    }

    /**
     * Convert SBOM File to a JSON String
     *
     * @return deserialized SBOM Object as a JSON String
     * @throws JsonProcessingException SBOM failed to be deserialized
     */
    public String toSBOMObjectAsJSON() throws JsonProcessingException {

        // Configure object mapper to remove null and empty arrays
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        // Return JSON String
        return mapper.writeValueAsString(toSBOMObject());
    }

    ///
    /// Setters
    ///

    /**
     * Set File Name
     * @param name filename of SBOM
     * @return SBOMFile
     */
    public SBOMFile setName(String name){
        this.name = name;
        return this;
    }

    /**
     * Set File Content
     * @param content SBOM string contents
     * @return SBOMFile
     */
    public SBOMFile setContent(String content){
        this.content = content;
        return this;
    }

    /**
     * Set SBOM Schema
     * @param d deserializer to infer schema from
     * @return SBOMFile
     */
    public SBOMFile setSchema(Deserializer d){
        // todo better method to determine schema

        if(d instanceof CDX14JSONDeserializer || d instanceof CDX14XMLDeserializer)
            this.schema = Schema.CYCLONEDX_14;

        if(d instanceof SPDX23JSONDeserializer || d instanceof SPDX23TagValueDeserializer)
            this.schema = Schema.SPDX_23;

        return this;
    }

    /**
     * Simple set schema
     * @param schema schema type
     * @return SBOMFile
     */
    public SBOMFile setSchema(Schema schema){
        this.schema = schema;
        return this;
    }


    /**
     * Set SBOM File Type
     * @param d deserializer to infer file type from
     * @return SBOMFile
     */
    public SBOMFile setFileType(Deserializer d){
        // todo better method to determine schema

        if(d instanceof CDX14JSONDeserializer || d instanceof SPDX23JSONDeserializer)
            this.fileType = FileType.JSON;

        if(d instanceof SPDX23TagValueDeserializer)
            this.fileType = FileType.TAG_VALUE;

        if(d instanceof CDX14XMLDeserializer)
            this.fileType = FileType.XML;

        return this;
    }


    /**
     * Set Quality Report File
     *
     * @param qaf Quality Report File
     * @return SBOMFile
     */
    public SBOMFile setQualityReport(QualityReportFile qaf){
        this.qualityReportFile = qaf;
        return this;
    }


    /**
     * Set VEX File
     *
     * @param vf VEX File
     * @return SBOMFile
     */
    public SBOMFile setVEXFile(VEXFile vf){
        this.vexFile = vf;
        return this;
    }

    public SBOMFile addComparisonFileAsTarget(ComparisonFile cf){
        this.comparisonsAsTarget.add(cf);
        return this;
    }

    public SBOMFile addComparisonFileAsOther(ComparisonFile cf){
        this.comparisonsAsOther.add(cf);
        return this;
    }

    /**
     * Simple set schema
     * @param fileType file type
     * @return SBOMFile
     */
    public SBOMFile setFileType(FileType fileType){
        this.fileType = fileType;
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

    /**
     * @return QualityReportFile
     */
    public QualityReportFile getQualityReportFile(){
        return this.qualityReportFile;
    }

    /**
     * @return vexFile
     */
    public VEXFile getVEXFile(){
        return this.vexFile;
    }

    /**
     * @return SBOM Schema
     */
    public Schema getSchema(){
        return this.schema;
    }

    /**
     * @return SBOM FileType
     */
    public FileType getFileType() {
        return this.fileType;
    }


}
