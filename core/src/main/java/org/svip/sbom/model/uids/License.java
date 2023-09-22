package org.svip.sbom.model.uids;

public class License {

    private final String id;
    private final String name;
    private final String url;

    public License(String id) {
        this.id = id;
        this.name = null;
        this.url = null;
    }

    public License(String name, String url) {
        this.id = null;
        this.name = name;
        this.url = url;
    }

}
