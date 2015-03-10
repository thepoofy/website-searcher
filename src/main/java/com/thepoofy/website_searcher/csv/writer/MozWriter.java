package com.thepoofy.website_searcher.csv.writer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.thepoofy.website_searcher.models.MozResults;

/**
 * 
 * @author wvanderhoef
 */
public interface MozWriter {

    public void toFile(String fileName, List<MozResults> mozData) throws IOException, FileNotFoundException;
}
