package com.thepoofy.website_searcher.csv.writer;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVWriter;
import com.opencsv.bean.BeanToCsv;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.thepoofy.website_searcher.models.MozResults;

public class MozCsvWriter implements MozWriter {

    @Override
    public void toFile(String fileName, List<MozResults> mozData) throws IOException, FileNotFoundException {

        BeanToCsv<MozResults> bean = new BeanToCsv<MozResults>();

        Map<String, String> columnMapping = new HashMap<String, String>();
        columnMapping.put("Rank", "rank");
        columnMapping.put("URL", "url");
        columnMapping.put("Linking Root Domains", "linkingRootDomains");
        columnMapping.put("External Links", "externalLinks");
        columnMapping.put("mozRank", "mozRank");
        columnMapping.put("mozTrust", "mozTrust");
        columnMapping.put("Success", "isSuccessful");

        HeaderColumnNameTranslateMappingStrategy<MozResults> strategy = new HeaderColumnNameTranslateMappingStrategy<MozResults>();
        strategy.setColumnMapping(columnMapping);

        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            
            bean.write(strategy, writer, mozData);
            
        }
    }

}
