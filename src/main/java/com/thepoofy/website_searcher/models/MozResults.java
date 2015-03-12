package com.thepoofy.website_searcher.models;

/**
 * POJO representing the MozData and the success of the search function.
 * 
 * @author wvanderhoef
 */
public class MozResults implements Comparable<MozResults> {

    private Boolean isSuccessful;
    private int     rank;
    private String  url;
    private int     linkingRootDomains;
    private int     externalLinks;
    private float   mozRank;
    private float   mozTrust;

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

    public Boolean getIsSuccessful() {
        return isSuccessful;
    }

    public void setIsSuccessful(Boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    public static MozResults instanceOf(MozData moz, Boolean isSuccessful) {

        MozResults results = new MozResults();
        results.setRank(moz.getRank());
        results.setUrl(moz.getUrl());
        results.setLinkingRootDomains(moz.getLinkingRootDomains());
        results.setExternalLinks(moz.getExternalLinks());
        results.setMozRank(moz.getMozRank());
        results.setMozTrust(moz.getMozTrust());

        results.setIsSuccessful(isSuccessful);

        return results;
    }

    @Override
    public int compareTo(MozResults o) {
        return this.getRank() - o.getRank();
    }

}
