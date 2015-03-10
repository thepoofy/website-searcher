package com.thepoofy.website_searcher.csv.reader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.thepoofy.website_searcher.models.MozData;

public class MozCsvReader implements MozReader {

    @Override
    public Collection<MozData> fromFile(String fileName) throws IOException, FileNotFoundException {

        List<MozData> dataList = new ArrayList<>();

        CsvToBean<MozData> bean = new CsvToBean<MozData>();

        Map<String, String> columnMapping = new HashMap<String, String>();
        columnMapping.put("Rank", "rank");
        columnMapping.put("URL", "url");
        columnMapping.put("Linking Root Domains", "linkingRootDomains");
        columnMapping.put("External Links", "externalLinks");
        columnMapping.put("mozRank", "mozRank");
        columnMapping.put("mozTrust", "mozTrust");

        HeaderColumnNameTranslateMappingStrategy<MozData> strategy = new HeaderColumnNameTranslateMappingStrategy<MozData>();
        strategy.setColumnMapping(columnMapping);


        try (Reader fileReader = new FileReader(fileName);
                CSVReader csvReader = new CSVReader(fileReader, 'c', '"')) {

            dataList = bean.parse(strategy, csvReader);

        } finally {

        }

        return dataList;
    }

}
