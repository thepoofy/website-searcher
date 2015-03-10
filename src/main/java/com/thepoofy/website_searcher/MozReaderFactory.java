package com.thepoofy.website_searcher;

public class MozReaderFactory {

    public MozReader instanceOf() {
        return new MozCsvReader();
    }
}
