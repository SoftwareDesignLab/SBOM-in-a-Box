/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
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
 */

package org.svip.sbom.builder.interfaces.generics;


import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.shared.util.LicenseCollection;

/**
 * file: ComponentBuilder.java
 * Generic interface for components in an SBOM that are common to both
 * SPDX and CycloneDX SBOMs
 *
 * @author Matthew Morrison
 */
public interface ComponentBuilder {

    /**
     * Set the component's type
     *
     * @param type the designated type of component
     * @return a ComponentBuilder
     */
    ComponentBuilder setType(String type);

    /**
     * Set the component's UID
     *
     * @param uid the uid of the component
     * @return a ComponentBuilder
     */
    ComponentBuilder setUID(String uid);

    /**
     * Set the component's author
     *
     * @param author the author of the component
     * @return a ComponentBuilder
     */
    ComponentBuilder setAuthor(String author);

    /**
     * Set the component's name
     *
     * @param name the name of the component
     * @return a ComponentBuilder
     */
    ComponentBuilder setName(String name);

    /**
     * Set the licenses associated with the component
     *
     * @param licenses a collection of licenses
     * @return a ComponentBuilder
     */
    ComponentBuilder setLicenses(LicenseCollection licenses);

    /**
     * Set the component's copyright
     *
     * @param copyright the copyright info of the component
     * @return a ComponentBuilder
     */
    ComponentBuilder setCopyright(String copyright);

    /**
     * Add a hash value to the component's info
     *
     * @param algorithm the algorithm of the hash
     * @param hash      the value of the hash
     * @return a ComponentBuilder
     */
    ComponentBuilder addHash(String algorithm, String hash);

    /**
     * Build the component
     *
     * @return a new Component
     */
    Component build();

    /**
     * Build and flush the component
     *
     * @return a new Component
     */
    Component buildAndFlush();
}
