package com.ppolivka.plugin.gitcleaner;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.config.GitVcsApplicationSettings;
import git4idea.config.GitVersion;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.intellij.openapi.ui.Messages.showErrorDialog;

public class GitUtil {

    public static boolean testGitExecutable(final Project project) {
        final GitVcsApplicationSettings settings = GitVcsApplicationSettings.getInstance();
        final String executable = settings.getPathToGit();
        final GitVersion version;
        try {
            version = GitVersion.identifyVersion(executable);
        } catch (Exception e) {
            showErrorDialog(project, "Cannot find git executable.", "Cannot Find Git");
            return false;
        }

        if (!version.isSupported()) {
            showErrorDialog(project, "Your version of git is not supported.", "Cannot Find Git");
            return false;
        }
        return true;
    }

    @Nullable
    public static GitRepository getGitRepository(@NotNull Project project, @Nullable VirtualFile file) {
        GitRepositoryManager manager = git4idea.GitUtil.getRepositoryManager(project);
        List<GitRepository> repositories = manager.getRepositories();
        if (repositories.size() == 0) {
            return null;
        }
        if (repositories.size() == 1) {
            return repositories.get(0);
        }
        if (file != null) {
            GitRepository repository = manager.getRepositoryForFile(file);
            if (repository != null) {
                return repository;
            }
        }
        return manager.getRepositoryForFile(project.getBaseDir());
    }

}
