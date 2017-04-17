package com.blogspot.sontx.bottle.system;

public final class Resource {
    private String bottlefsServer;

    public String absoluteUrl(String relativeUrl) {
        if (relativeUrl.startsWith("http"))
            return relativeUrl;
        else if (relativeUrl.startsWith("/"))
            return "file://" + relativeUrl;
        return bottlefsServer + relativeUrl;
    }

    Resource(String bottlefsServer) {
        if (bottlefsServer.endsWith("/"))
            this.bottlefsServer = bottlefsServer;
        else
            this.bottlefsServer = bottlefsServer + "/";
    }
}
