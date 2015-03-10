package com.thepoofy.website_searcher.models;

/**
 * Pojo representing a line in the urls.txt file
 * 
 * @author wvanderhoef
 */
public class MozData {

    private int    rank;
    private String url;
    private int    linkingRootDomains;
    private int    externalLinks;
    private float  mozRank;
    private float  mozTrust;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLinkingRootDomains() {
        return linkingRootDomains;
    }

    public void setLinkingRootDomains(int linkingRootDomains) {
        this.linkingRootDomains = linkingRootDomains;
    }

    public int getExternalLinks() {
        return externalLinks;
    }

    public void setExternalLinks(int externalLinks) {
        this.externalLinks = externalLinks;
    }

    public float getMozRank() {
        return mozRank;
    }

    public void setMozRank(float mozRank) {
        this.mozRank = mozRank;
    }

    public float getMozTrust() {
        return mozTrust;
    }

    public void setMozTrust(float mozTrust) {
        this.mozTrust = mozTrust;
    }


}
