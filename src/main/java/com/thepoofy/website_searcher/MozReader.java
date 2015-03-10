package com.thepoofy.website_searcher;

import java.util.Collection;

/**
 * 
 * @author wvanderhoef
 */
public interface MozReader {

    public Collection<MozData> fromFile(String fileName);
}
