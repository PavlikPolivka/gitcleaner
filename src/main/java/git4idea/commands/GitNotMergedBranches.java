package git4idea.commands;

import git4idea.repo.GitRepository;

import java.util.List;

public class GitNotMergedBranches extends GitImpl {

    public List<String> listUnmergedBranchces(GitRepository repository) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.BRANCH);
        h.setSilent(false);
        h.setStdoutSuppressed(false);
        h.addParameters("--no-merged");

        GitCommandResult result = runCommand(h);
        return result.getOutput();
    }

}
