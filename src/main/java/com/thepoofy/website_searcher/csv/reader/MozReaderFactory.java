package com.thepoofy.website_searcher.csv.reader;


public class MozReaderFactory {

    public MozReader instanceOf() {
        return new MozCsvReader();
    }
}
