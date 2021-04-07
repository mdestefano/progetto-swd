package mining;

import org.repodriller.RepoDriller;
import org.repodriller.RepositoryMining;
import org.repodriller.Study;
import org.repodriller.filter.range.Commits;
import org.repodriller.scm.GitRemoteRepository;
import org.repodriller.scm.GitRepository;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import utils.*;

public class MyStudy implements Study {
    private static String file;
    private static String baseOutputFolder;

    public static void main(String[] args) {
        file = args[0];
        baseOutputFolder = args[1];
        File fileOutputFolder = new File(baseOutputFolder);
        if(!fileOutputFolder.exists()) {
            fileOutputFolder.mkdirs();
        }
        new RepoDriller().start(new MyStudy());
    }

    @Override
    public void execute() {
        DeveloperVisitor developerVisitor;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            for (line = br.readLine(); line != null; line = br.readLine()) {

                String repoDir = UtilsGit.getNameFromGitUrl(line);
                File folder = new File(baseOutputFolder + repoDir);

                if (!folder.exists()) {
                    boolean mkdir = folder.mkdir();

                    if(mkdir){
                        System.out.println("### Directory creata");
                    }
                }

                GitRemoteRepository
                        .hostedOn(line)
                        .inTempDir(repoDir)
                        .buildAsSCMRepository();

                ProcessBuilder builder = new ProcessBuilder("curl", UtilsGit.getUrlTagsFromGitUrl(line));
                builder.redirectOutput(new File(baseOutputFolder + UtilsGit.getNameFromGitUrl(line) + "/tag.json"));

                Process p = builder.start();
                p.waitFor();

                HashMap<String,String> hashTags = UtilsGit.getTags(line, baseOutputFolder);

                developerVisitor = new DeveloperVisitor(repoDir ,hashTags);
                new RepositoryMining()
                        .in(
                                GitRepository.singleProject(repoDir)
                        )
                        .through(Commits.list(UtilsGit.getHashTag(hashTags)))
                        .visitorsAreThreadSafe(true) // Threads are possible.
                        .visitorsChangeRepoState(true) // Each thread needs its own copy of the repo.
                        .withThreads(Runtime.getRuntime().availableProcessors()) // Now pick a good number of threads for my machine.
                        .process(developerVisitor)
                        .mine();

                File file = new File(repoDir);
                boolean b = UtilsFileDirectory.deleteDirectory(file);

                if(b){
                    System.out.println("#### Cartella eliminata: " + file);
                }
                UtilsFileDirectory.addColumnsCSV(line, baseOutputFolder);

                List<String> hashCommits = developerVisitor.getHashCommit();
                UtilsCSV.mergeAll(hashCommits, line, developerVisitor.getPathCommit(), baseOutputFolder);

            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

}
