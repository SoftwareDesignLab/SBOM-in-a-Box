/**
 * Copyright 2021 Rochester Institute of Technology (RIT). Developed with
 * government support under contract 70RCSA22C00000008 awarded by the United
 * States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.svip.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.svip.api.entities.SBOMFile;

/**
 * file: SBOMFileDTO.java
 * <p>
 * Data Transfer Object for SBOM Files - eliminates circular references
 *
 * @author Ibrahim Matar
 **/
public class SBOMFileDTO {

    @JsonProperty
    private Long id;
    
    @JsonProperty("fileName")
    private String name;
    
    @JsonProperty("contents")
    private String content;
    
    @JsonProperty
    private SBOMFile.Schema schema;
    
    @JsonProperty
    private SBOMFile.FileType fileType;

    // Constructors
    public SBOMFileDTO() {}

    public SBOMFileDTO(SBOMFile sbomFile) {
        this.id = sbomFile.getId();
        this.name = sbomFile.getName();
        this.content = sbomFile.getContent();
        this.schema = sbomFile.getSchema();
        this.fileType = sbomFile.getFileType();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public SBOMFile.Schema getSchema() {
        return schema;
    }

    public void setSchema(SBOMFile.Schema schema) {
        this.schema = schema;
    }

    public SBOMFile.FileType getFileType() {
        return fileType;
    }

    public void setFileType(SBOMFile.FileType fileType) {
        this.fileType = fileType;
    }
}
