import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.repodriller.domain.Commit;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.*;

import java.io.*;
import java.util.*;

public class DeveloperVisitor implements CommitVisitor {


    public void addInfoToCSV(String nomeFile, String support) throws IOException {
        BufferedReader br=null;
        BufferedWriter bw=null;
        final String lineSep=System.getProperty("line.separator");
        File file= null, file2=null;
        try {
             file = new File(nomeFile);
             file2 = new File(nomeFile+"1");//so the
            //names don't conflict or just use different folders
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file))) ;
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2)));
            String line = null;
            String addedColumn = null;
            int i=0;
            for ( line = br.readLine(); line != null; line = br.readLine(),i++)
            {
                if(i==0){
                    addedColumn = "HashCommit, Date, ";
                }else{
                    addedColumn = support;
                }
                bw.write(addedColumn+line+lineSep);
            }
        }catch(Exception e){
            System.out.println(e);
        }finally  {
            if(br!=null)
                br.close();
            if(bw!=null)
                bw.close();
            file.delete();
            File fBuffer = new File(nomeFile);
            file2.renameTo(fBuffer);
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
                        ("java -jar DesigniteJava.jar -i -f " + repo.getPath() + " -o output/" + commit.getHash(),
                                null,
                                new File("."));
                int processComplete = runtimeProcess.waitFor(); // value 0 indicates normal termination
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String infoAggiuntive = ","+ commit.getHash() + "," + commit.getDate().getTime();

            String csvArchitectureSmells = "output/" + commit.getHash() + "/ArchitectureSmells.csv";
            String csvDesignSmells = "output/" + commit.getHash() + "/DesignSmells.csv";
            String csvImplementationSmells = "output/" + commit.getHash() + "/ImplementationSmells.csv";
            String csvMethodMetrics = "output/" + commit.getHash() + "/MethodMetrics.csv";
            String csvTypeMetrics = "output/" + commit.getHash() + "/TypeMetrics.csv";

            //modifico i file CSV aggiungendo commit e data
            addInfoToCSV(csvArchitectureSmells, infoAggiuntive);
            addInfoToCSV(csvDesignSmells, infoAggiuntive);
            addInfoToCSV(csvImplementationSmells, infoAggiuntive);
            addInfoToCSV(csvMethodMetrics, infoAggiuntive);
            addInfoToCSV(csvTypeMetrics, infoAggiuntive);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            repo.getScm().reset();
        }
    }
}
