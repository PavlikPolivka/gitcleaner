package com.ppolivka.plugin.gitcleaner;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.DialogManager;


public class BranchCleanerAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {

        Project project = event.getProject();
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);

        if (!GitUtil.testGitExecutable(project)) {
            return;
        }

        BranchCleanerWorker branchCleanerWorker = BranchCleanerWorker.create(project, file);
        if(branchCleanerWorker != null) {
            BranchCleanerDialog branchCleanerDialog = new BranchCleanerDialog(branchCleanerWorker);
            DialogManager.show(branchCleanerDialog);
        }
    }
}
