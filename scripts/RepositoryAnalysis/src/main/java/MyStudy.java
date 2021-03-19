import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.repodriller.RepoDriller;
import org.repodriller.RepositoryMining;
import org.repodriller.Study;
import org.repodriller.filter.commit.OnlyInBranches;
import org.repodriller.filter.commit.OnlyModificationsWithFileTypes;
import org.repodriller.filter.range.Commits;
import org.repodriller.persistence.csv.CSVFile;
import org.repodriller.scm.GitRepository;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class MyStudy implements Study {

    public static void main(String[] args) {
        new RepoDriller().start(new MyStudy());
    }

   @Override
    public void execute() {
        CSVFile fileProva = new CSVFile("prova.csv");
        new RepositoryMining()
                .in(GitRepository.singleProject("/Users/marioinglese/Desktop/javapoet"))
                .through(Commits.range("0a6b21afd67b524d14c515c7685476ad978bab9a","fd7fad7e35ada981ef07fee0cd616f33c230420c"))
                .visitorsAreThreadSafe(true) // Threads are possible.
                .visitorsChangeRepoState(true) // Each thread needs its own copy of the repo.
                .withThreads() // Now pick a good number of threads for my machine.
                .filters(
                        new OnlyModificationsWithFileTypes(Arrays.asList(".java")),
                        new OnlyInBranches(Arrays.asList("master"))
                )
                .process(new DeveloperVisitor(), new CSVFile("file.csv"))
                .mine();

       try {
           Writer output = new BufferedWriter(new FileWriter("outputFinali/ArchitectureSmells.csv"));
           Writer output1 = new BufferedWriter(new FileWriter("outputFinali/DesignSmells.csv"));
           Writer output2= new BufferedWriter(new FileWriter("outputFinali/ImplementationSmells.csv"));
           Writer output3= new BufferedWriter(new FileWriter("outputFinali/MethodMetrics.csv"));
           Writer output4= new BufferedWriter(new FileWriter("outputFinali/TypeMetrics.csv"));

           new CSVPrinter(output, CSVFormat.DEFAULT.withHeader("Project Name","Package Name","Architecture Smell","Cause of the Smell", "HashCommit", "Date"));
           new CSVPrinter(output1, CSVFormat.DEFAULT.withHeader("Project Name","Package Name","Type Name","Design Smell","Cause of the Smell", "HashCommit, Date"));
           new CSVPrinter(output2, CSVFormat.DEFAULT.withHeader("Project Name","Package Name","Type Name","Method Name","Implementation Smell,Cause of the Smell", "HashCommit", "Date"));
           new CSVPrinter(output3, CSVFormat.DEFAULT.withHeader("Project Name","Package Name","Type Name,Method Name","LOC","CC","PC", "HashCommit", "Date"));
           new CSVPrinter(output4, CSVFormat.DEFAULT.withHeader("Project Name","Package Name","Type Name","NOF,NOPF","NOM","NOPM","LOC","WMC","NC","DIT","LCOM","FANIN","FANOUT","File path", "HashCommit", "Date"));

       } catch (IOException e) {
           e.printStackTrace();
       }

       new RepositoryMining()
               .in(GitRepository.singleProject("/Users/marioinglese/Desktop/javapoet"))
               .through(Commits.range("0a6b21afd67b524d14c515c7685476ad978bab9a","fd7fad7e35ada981ef07fee0cd616f33c230420c"))
               .visitorsAreThreadSafe(true) // Threads are possible.
               .visitorsChangeRepoState(true) // Each thread needs its own copy of the repo.
               .withThreads() // Now pick a good number of threads for my machine.
               .filters(
                       new OnlyModificationsWithFileTypes(Arrays.asList(".java")),
                       new OnlyInBranches(Arrays.asList("master"))
               )
               .process(new ProvaVisitor())
               .mine();
    }

}
