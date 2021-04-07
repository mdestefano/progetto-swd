package mining;

import org.repodriller.domain.Commit;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.*;
import utils.*;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class DeveloperVisitor implements CommitVisitor {

    private  List<String> hashCommits;
    private  HashMap<String,String> hashmapTag;
    private final String projectName;
    private final File pathOutput;
    private final File pathProject;

    public DeveloperVisitor(String projectName, HashMap<String,String> hashmapTag) {
        hashCommits = Collections.synchronizedList(new ArrayList<String>());
        this.hashmapTag = hashmapTag;
        this.projectName = projectName;
        pathOutput = UtilsFileDirectory.createTempDirectory("output");
        pathProject = UtilsFileDirectory.createTempDirectory(pathOutput.getName() + "/" + projectName);
    }

    public List<String> getHashCommit() {
        return hashCommits;
    }

    public String getPathCommit() {
        return pathProject.getPath();
    }

    @Override
    public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {
        try {
            hashCommits.add(commit.getHash());

            repo.getScm().checkout(commit.getHash());

            String tempCsvPathPrefix = Paths.get(pathOutput.getName(), pathProject.getName(), commit.getHash()).toString();
            UtilsFileDirectory.createTempDirectory(tempCsvPathPrefix);

            try {
                Process runtimeProcess = Runtime.getRuntime().exec
                        ("java -jar DesigniteJava.jar -i " + repo.getPath() + " -o " + pathOutput.getPath() + "/" + pathProject.getName() + "/" + commit.getHash(),
                                null,
                                new File("."));
                System.out.println("#### DESIGNITE, progetto " + projectName +
                        " commit " + commit.getHash() + "-> START");
                int processComplete = runtimeProcess.waitFor(); // value 0 indicates normal termination

                if (processComplete == 0) {
                    System.out.println("### DESIGNITE, progetto " + projectName +
                            " commit " + commit.getHash() + "-> END");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            String infoAggiuntive =  hashmapTag.get(commit.getHash())+ ","+commit.getHash() + "," + commit.getDate().getTime() + ",";
            String csvArchitectureSmells =
                    UtilsFileDirectory.createTempFile(tempCsvPathPrefix, "ArchitectureSmells.csv").getPath();
            String csvDesignSmells =
                    UtilsFileDirectory.createTempFile(tempCsvPathPrefix, "DesignSmells.csv").getPath();
            String csvImplementationSmells =
                    UtilsFileDirectory.createTempFile(tempCsvPathPrefix, "ImplementationSmells.csv").getPath();
            String csvMethodMetrics =
                    UtilsFileDirectory.createTempFile(tempCsvPathPrefix, "MethodMetrics.csv").getPath();
            String csvTypeMetrics =
                    UtilsFileDirectory.createTempFile(tempCsvPathPrefix, "TypeMetrics.csv").getPath();

            UtilsCSV.addInfoToCSV(csvArchitectureSmells, infoAggiuntive);
            UtilsCSV.addInfoToCSV(csvDesignSmells, infoAggiuntive);
            UtilsCSV.addInfoToCSV(csvImplementationSmells, infoAggiuntive);
            UtilsCSV.addInfoToCSV(csvMethodMetrics, infoAggiuntive);
            UtilsCSV.addInfoToCSV(csvTypeMetrics, infoAggiuntive);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            repo.getScm().reset();
            boolean delete = pathOutput.delete();

            if(delete){
                System.out.println ("### Directory output eliminata:");
            }

        }
    }

}
