package org.svip.sbom.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * File: CPE.java
 * <p>
 * CPE stores all relevant information about a WFN (well-formed CPE name) and can construct a formatted string binding.
 * All documentation on how this works can be found
 * <a href="https://nvlpubs.nist.gov/nistpubs/Legacy/IR/nistir7695.pdf">here</a>.
 * </p>
 * @author Ian Dunn
 */
public class CPE {
    private static final String CPE_V2_3_PREFIX = "cpe:2.3";
    private final String part;
    private String vendor;
    private String product;
    private String version;
    private String update;
    private String edition;
    private String swEdition;
    private String targetSw;
    private String targetHw;
    private String language;
    private String other;

    public CPE(String vendor, String product) {
        this.part = "a";
        this.vendor = vendor;
        this.product = product;
    }

    public CPE(String vendor, String product, String version) {
        this(vendor, product);
        this.version = version;
    }

    public String bindToFS() {
        List<String> values = new ArrayList<>(Stream.of(
                part,
                vendor,
                product,
                version,
                update,
                edition,
                swEdition,
                targetSw,
                targetHw,
                language,
                other
        ).toList());

        values = values.stream().map(v -> {
            if(v == null) v = "ANY";
            return bindValueForFS(v);
        }).collect(Collectors.toList());

        values.add(0, CPE_V2_3_PREFIX);

        return String.join(":", values);
    }

    private String bindValueForFS(String value) {
        switch(value) {
            case "ANY" -> { return "*"; }
            case "NA" -> { return "-"; }
            default -> { return processQuotedChars(value); }
        }
    }

    private String processQuotedChars(String value) {
        String formattedValue = value.trim().replaceAll(" ", "_");
        StringBuilder processed = new StringBuilder();

        int idx = 0;
        while(idx < value.length()) {
            char c = value.charAt(idx);
            if(c != '\\') {
                processed.append(c);
            } else {
                char next = value.charAt(idx + 1);
                if(next == '.' || next == '-' || next == '_') {
                    processed.append(next);
                } else {
                    processed.append('\\').append(next);
                }
                idx += 2;
            }

            idx++;
        }

        return processed.toString();
    }

    public String getPart() {
        return part;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getSwEdition() {
        return swEdition;
    }

    public void setSwEdition(String sw_edition) {
        this.swEdition = sw_edition;
    }

    public String getTargetSw() {
        return targetSw;
    }

    public void setTargetSw(String targetSw) {
        this.targetSw = targetSw;
    }

    public String getTargetHw() {
        return targetHw;
    }

    public void setTargetHw(String targetHw) {
        this.targetHw = targetHw;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("wfn:[");
        out.append(String.format("part=\"%s\"", processQuotedChars(part)));
        if(vendor != null) out.append(String.format("vendor=\"%s\"", processQuotedChars(vendor)));
        if(product != null) out.append(String.format("product=\"%s\"", processQuotedChars(product)));
        if(version != null) out.append(String.format("version=\"%s\"", processQuotedChars(version)));
        if(update != null) out.append(String.format("update=\"%s\"", processQuotedChars(update)));
        if(edition != null) out.append(String.format("edition=\"%s\"", processQuotedChars(edition)));
        if(swEdition != null) out.append(String.format("sw_edition=\"%s\"", processQuotedChars(swEdition)));
        if(targetSw != null) out.append(String.format("target_sw=\"%s\"", processQuotedChars(targetSw)));
        if(targetHw != null) out.append(String.format("target_hw=\"%s\"", processQuotedChars(targetHw)));
        if(language != null) out.append(String.format("language=\"%s\"", processQuotedChars(language)));
        if(other != null) out.append(String.format("other=\"%s\"", processQuotedChars(other)));

        return out.append("]").toString();
    }
}
