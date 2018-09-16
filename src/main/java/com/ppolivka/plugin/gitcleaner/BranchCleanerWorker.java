package com.ppolivka.plugin.gitcleaner;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitLocalBranch;
import git4idea.GitReference;
import git4idea.branch.GitBranchUiHandlerImpl;
import git4idea.branch.GitBranchWorker;
import git4idea.commands.Git;
import git4idea.commands.GitNotMergedBranches;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.openapi.ui.Messages.showErrorDialog;
import static java.util.Collections.singletonList;

public class BranchCleanerWorker {

    private Project project;
    private VirtualFile virtualFile;
    private Git git;
    private GitRepository gitRepository;
    private List<String> localBranches;
    private List<String> unmergedBranches;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public VirtualFile getVirtualFile() {
        return virtualFile;
    }

    public void setVirtualFile(VirtualFile virtualFile) {
        this.virtualFile = virtualFile;
    }

    public Git getGit() {
        return git;
    }

    public void setGit(Git git) {
        this.git = git;
    }

    public GitRepository getGitRepository() {
        return gitRepository;
    }

    public void setGitRepository(GitRepository gitRepository) {
        this.gitRepository = gitRepository;
    }

    public List<String> getLocalBranches() {
        return localBranches;
    }

    public void setLocalBranches(List<String> localBranches) {
        this.localBranches = localBranches;
    }

    public List<String> getUnmergedBranches() {
        return unmergedBranches;
    }

    public void setUnmergedBranches(List<String> unmergedBranches) {
        this.unmergedBranches = unmergedBranches;
    }

    public void deleteBranches(List<String> branchesToDelete) {
        new Task.Backgroundable(project, "Deleting branches...") {

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                GitBranchWorker branchWorker = branchWorker(indicator);
                for(String branch : branchesToDelete) {
                    branchWorker.deleteBranch(branch, singletonList(gitRepository));
                }
            }
        }.queue();
    }

    private GitBranchWorker branchWorker(ProgressIndicator indicator) {
        return new GitBranchWorker(project, git, new GitBranchUiHandlerImpl(project, git, indicator));
    }

    public static BranchCleanerWorker create(Project project, VirtualFile virtualFile) {
        return WorkerUtil.computeValueInModal(project, "Loading branch info...", indicator -> {
            BranchCleanerWorker branchCleanerWorker = new BranchCleanerWorker();
            branchCleanerWorker.setProject(project);
            branchCleanerWorker.setVirtualFile(virtualFile);

            Git git = ServiceManager.getService(Git.class);
            branchCleanerWorker.setGit(git);

            GitRepository gitRepository = GitUtil.getGitRepository(project, virtualFile);
            if (gitRepository == null) {
                showErrorDialog(project, "Can't find git repository", "Cannot List Branches");
                throw new RuntimeException("Cannot find git repository");
            }
            branchCleanerWorker.setGitRepository(gitRepository);
            gitRepository.update();

            GitLocalBranch currentBranch = gitRepository.getCurrentBranch();

            List<String> localBranches = gitRepository.getBranches().getLocalBranches().stream()
                    .filter(gitLocalBranch -> gitLocalBranch.findTrackedBranch(gitRepository) == null)
                    .filter(gitLocalBranch -> !gitLocalBranch.equals(currentBranch))
                    .map(GitReference::getName)
                    .collect(Collectors.toList());
            branchCleanerWorker.setLocalBranches(localBranches);

            GitNotMergedBranches gitNotMergedBranches = new GitNotMergedBranches();
            List<String> unmergedBranches = gitNotMergedBranches.listUnmergedBranchces(gitRepository);
            unmergedBranches = unmergedBranches.stream().map(String::trim).collect(Collectors.toList());
            branchCleanerWorker.setUnmergedBranches(unmergedBranches);

            return branchCleanerWorker;
        });
    }

}
