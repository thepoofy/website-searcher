package com.thepoofy.website_searcher.models;

public class MozResults extends MozData {

    private Boolean isSuccessful;

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
}
