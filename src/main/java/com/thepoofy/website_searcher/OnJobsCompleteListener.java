package com.thepoofy.website_searcher;

import java.util.List;

import com.thepoofy.website_searcher.models.MozResults;

public interface OnJobsCompleteListener {

    public void onComplete(List<MozResults> resultsList);
}
