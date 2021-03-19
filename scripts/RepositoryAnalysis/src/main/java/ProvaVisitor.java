import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.repodriller.domain.Commit;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.persistence.csv.CSVFile;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.RepositoryFile;
import org.repodriller.scm.SCMRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class ProvaVisitor implements CommitVisitor {

    public void mergeCSV(String pathDestination, String nomeFile) throws IOException {
        BufferedReader br=null;
        final String lineSep=System.getProperty("line.separator");
        File file = new File(nomeFile);
        Writer output = new BufferedWriter(new FileWriter(pathDestination, true));

        try {
            //names don't conflict or just use different folders
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            int i=0;
            for ( line = br.readLine(); line != null; line = br.readLine(),i++)
            {
                if (i!=0){
                 output.append(line+lineSep);
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }finally  {
            if(br!=null)
                br.close();
            output.close();
        }



    }
    @Override
    public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {

        String csvArchitectureSmells = "output/" + commit.getHash() + "/ArchitectureSmells.csv";
        String csvDesignSmells = "output/" + commit.getHash() + "/DesignSmells.csv";
        String csvImplementationSmells = "output/" + commit.getHash() + "/ImplementationSmells.csv";
        String csvMethodMetrics = "output/" + commit.getHash() + "/MethodMetrics.csv";
        String csvTypeMetrics = "output/" + commit.getHash() + "/TypeMetrics.csv";
        try {
            mergeCSV("outputFinali/ArchitectureSmells.csv",csvArchitectureSmells);
            mergeCSV("outputFinali/DesignSmells.csv",csvDesignSmells);
            mergeCSV("outputFinali/ImplementationSmells.csv",csvImplementationSmells);
            mergeCSV("outputFinali/MethodMetrics.csv",csvMethodMetrics);
            mergeCSV("outputFinali/TypeMetrics.csv",csvTypeMetrics);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
