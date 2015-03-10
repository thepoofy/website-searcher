package com.thepoofy.website_searcher.csv.reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType;
import com.thepoofy.website_searcher.models.MozData;

public class MozCsvReader implements MozReader {

    @Override
    public Collection<MozData> fromFile(String fileName) throws IOException, FileNotFoundException {

        List<MozData> dataList = new ArrayList<>();

        CsvSchema schema = CsvSchema.builder()
                .addColumn("rank", ColumnType.NUMBER)
                .addColumn("url", ColumnType.NUMBER)
                .addColumn("linkingRootDomains", ColumnType.NUMBER)
                .addColumn("externalLinks", ColumnType.NUMBER)
                .addColumn("mozRank", ColumnType.NUMBER)
                .addColumn("mozTrust", ColumnType.NUMBER)
                .setSkipFirstDataRow(true)
                .build();

        CsvMapper mapper = new CsvMapper();


        try (InputStream is = new FileInputStream(fileName)) {

            MappingIterator<MozData> itr = mapper.reader(MozData.class)
                    .with(schema)
                    .readValues(is);

            while (itr.hasNext()) {
                dataList.add(itr.next());
            }

        } finally {

        }

        return dataList;
    }

}
