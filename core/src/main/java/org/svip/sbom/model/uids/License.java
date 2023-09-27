package org.svip.sbom.model.uids;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class License {

    private String id;
    private String name;
    private String url;

    public License() {
        // Empty constructor required for Jackson Databind ObjectMapper
    }

    public License(String identifier) {
        if (identifier.split(" ").length > 1) {
            this.name = identifier;
        } else {
            this.id = identifier;
        }
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    @JsonProperty("licenseId")
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("seeAlso")
    public void setUrl(String[] url) {
        if (url.length > 0) {
            this.url = url[0];
        }
    }

    @Override
    public String toString() {
        return "{ id: " + this.id
                + ", name: " + this.name
                + ", url: " + this.url + " }";
    }

}
