import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;

public class MainProva {

    public static void main(String[] args) {

        String support = "ciao, mondo";
        String nomeFile = "outputCalcolati\\8d7a39b2dab980363104164134638cf762d94edb\\ImplementationSmells.csv";
        ArrayList<String> infoLette = new ArrayList<String>();
        try {
            Reader in = new FileReader(nomeFile);
            Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
            for (CSVRecord record : records) {
                for(int i=0; i < record.size(); i++)
                    infoLette.add(record.get(i));
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("info" + infoLette);
    }
}
