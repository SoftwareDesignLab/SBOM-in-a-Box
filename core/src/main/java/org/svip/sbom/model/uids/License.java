package org.svip.sbom.model.uids;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class License {

    private String id;
    private String name;
    private String url;
    private boolean isDeprecated;

    public License() {
        // Empty constructor
    }

    public License(String id) {
        this.id = id;
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

    public boolean isDeprecated() {
        return this.isDeprecated;
    }

    @JsonProperty("licenseId")
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("detailsUrl")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("isDeprecatedLicenseId")
    public void setIsDeprecated(boolean isDeprecated) {
        this.isDeprecated = isDeprecated;
    }

    @Override
    public String toString() {
        return "{ id: " + this.id
                + ", name: " + this.name
                + ", url: " + this.url
                + ", isDeprecated: " + this.isDeprecated + " }";
    }

}
