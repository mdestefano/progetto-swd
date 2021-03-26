import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;

public class MainProva {

    public static void main(String[] args) throws IOException {

 /*       String support = "ciao, mondo";
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
        System.out.println("info" + infoLette);*/

        File pathOutput = createTempDirectory("output");
        File pathJavaPoet = createTempDirectory(pathOutput.getName()+"/javapoet");
        File commitACaso = createTempDirectory(pathOutput.getName() +"/" + pathJavaPoet.getName()+"/sjdijosijfjieif2828");

        System.out.println(pathOutput);
        System.out.println(pathJavaPoet);
        System.out.println(commitACaso);

        File fileInserito = createTempFile(pathOutput.getName() +"/" + pathJavaPoet.getName()+"/sjdijosijfjieif2828", "/fileProva.txt");

        System.out.println(fileInserito);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileInserito.getName())));
        bw.write("ESATT");
        bw.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileInserito.getName())));
        String line = null;
        line = br.readLine();
        System.out.println(line);

        fileInserito.delete();

        System.out.println(pathOutput.getPath() +"/" + pathJavaPoet.getName()+"/sjdijosijfjieif2828");
    }



    public static File createTempFile(String prefix, String suffix) {
        File parent = new File(System.getProperty("java.io.tmpdir"));

        File temp = new File(parent, prefix + suffix);

        if (temp.exists()) {
            temp.delete();
        }

        try {
            temp.createNewFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return temp;
    }


    public static File createTempDirectory(String fileName) {
        File parent = new File(System.getProperty("java.io.tmpdir"));

        File temp = new File(parent, fileName);

        if (temp.exists()) {
            temp.delete();
        }

        temp.mkdir();

        return temp;
    }
}
