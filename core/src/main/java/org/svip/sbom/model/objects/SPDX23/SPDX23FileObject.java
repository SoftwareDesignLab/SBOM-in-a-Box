package org.svip.sbom.model.objects.SPDX23;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Component;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23File;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;
import org.svip.sbomanalysis.comparison.conflicts.ConflictFactory;

import java.util.*;

import static org.svip.sbomanalysis.comparison.conflicts.MismatchType.*;

/**
 * file: SPDX23FileObject.java
 * Holds information for an SPDX 2.3 File object
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
public class SPDX23FileObject implements SPDX23File {

    /**File's type*/
    private final String type;

    /**File's uid*/
    private final String uid;

    /**File's author*/
    private final String author;

    /**File's name*/
    private final String name;

    /**File's licenses*/
    private final LicenseCollection licenses;

    /**File's copyright*/
    private final String copyright;

    /**File's hashes*/
    private final HashMap<String, String> hashes;

    /**File's file notice*/
    private final String fileNotice;

    /**File's comment*/
    private final String comment;

    /**File's attribution text*/
    private final String attributionText;

    /**
     * Get the file's comment
     * @return the file's comment
     */
    @Override
    public String getComment() {
        return this.comment;
    }

    /**
     * Get the file's attribution text
     * @return the file's attribution text
     */
    @Override
    public String getAttributionText() {
        return this.attributionText;
    }

    /**
     * Get the file's file notice
     * @return the file's file notice
     */
    @Override
    public String getFileNotice() {
        return this.fileNotice;
    }

    /**
     * Get the file's type
     * @return the file's type
     */
    @Override
    public String getType() {
        return this.type;
    }

    /**
     * Get the file's uid
     * @return the file's uid
     */
    @Override
    public String getUID() {
        return this.uid;
    }

    /**
     * Get the file's author
     * @return the file's author
     */
    @Override
    public String getAuthor() {
        return this.author;
    }

    /**
     * Get the file's name
     * @return the file's name
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Get the file's licenses
     * @return the file's licenses
     */
    @Override
    public LicenseCollection getLicenses() {
        return this.licenses;
    }

    /**
     * Get the file's copyright info
     * @return the file's copyright info
     */
    @Override
    public String getCopyright() {
        return this.copyright;
    }

    /**
     * Get the file's hashes
     * @return the file's hashes
     */
    @Override
    public Map<String, String> getHashes() {
        return this.hashes;
    }

    /**
     * Constructor to build a new SPDX 2.3 File Object
     * @param type file type
     * @param uid file uid
     * @param author file author
     * @param name file name
     * @param licenses file licenses
     * @param copyright file copyright
     * @param hashes file hashes
     * @param fileNotice file's file notice
     * @param comment file's comment
     */
    public SPDX23FileObject(String type, String uid, String author, String name,
                      LicenseCollection licenses, String copyright,
                      HashMap<String, String> hashes, String fileNotice,
                            String comment, String attributionText){
        this.type = type;
        this.uid = uid;
        this.author = author;
        this.name = name;
        this.licenses = licenses;
        this.copyright = copyright;
        this.hashes = hashes;
        this.fileNotice = fileNotice;
        this.comment = comment;
        this.attributionText = attributionText;

    }

    /**
     * Compare against another generic SBOM Package
     *
     * @param other Other SBOM Package to compare against
     * @return List of conflicts
     */
    @Override
    public List<Conflict> compare(Component other) throws JsonProcessingException {
        ConflictFactory cf = new ConflictFactory();

        // Type
        cf.addConflict("Type", MISC_MISMATCH, this.type, other.getType());

        // UID
        cf.addConflict("UID", MISC_MISMATCH, this.uid, other.getUID());

        // NAME
        // shouldn't occur
        cf.addConflict("Name", NAME_MISMATCH, this.name, other.getName());

        // AUTHOR
        cf.addConflict("Author", AUTHOR_MISMATCH, this.author, other.getAuthor());

        // Licenses
        if(cf.comparable("License", this.licenses, other.getLicenses()))
            cf.addConflicts(this.licenses.compare(other.getLicenses()));

        // Copyright
        cf.addConflict("Copyright", MISC_MISMATCH, this.copyright, other.getCopyright());

        // Hashes
        cf.compareHashes("Component Hash", this.hashes, other.getHashes());

        // Compare SPDX component specific fields
        if( other instanceof SPDX23Component)
            cf.addConflicts( compare((SPDX23Component) other) );

        return cf.getConflicts();
    }

    /**
     * Compare against another SPDX 2.3 Component
     *
     * @param other Other SPDX 2.3 Component to compare against
     * @return List of conflicts
     */
    @Override
    public List<Conflict> compare(SPDX23Component other){
        ConflictFactory cf = new ConflictFactory();

        // Comment
        cf.addConflict("Comment", MISC_MISMATCH, this.comment, other.getComment());

        // Attribution Text
        cf.addConflict("Attribution Text", MISC_MISMATCH, this.attributionText, other.getAttributionText());

        // Compare SPDX File specific fields
        if( other instanceof SPDX23File)
            cf.addConflicts( compare((SPDX23File) other) );

        return cf.getConflicts();
    }

    /**
     * Compare against another SPDX 2.3 File
     *
     * @param other Other SPDX 2.3 File to compare against
     * @return List of conflicts
     */
    @Override
    public List<Conflict> compare(SPDX23File other){
        ConflictFactory cf = new ConflictFactory();

        // File Notice
        cf.addConflict("File Notice", MISC_MISMATCH, this.fileNotice, other.getFileNotice());

        return cf.getConflicts();
    }

    @Override
    public int hashCode() {
        if(name == null) return super.hashCode();
        return this.name.hashCode();
    }
}
