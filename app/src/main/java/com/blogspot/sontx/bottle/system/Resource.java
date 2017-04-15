package com.blogspot.sontx.bottle.system;

public final class Resource {
    private static final String LINK_PREFIX = "link://";

    private String bottlefsServer;

    public String absoluteUrl(String relativeUrl) {
        if (relativeUrl.startsWith("http"))
            return relativeUrl;
        return bottlefsServer + relativeUrl;
    }

    public boolean isLink(String relativeUrl) {
        return relativeUrl.startsWith(LINK_PREFIX);
    }

    public String buildLink(String relativeUrl) {
        return LINK_PREFIX + relativeUrl;
    }

    public String getUrlFromLink(String link) {
        return link.substring(LINK_PREFIX.length());
    }

    Resource(String bottlefsServer) {
        if (bottlefsServer.endsWith("/"))
            this.bottlefsServer = bottlefsServer;
        else
            this.bottlefsServer = bottlefsServer + "/";
    }
}
