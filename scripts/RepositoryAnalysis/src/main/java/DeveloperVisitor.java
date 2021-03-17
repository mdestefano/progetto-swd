import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.repodriller.domain.Commit;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.*;

import java.io.*;
import java.util.*;

public class DeveloperVisitor implements CommitVisitor {

    public void addInfoToCSV(String nomeFile, String support) {
        try {
            CSVWriter writerCommit = new CSVWriter(new FileWriter(nomeFile, true));
            String[] record = support.split(",");
            writerCommit.writeNext(record);
            writerCommit.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> readInfoFromCSV(String nomeFile) {
        ArrayList<String> infoLette = new ArrayList<String>();
        try {
            Reader in = new FileReader(nomeFile);
            Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
            for (CSVRecord record : records) {
                for(int i = 0; i < record.size(); i++)
                    infoLette.add(record.get(i));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return infoLette;
    }

    @Override
    public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {
        try {
            repo.getScm().checkout(commit.getHash());

            List<RepositoryFile> files = repo.getScm().files();

            File folder = new File("output/" + commit.getHash());
            if (!folder.exists()) {
                if (folder.mkdir()) {
                    System.out.println("Directory is created!");
                } else {
                    System.out.println("Failed to create directory!");
                }
            }

            try {
                Process runtimeProcess = Runtime.getRuntime().exec
                        ("java -jar DesigniteJava.jar -i " + repo.getPath() + " -o C:\\Users\\Armando\\swdProjects\\JavaPoetAnalysis\\output\\" + commit.getHash(),
                                null,
                                new File("C:\\Users\\Armando\\swdProjects"));
                int processComplete = runtimeProcess.waitFor(); // value 0 indicates normal termination
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String infoAggiuntive = commit.getHash() + "," + commit.getDate().getTime();

            String csvArchitectureSmells = "C:\\Users\\Armando\\swdProjects\\JavaPoetAnalysis\\output\\" + commit.getHash() + "\\ArchitectureSmells.csv";
            String csvDesignSmells = "C:\\Users\\Armando\\swdProjects\\JavaPoetAnalysis\\output\\" + commit.getHash() + "\\DesignSmells.csv";
            String csvImplementationSmells = "C:\\Users\\Armando\\swdProjects\\JavaPoetAnalysis\\output\\" + commit.getHash() + "\\ImplementationSmells.csv";
            String csvMethodMetrics = "C:\\Users\\Armando\\swdProjects\\JavaPoetAnalysis\\output\\" + commit.getHash() + "\\MethodMetrics.csv";
            String csvTypeMetrics = "C:\\Users\\Armando\\swdProjects\\JavaPoetAnalysis\\output\\" + commit.getHash() + "\\TypeMetrics.csv";

            //modifico i file CSV aggiungendo commit e data
            addInfoToCSV(csvArchitectureSmells, infoAggiuntive);
            addInfoToCSV(csvDesignSmells, infoAggiuntive);
            addInfoToCSV(csvImplementationSmells, infoAggiuntive);
            addInfoToCSV(csvMethodMetrics, infoAggiuntive);
            addInfoToCSV(csvTypeMetrics, infoAggiuntive);

            //leggo i contenuti dei file CSV per scriverli nel file globale
            ArrayList<String> contentArchitectureSmells = readInfoFromCSV(csvArchitectureSmells);
            ArrayList<String> contentDesignSmells = readInfoFromCSV(csvDesignSmells);
            ArrayList<String> contentImplementationSmells = readInfoFromCSV(csvImplementationSmells);
            ArrayList<String> contentMethodMetrics = readInfoFromCSV(csvMethodMetrics);
            ArrayList<String> contentTypeMetrics = readInfoFromCSV(csvTypeMetrics);

            writer.write(contentArchitectureSmells);
            writer.write(contentDesignSmells);
            writer.write(contentImplementationSmells);
            writer.write(contentMethodMetrics);
            writer.write(contentTypeMetrics);

        } finally {
            repo.getScm().reset();
        }
    }
}
