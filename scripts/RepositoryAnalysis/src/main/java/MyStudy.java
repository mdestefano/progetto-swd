import org.repodriller.RepoDriller;
import org.repodriller.RepositoryMining;
import org.repodriller.Study;
import org.repodriller.domain.Commit;
import org.repodriller.domain.Modification;
import org.repodriller.filter.commit.OnlyInBranches;
import org.repodriller.filter.commit.OnlyInMainBranch;
import org.repodriller.filter.commit.OnlyModificationsWithFileTypes;
import org.repodriller.filter.commit.OnlyNoMerge;
import org.repodriller.filter.range.Commits;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.persistence.csv.CSVFile;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.GitRepository;
import org.repodriller.scm.SCMRepository;

import java.util.Arrays;

public class MyStudy implements Study {

    public static void main(String[] args) {
        new RepoDriller().start(new MyStudy());
    }

   @Override
    public void execute() {
        new RepositoryMining()
                .in(GitRepository.singleProject("javapoet"))
                .through(Commits.all())
                .visitorsAreThreadSafe(true) // Threads are possible.
                .visitorsChangeRepoState(true) // Each thread needs its own copy of the repo.
                .withThreads() // Now pick a good number of threads for my machine.
                .filters(
                        new OnlyInBranches(Arrays.asList("master"))
                )
                .process(new DeveloperVisitor(), new CSVFile("file.csv"))
                .mine();
    }

}
