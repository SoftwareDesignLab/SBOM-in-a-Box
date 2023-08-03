//package org.svip.api.entities;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.svip.api.repository.SBOMFileIdentifierGenerator;
//
///**
// * Dataclass containing JSON-friendly SBOM objects. Contains {@code fileName} & {@code contents} fields that are capable
// * of being automatically serialized to and from JSON.
// *
// * This class is also the model for the MySQL table.
// *
// * @author Ian Dunn
// */
//
//@Entity
//@Table(name = "files")
//public class SBOMFile {
//
//    /**
//     * Autogenerated primary key.
//     */
//    @Id
//    @GeneratedValue(generator = SBOMFileIdentifierGenerator.generatorName)
    @GenericGenerator(name = SBOMFileIdentifierGenerator.generatorName,
            strategy = "org.svip.api.repository.SBOMFileIdentifierGenerator")
//    private long id;
//
//    /**
//     * The name of the SBOM file.
//     */
//    @JsonProperty
//    @Column(name = "fileName", columnDefinition="TEXT")
//    private String fileName;
//
//    /**
//     * The string contents of the SBOM file.
//     */
//    @JsonProperty
//    @Column(name = "contents", columnDefinition="LONGTEXT")
//    private String contents;
//
//    /**
//     * Parameter constructor for SBOMFile.
//     *
//     * @param fileName The name of the SBOM file.
//     * @param contents The contents of the SBOM file.
//     */
//    public SBOMFile(String fileName, String contents) {
//        this.fileName = fileName;
//        this.contents = contents;
//    }
//
//    /**
//     * Default, no-argument constructor for SBOMFile. Sets everything to null.
//     */
//    public SBOMFile() {
//        this(null, null);
//    }
//
//    public boolean hasNullProperties() {
//        return fileName == null || contents == null || fileName.length() == 0 || contents.length() == 0;
//    }
//
//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//
//    public String getFileName() {
//        return fileName;
//    }
//
//    public void setFileName(String fileName) {
//        this.fileName = fileName;
//    }
//
//    public String getContents() {
//        return contents;
//    }
//
//    public void setContents(String contents) {
//        this.contents = contents;
//    }
//}
