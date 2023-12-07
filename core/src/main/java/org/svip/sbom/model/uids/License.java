/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

package org.svip.sbom.model.uids;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * File: License.java
 * License Object to hold License values. Maps to license data using Jackson Databind from
 * the official SPDX GitHub repository:
 * https://raw.githubusercontent.com/spdx/license-list-data/main/json/licenses.json
 *
 * @author Jordan Wong
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class License {

    private String id;
    private String name;
    private String url;

    /**
     * Create a new License object
     */
    public License() {
        // Empty constructor required for Jackson Databind ObjectMapper
    }

    /**
     * Create a new License object
     *
     * @param identifier license id or name
     */
    public License(String identifier) {
        if (identifier.split(" ").length > 1) {
            this.name = identifier;
        } else {
            this.id = identifier;
        }
    }

    //
    // Getters
    //

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    public String getIdentifier() {
        return this.id != null ? this.id : this.name;
    }

    //
    // Setters
    //

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

    //
    // Overrides
    //

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
