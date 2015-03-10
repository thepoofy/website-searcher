package urlfetch;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MozCsvReader implements MozReader {

    @Override
    public Collection<MozData> fromFile(String fileName) {

        List<MozData> dataList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            // must read off the first line of the file
            String header = br.readLine();
            while (br.ready()) {

                // each line contains a url and some other moz related data
                String line = br.readLine();

                dataList.add(fromLine(line));
            }


        } catch (FileNotFoundException fnfe) {

        } catch (IOException ioe) {

        } finally {

        }

        return dataList;
    }

    private static MozData fromLine(String line) {
        // TODO 
        return null;
    }
}
