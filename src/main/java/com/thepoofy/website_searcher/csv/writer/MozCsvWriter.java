package com.thepoofy.website_searcher.csv.writer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType;
import com.thepoofy.website_searcher.models.MozResults;

public class MozCsvWriter implements MozWriter {

    @Override
    public void toFile(String fileName, List<MozResults> mozData) throws IOException, FileNotFoundException {


        CsvSchema schema = CsvSchema.builder()
                .addColumn("rank", ColumnType.NUMBER)
                .addColumn("url", ColumnType.NUMBER)
                .addColumn("linkingRootDomains", ColumnType.NUMBER)
                .addColumn("externalLinks", ColumnType.NUMBER)
                .addColumn("mozRank", ColumnType.NUMBER)
                .addColumn("mozTrust", ColumnType.NUMBER)
                .addColumn("isSuccessful", ColumnType.BOOLEAN)
                .build();

        CsvMapper mapper = new CsvMapper();

        try (FileOutputStream fos = new FileOutputStream(fileName)) {

            ObjectWriter writer = mapper.writer(schema.withLineSeparator("\n"));

            writer.writeValues(fos).writeAll(mozData.toArray());
            
        }

    }

}
