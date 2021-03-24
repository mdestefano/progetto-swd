
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.repodriller.RepoDriller;
import org.repodriller.RepositoryMining;
import org.repodriller.Study;
import org.repodriller.filter.commit.OnlyInBranches;
import org.repodriller.filter.commit.OnlyModificationsWithFileTypes;
import org.repodriller.filter.range.Commits;
import org.repodriller.persistence.csv.CSVFile;
import org.repodriller.scm.GitRemoteRepository;
import org.repodriller.scm.GitRepository;
import org.repodriller.scm.SCMRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyStudy implements Study {

    public static void main(String[] args) {
        new RepoDriller().start(new MyStudy());
    }

    private static int countOccurences(String word, char character) {
        int count = 0;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == character) {
                count++;
            }
        }
        return count;
    }

    public void mergeCSV(String pathDestination, String nomeFile, String hashCommit) throws IOException {

        nomeFile = "output/" + hashCommit + nomeFile;

        BufferedReader br = null;
        final String lineSep = System.getProperty("line.separator");
        File file = new File(nomeFile);
        Writer output = new BufferedWriter(new FileWriter(pathDestination, true));

        try {
            //names don't conflict or just use different folders
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            int i = 0;
            int numCol = 0;
            for (line = br.readLine(); line != null; line = br.readLine(), i++) {
                if (i != 0) {
                    if(countOccurences(line, ',') != numCol) {
                        StringBuffer buffer = new StringBuffer(line);
                        line = buffer.reverse().toString().replaceFirst(",","");
                        line = new StringBuffer(line).reverse().toString();
                    }
                    output.append(line + lineSep);
                } else {
                    numCol = countOccurences(line, ',');
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (br != null)
                br.close();
            output.close();
        }
    }

    public boolean deleteDirectory(File f) {
        File[] allContents = f.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return f.delete();
    }

    public List<String> getTags(){
        List<String> arrayReturn = new ArrayList<String>();
        JSONParser parser = new JSONParser();
        JSONArray a = null;
        try {
            a = (JSONArray) parser.parse(new FileReader("outputFinali/javapoet/tag.json"));
            for (Object o : a) {
                JSONObject info = (JSONObject) o;
                JSONObject commit = (JSONObject) info.get("commit");
                arrayReturn.add(commit.get("sha").toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return arrayReturn;
    }




    @Override
    public void execute() {
        DeveloperVisitor developerVisitor = new DeveloperVisitor("javapoet");

        String repoDir = "javapoet";

        File folder = new File("outputFinali/javapoet");
        if (!folder.exists()) {
            folder.mkdir();
        }

        SCMRepository scmRepository = GitRemoteRepository
                .hostedOn("https://github.com/square/javapoet.git") // URL like: https://github.com/mauricioaniche/repodriller.git
                .inTempDir(repoDir)
                .buildAsSCMRepository();

        ProcessBuilder builder = new ProcessBuilder("curl","https://api.github.com/repos/square/javapoet/tags");
        builder.redirectOutput(new File("outputFinali/javapoet/tag.json"));
        try {
            Process p = builder.start();// may throw IOException
            int i = p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        List<String> arrayTags = getTags();
        new RepositoryMining()
                .in(
                        GitRepository.singleProject(repoDir)
                )
                .through(Commits.list(arrayTags))
                .visitorsAreThreadSafe(true) // Threads are possible.
                .visitorsChangeRepoState(true) // Each thread needs its own copy of the repo.
                .withThreads() // Now pick a good number of threads for my machine.
                .process(developerVisitor, new CSVFile("file.csv"))
                .mine();

        File file = new File(repoDir);
        deleteDirectory(file);

        Writer output = null, output1 = null, output2 = null, output3 = null, output4 = null;

        try {
            output = new BufferedWriter(new FileWriter("outputFinali/javapoet/ArchitectureSmells.csv"));
            output1 = new BufferedWriter(new FileWriter("outputFinali/javapoet/DesignSmells.csv"));
            output2 = new BufferedWriter(new FileWriter("outputFinali/javapoet/ImplementationSmells.csv"));
            output3 = new BufferedWriter(new FileWriter("outputFinali/javapoet/MethodMetrics.csv"));
            output4 = new BufferedWriter(new FileWriter("outputFinali/javapoet/TypeMetrics.csv"));

            final String lineSep = System.getProperty("line.separator");

            output.write("HashCommit, Date, Project Name, Package Name, Architecture Smell, Cause of the Smell" + lineSep);
            output1.write("HashCommit, Date, Project Name, Package Name, Type Name, Design Smell, Cause of the Smell" + lineSep);
            output2.write("HashCommit, Date, Project Name, Package Name, Type Name, Method Name, Implementation Smell, Cause of the Smell" + lineSep);
            output3.write("HashCommit, Date, Project Name, Package Name, Type Name, Method Name, LOC, CC, PC" + lineSep);
            output4.write("HashCommit, Date, Project Name, Package Name, Type Name, NOF, NOPF, NOM, NOPM, LOC, WMC, NC, DIT, LCOM, FANIN, FANOUT, File path" + lineSep);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
                output1.close();
                output2.close();
                output3.close();
                output4.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        List<String> hashCommits = developerVisitor.getHashCommit();

        for (int i = 0; i < hashCommits.size(); i++) {
            try {
                mergeCSV("outputFinali/javapoet/ArchitectureSmells.csv", "/ArchitectureSmells.csv", hashCommits.get(i));
                mergeCSV("outputFinali/javapoet/DesignSmells.csv", "/DesignSmells.csv", hashCommits.get(i));
                mergeCSV("outputFinali/javapoet/ImplementationSmells.csv", "/ImplementationSmells.csv", hashCommits.get(i));
                mergeCSV("outputFinali/javapoet/MethodMetrics.csv", "/MethodMetrics.csv", hashCommits.get(i));
                mergeCSV("outputFinali/javapoet/TypeMetrics.csv", "/TypeMetrics.csv", hashCommits.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
