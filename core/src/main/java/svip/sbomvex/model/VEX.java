package svip.sbomvex.model;

import java.util.Objects;

/**
 * file: Vulnerability.java
 * VEX Object for SBOMs
 *
 * @author Asa Horn
 * @author Matt London
 * @author Henry Lu
 */
public class VEX {
    private String vulnId;
    private String cveId;
    private String description;
    private String platform;
    private String introducedDate;
    private String publishedDate;
    private String createdDate;
    private String lastModifiedDate;
    private String fixedDate;
    private boolean existsAtMitre;
    private boolean existsAtNvd;
    private int timeGapNvd;
    private int timeGapMitre;
    private int statusId;

    /**
     * VEX REQUIREMENTS https://www.cisa.gov/sites/default/files/publications/VEX_Use_Cases_Aprill2022.pdf
     */
    private String vexFormatIdentifier;
    private String vexAuthor;
    private String vexAuthorRole;
    private String productIdentifier;

    /**
     * FOR productStatusDetails
     * <p>
     * NOT AFFECTED – No remediation is required regarding this vulnerability
     * AFFECTED – Actions are recommended to remediate or address this vulnerability
     * FIXED – These product versions contain a fix for the vulnerability
     * UNDER INVESTIGATION – It is not yet known whether these product versions are affected by the vulnerability.
     * An update will be provided in a later release
     * <p>
     * If a status is AFFECTED, the VEX document must have an action statement that tells the
     * product user what to do. If the status is NOT AFFECTED, then a VEX document must
     * have an impact statement to further explain details.
     */
    private String productStatusDetails;

    /**
     * Default constructor
     */
    public VEX() {

    }

    /**
     * Constructor that assigns all parameters from NVIP
     *
     * @param vulnId           Vulnerability ID found
     * @param cveId            CVE ID found
     * @param description      Description of the vulnerability
     * @param platform         Platforms affected
     * @param introducedDate   Date the vulnerability was introduced
     * @param publishedDate    Date the vulnerability was published
     * @param createdDate      Date the vulnerability was created
     * @param lastModifiedDate Date the vulnerability was last modified
     * @param fixedDate        Date the vulnerability was fixed
     * @param existsAtMitre    Whether the vulnerability exists at MITRE
     * @param existsAtNvd      Whether the vulnerability exists at NVD
     * @param timeGapNvd       Time gap between the vulnerability appearing through NVIP and appearing at NVD
     * @param timeGapMitre     Time gap between the vulnerability appearing through NVIP and appearing at MITRE
     * @param statusId         Status ID of the vulnerability
     */
    public VEX(String vulnId, String cveId, String description, String platform, String introducedDate, String publishedDate, String createdDate, String lastModifiedDate, String fixedDate, boolean existsAtMitre, boolean existsAtNvd, int timeGapNvd, int timeGapMitre, int statusId, String vexFormatIdentifier, String vexAuthor, String vexAuthorRole, String productIdentifier, String productStatusDetails) {
        this.vulnId = vulnId;
        this.cveId = cveId;
        this.description = description;
        this.platform = platform;
        this.introducedDate = introducedDate;
        this.publishedDate = publishedDate;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.fixedDate = fixedDate;
        this.existsAtMitre = existsAtMitre;
        this.existsAtNvd = existsAtNvd;
        this.timeGapNvd = timeGapNvd;
        this.timeGapMitre = timeGapMitre;
        this.statusId = statusId;

        this.vexFormatIdentifier = vexFormatIdentifier;
        this.vexAuthor = vexAuthor;
        this.vexAuthorRole = vexAuthorRole;
        this.productIdentifier = productIdentifier;
        this.productStatusDetails = productStatusDetails;
    }

    ///
    /// Getters
    ///

    public String getVulnId() {
        return this.vulnId;
    }

    public String getCveId() {
        return this.cveId;
    }

    public String getDescription() {
        return this.description;
    }

    public String getPlatform() {
        return this.platform;
    }

    public String getIntroducedDate() {
        return this.introducedDate;
    }

    public String getPublishedDate() {
        return this.publishedDate;
    }

    public String getCreatedDate() {
        return this.createdDate;
    }

    public String getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public String getFixedDate() {
        return this.fixedDate;
    }

    public boolean isExistsAtMitre() {
        return this.existsAtMitre;
    }

    public boolean isExistsAtNvd() {
        return this.existsAtNvd;
    }

    public int getTimeGapNvd() {
        return this.timeGapNvd;
    }

    public int getTimeGapMitre() {
        return this.timeGapMitre;
    }

    public int getStatusId() {
        return this.statusId;
    }

    public String getVexFormatIdentifier() {
        return this.vexFormatIdentifier;
    }

    public String getVexAuthor() {
        return this.vexAuthor;
    }

    public String getVexAuthorRole() {
        return this.vexAuthorRole;
    }

    public String getProductIdentifier() {
        return this.productIdentifier;
    }

    public String getProductStatusDetails() {
        return this.productStatusDetails;
    }

    ///
    /// Overrides
    ///

    @Override
    public int hashCode() {
        return Objects.hash(vulnId, cveId, description, platform, introducedDate, publishedDate, createdDate, lastModifiedDate, fixedDate, existsAtMitre, existsAtNvd, timeGapNvd, timeGapMitre, statusId, vexFormatIdentifier, vexAuthor, vexAuthorRole, productIdentifier, productStatusDetails);
    }

    @Override
    public String toString() {
        return "Vulnerability{" +
                "vulnId='" + vulnId + '\'' +
                ", cveId='" + cveId + '\'' +
                ", description='" + description + '\'' +
                ", platform='" + platform + '\'' +
                ", introducedDate='" + introducedDate + '\'' +
                ", publishedDate='" + publishedDate + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", lastModifiedDate='" + lastModifiedDate + '\'' +
                ", fixedDate='" + fixedDate + '\'' +
                ", existsAtMitre=" + existsAtMitre +
                ", existsAtNvd=" + existsAtNvd +
                ", timeGapNvd=" + timeGapNvd +
                ", timeGapMitre=" + timeGapMitre +
                ", statusId=" + statusId +
                ", vexFormatIdentifier='" + vexFormatIdentifier + '\'' +
                ", vexAuthor='" + vexAuthor + '\'' +
                ", vexAuthorRole='" + vexAuthorRole + '\'' +
                ", productIdentifier='" + productIdentifier + '\'' +
                ", productStatusDetails='" + productStatusDetails + '\'' +
                '}';
    }
}
