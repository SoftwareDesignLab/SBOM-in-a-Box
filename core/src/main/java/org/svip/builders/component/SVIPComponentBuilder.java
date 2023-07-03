package org.svip.builders.component;

import org.svip.builders.component.interfaces.CycloneDX14.CDX14PackageBuilder_I;
import org.svip.builders.component.interfaces.SPDX23.SPDX23ComponentBuilder;
import org.svip.builders.component.interfaces.SPDX23.SPDX23FileBuilder_I;
import org.svip.builders.component.interfaces.SPDX23.SPDX23PackageBuilder_I;
import org.svip.builders.component.interfaces.generics.ComponentBuilder;
import org.svip.builders.component.interfaces.generics.SBOMComponentBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * file: SVIPComponentBuilder.java
 * Builder class for SVIP components
 *
 * @author Matthew Morrison
 */
public class SVIPComponentBuilder implements SPDX23PackageBuilder_I, CDX14PackageBuilder_I, SPDX23FileBuilder_I {

    /**Component type*/
    private String type;

    /**Component uid*/
    private String uid;

    /**Component author*/
    private String author;

    /**Component name*/
    private String name;

    /**Component licenses*/
    private LicenseCollection licenses;

    /**Component copyright*/
    private String copyright;

    /**Component hashes*/
    private HashMap<String, String> hashes;

    /**Component comment*/
    private String comment;

    /**Component attribution text*/
    private String attributionText;

    /**Component file notice*/
    private String fileNotice;

    /**Component download location*/
    private String downloadLocation;

    /**Component file name*/
    private String fileName;

    /**If the component's files were analyzed*/
    private Boolean filesAnalyzed;

    /**Component verification code*/
    private String verificationCode;

    /**Component home page*/
    private String homePage;

    /**Component source info*/
    private String sourceInfo;

    /**Component release date*/
    private String releaseDate;

    /**Component built date*/
    private String builtDate;

    /**Component valid until date*/
    private String validUntilDate;

    /**Component supplier*/
    private Organization supplier;

    /**Component version*/
    private String version;

    /**Component description*/
    private Description description;

    /**Component CPEs*/
    private Set<String> cpes;

    /**Component PURLs*/
    private Set<String> purls;

    /**Component external references*/
    private Set<ExternalReference> externalReferences;

    /**Component mime type*/
    private String mimeType;

    /**Component publisher*/
    private String publisher;

    /**Component scope*/
    private String scope;

    /**Component group*/
    private String group;

    /**Component properties*/
    private HashMap<String, Set<String>> properties;

    /**
     * Set the component's mime type
     * @param mimeType the package's mime type
     * @return a CDX14PackageBuilder_I
     */
    @Override
    public CDX14PackageBuilder_I setMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    /**
     * Set the component's publisher
     * @param publisher the package's publisher
     * @return a CDX14PackageBuilder_I
     */
    @Override
    public CDX14PackageBuilder_I setPublisher(String publisher) {
        this.publisher = publisher;
        return this;
    }

    /**
     * Set the component's scope
     * @param scope the package's scope
     * @return a CDX14PackageBuilder_I
     */
    @Override
    public CDX14PackageBuilder_I setScope(String scope) {
        this.scope = scope;
        return this;
    }

    /**
     * Set the component's group
     * @param group the package's group
     * @return a CDX14PackageBuilder_I
     */
    @Override
    public CDX14PackageBuilder_I setGroup(String group) {
        this.group = group;
        return this;
    }

    /**
     * Add an external references to the component
     * @param externalReference a package's external reference
     * @return a CDX14PackageBuilder_I
     */
    @Override
    public CDX14PackageBuilder_I addExternalReferences(ExternalReference externalReference) {
        this.externalReferences.add(externalReference);
        return this;
    }

    /**
     * Add a property to the component
     * @param name the name of the property
     * @param value the value of the property
     * @return a CDX14PackageBuilder_I
     */
    @Override
    public CDX14PackageBuilder_I addProperty(String name, String value) {
        Set<String> values;
        if(this.properties.containsKey(name)){
            values = this.properties.get(name);
        }
        else{
            values = new HashSet<>();
        }
        values.add(value);
        this.properties.put(name, values);

        return this;
    }

    /**
     * Set the component's type
     * @param type the designated type of component
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Set the component's uid
     * @param uid the uid of the component
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder setUID(String uid) {
        this.uid = uid;
        return this;
    }

    /**
     * Set the component's author
     * @param author the author of the component
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    /**
     * Set the component's name
     * @param name the name of the component
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set the component's licenses
     * @param licenses a collection of licenses
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder setLicenses(LicenseCollection licenses) {
        this.licenses = licenses;
        return this;
    }

    /**
     * Set the component's copyright info
     * @param copyright the copyright info of the component
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder setCopyright(String copyright) {
        this.copyright = copyright;
        return this;
    }

    /**
     * Add a hash value to the component
     * @param algorithm the algorithm of the hash
     * @param hash the value of the hash
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder addHash(String algorithm, String hash) {
        this.hashes.put(algorithm, hash);
        return this;
    }

    /**
     * Build an SVIPComponent
     * @return a Component
     */
    @Override
    public Component build() {
        return null;
    }

