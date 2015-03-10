package com.thepoofy.website_searcher.csv.writer;


public class MozWriterFactory {

    public MozWriter instanceOf() {
        return new MozCsvWriter();
    }
}
