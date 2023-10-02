package org.svip.sbom.model.uids;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * File: License.java
 * License Object to hold License values. Maps to license data using Jackson Databind from
 * https://raw.githubusercontent.com/spdx/license-list-data/main/json/licenses.json
 *
 * @author Jordan Wong
 */
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
    public void setUrl(List<String> url) {
        if (url.size() > 0) {
            this.url = url.get(0);
        }
    }

    @Override
    public String toString() {
        return "{ id: " + this.id
                + ", name: " + this.name
                + ", url: " + this.url + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        License license = (License) o;

        return Objects.equals(id, license.id)
                && Objects.equals(name, license.name)
                && Objects.equals(url, license.url);
    }

}
