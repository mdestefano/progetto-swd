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
import java.util.Arrays;

public class MyStudy implements Study {

    public static void main(String[] args) {
        new RepoDriller().start(new MyStudy());
    }

   @Override
    public void execute() {
        new RepositoryMining()
                .in(GitRepository.singleProject("C:\\Users\\Armando\\Desktop\\javapoet"))
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

       Writer output = null, output1 = null, output2 = null, output3 = null, output4 = null;

       try {
           output = new BufferedWriter(new FileWriter("outputFinali/ArchitectureSmells.csv", true));
           output1 = new BufferedWriter(new FileWriter("outputFinali/DesignSmells.csv", true));
           output2= new BufferedWriter(new FileWriter("outputFinali/ImplementationSmells.csv", true));
           output3= new BufferedWriter(new FileWriter("outputFinali/MethodMetrics.csv", true));
           output4= new BufferedWriter(new FileWriter("outputFinali/TypeMetrics.csv", true));

           final String lineSep = System.getProperty("line.separator");

           output.append("HashCommit, Date, Project Name, Package Name, Architecture Smell, Cause of the Smell" + lineSep);
           output1.append("HashCommit, Date, Project Name, Package Name, Type Name, Design Smell, Cause of the Smell" + lineSep);
           output2.append("HashCommit, Date, Project Name, Package Name, Type Name, Method Name, Implementation Smell, Cause of the Smell" + lineSep);
           output3.append("HashCommit, Date, Project Name, Package Name, Type Name, Method Name, LOC, CC, PC" + lineSep);
           output4.append("HashCommit, Date, Project Name, Package Name, Type Name, NOF, NOPF, NOM, NOPM, LOC, WMC, NC, DIT, LCOM, FANIN, FANOUT, File path" + lineSep);

       } catch (IOException e) {
           e.printStackTrace();
       } finally  {
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

       new RepositoryMining()
               .in(GitRepository.singleProject("C:\\Users\\Armando\\Desktop\\javapoet"))
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
