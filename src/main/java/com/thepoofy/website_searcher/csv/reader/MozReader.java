package com.thepoofy.website_searcher.csv.reader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import com.thepoofy.website_searcher.models.MozData;

/**
 * 
 * @author wvanderhoef
 */
public interface MozReader {

    public Collection<MozData> fromFile(String fileName) throws IOException, FileNotFoundException;
}
