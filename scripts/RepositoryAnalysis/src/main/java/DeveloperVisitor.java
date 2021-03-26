import org.repodriller.domain.Commit;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.*;

import java.io.*;
import java.util.*;

public class DeveloperVisitor implements CommitVisitor {

    private static List<String> hashCommits;
    private String projectName;
    private File pathOutput;
    private File pathJavaPoet;

    public DeveloperVisitor(String projectName) {
        hashCommits = Collections.synchronizedList(new ArrayList<String>());
        this.projectName = projectName;
    }


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

    @Override
    public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {
        try {
            hashCommits.add(commit.getHash());

            repo.getScm().checkout(commit.getHash());

            List<RepositoryFile> files = repo.getScm().files();
            pathOutput = createTempDirectory("output");
            pathJavaPoet = createTempDirectory(pathOutput.getName()+"/javapoet");
            File commitACaso = createTempDirectory(pathOutput.getName() +"/" + pathJavaPoet.getName()+"/"+commit.getHash());

            try {
                Process runtimeProcess = Runtime.getRuntime().exec
                        ("java -jar DesigniteJava.jar -i " + repo.getPath() + " -o "+pathOutput.getPath() +"/" + pathJavaPoet.getName()+"/"+commit.getHash(),
                                null,
                                new File("."));
                System.out.println("### DESIGNITE, progetto "+projectName+
                        " commit "+commit.getHash() + "-> START");
                int processComplete = runtimeProcess.waitFor(); // value 0 indicates normal termination

                if(processComplete==0){
                    System.out.println("#### DESIGNITE, progetto "+projectName+
                            " commit "+commit.getHash() + "-> END");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String infoAggiuntive = commit.getHash() + "," + commit.getDate().getTime() + ",";
            // per ogni file csv chiamo createTempFile
            String csvArchitectureSmells =
                    createTempFile(pathOutput.getName() +"/" + pathJavaPoet.getName()+"/"+commit.getHash(), "/ArchitectureSmells.csv").getPath();
           String csvDesignSmells =
                   createTempFile(pathOutput.getName() +"/" + pathJavaPoet.getName()+"/"+commit.getHash(), "/DesignSmells.csv").getPath();
            String csvImplementationSmells =
                    createTempFile(pathOutput.getName() +"/" + pathJavaPoet.getName()+"/"+commit.getHash(), "/ImplementationSmells.csv").getPath();
            String csvMethodMetrics =
                    createTempFile(pathOutput.getName() +"/" + pathJavaPoet.getName()+"/"+commit.getHash(), "/MethodMetrics.csv").getPath();
            String csvTypeMetrics =
                    createTempFile(pathOutput.getName() +"/" + pathJavaPoet.getName()+"/"+commit.getHash(), "/TypeMetrics.csv").getPath();

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
            pathOutput.delete();
        }
    }

    public List<String> getHashCommit() {
        return hashCommits;
    }

    public static File createTempFile(String prefix, String suffix) {
        File parent = new File(System.getProperty("java.io.tmpdir"));

        File temp = new File(parent, prefix + suffix);
        /*
        if (temp.exists()) {
            temp.delete();
        }
        */
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
        /*
        if (temp.exists()) {
            temp.delete();
        }
        */
        temp.mkdir();

        return temp;
    }

    public String getPathCommit(){
        return pathJavaPoet.getPath();
    }

}