    /**
     * Build and flush an SVIP component
     * @return a Component
     */
    @Override
    public Component buildAndFlush() {
        return null;
    }

    /**
     * Set the component's supplier
     * @param supplier the component's supplier
     * @return an SBOMComponentBuilder
     */
    @Override
    public SBOMComponentBuilder setSupplier(Organization supplier) {
        this.supplier = supplier;
        return this;
    }

    /**
     * Set the component's version
     * @param version the component's version
     * @return an SBOMComponentBuilder
     */
    @Override
    public SBOMComponentBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * Set the component's description
     * @param description the component's description
     * @return an SBOMComponentBuilder
     */
    @Override
    public SBOMComponentBuilder setDescription(Description description) {
        this.description = description;
        return this;
    }

    /**
     * Add a cpe to the component
     * @param cpe the cpe string to add
     * @return an SBOMComponentBuilder
     */
    @Override
    public SBOMComponentBuilder addCPE(String cpe) {
        this.cpes.add(cpe);
        return this;
    }

    /**
     * Add a purl to the component
     * @param purl the purl string to add
     * @return an SBOMComponentBuilder
     */
    @Override
    public SBOMComponentBuilder addPURL(String purl) {
        this.purls.add(purl);
        return this;
    }

    /**
     * Add an external reference to the component
     * @param externalReference the external component to add
     * @return an SBOMComponentBuilder
     */
    @Override
    public SBOMComponentBuilder addExternalReference(ExternalReference externalReference) {
        this.externalReferences.add(externalReference);
        return this;
    }

    /**
     * Set the component's comment
     * @param comment the comment for the component
     * @return an SPDX23ComponentBuilder
     */
    @Override
    public SPDX23ComponentBuilder setComment(String comment) {
        this.comment = comment;
        return this;
    }

    /**
     * Set the component's attribution text
     * @param attributionText the attribution text of the component
     * @return an SPDX23ComponentBuilder
     */
    @Override
    public SPDX23ComponentBuilder setAttributionText(String attributionText) {
        this.attributionText = attributionText;
        return this;
    }

    /**
     * Set the component's download location
     * @param downloadLocation the package's download location
     * @return an SPDX23PackageBuilder_I
     */
    @Override
    public SPDX23PackageBuilder_I setDownloadLocation(String downloadLocation) {
        this.downloadLocation = downloadLocation;
        return this;
    }

    /**
     * Set the component's file name
     * @param fileName the package's file name
     * @return an SPDX23PackageBuilder_I
     */
    @Override
    public SPDX23PackageBuilder_I setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    /**
     * Set if the component's files were analyzed
     * @param filesAnalyzed a boolean if the files were analyzed
     * @return an SPDX23PackageBuilder_I
     */
    @Override
    public SPDX23PackageBuilder_I setFilesAnalyzed(Boolean filesAnalyzed) {
        this.filesAnalyzed = filesAnalyzed;
        return this;
    }

    /**
     * Set the component's verification code
     * @param verificationCode the package's verification code
     * @return an SPDX23PackageBuilder_I
     *
     */
    @Override
    public SPDX23PackageBuilder_I setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
        return this;
    }

    /**
     * Set the component's home page
     * @param homePage the package's home page
     * @return an SPDX23PackageBuilder_I
     */
    @Override
    public SPDX23PackageBuilder_I setHomePage(String homePage) {
        this.homePage = homePage;
        return this;
    }

    /**
     * Set the component's source information
     * @param sourceInfo the package's source information
     * @return an SPDX23PackageBuilder_I
     */
    @Override
    public SPDX23PackageBuilder_I setSourceInfo(String sourceInfo) {
        this.sourceInfo = sourceInfo;
        return this;
    }

    /**
     * Set the component's release date
     * @param releaseDate the package's release date
     * @return an SPDX23PackageBuilder_I
     */
    @Override
    public SPDX23PackageBuilder_I setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    /**
     * Set the component's build date
     * @param buildDate the package's build date
     * @return an SPDX23PackageBuilder_I
     */
    @Override
    public SPDX23PackageBuilder_I setBuildDate(String buildDate) {
        this.builtDate = buildDate;
        return this;
    }

    /**
     * Set the component's valid until date
     * @param validUntilDate the package's valid until date
     * @return an SPDX23PackageBuilder_I
     */
    @Override
    public SPDX23PackageBuilder_I setValidUntilDate(String validUntilDate) {
        this.validUntilDate = validUntilDate;
        return this;
    }

    /**
     * Set the file's file notice
     * @param fileNotice the file notice
     * @return an SPDX23FileBuilder_I
     */
    @Override
    public SPDX23FileBuilder_I setFileNotice(String fileNotice) {
        this.fileNotice = fileNotice;
        return this;
    }
}
