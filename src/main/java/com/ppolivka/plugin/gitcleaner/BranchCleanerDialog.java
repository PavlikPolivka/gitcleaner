package com.ppolivka.plugin.gitcleaner;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.CollectionListModel;
import com.ppolivka.plugin.gitcleaner.list.CheckboxListItem;
import com.ppolivka.plugin.gitcleaner.list.CheckboxListRenderer;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class BranchCleanerDialog extends DialogWrapper {

    private JPanel mainView;
    private JList<CheckboxListItem> branchList;

    private BranchCleanerWorker branchCleanerWorker;

    private List<String> branchesToDelete = new ArrayList<>();

    public BranchCleanerDialog(BranchCleanerWorker branchCleanerWorker) {
        super(branchCleanerWorker.getProject());
        this.branchCleanerWorker = branchCleanerWorker;
        init();
        List<String> unmergedBranches = branchCleanerWorker.getUnmergedBranches();
        List<CheckboxListItem> listModel = branchCleanerWorker.getLocalBranches().stream()
                .map(localBranch -> new CheckboxListItem(localBranch, !unmergedBranches.contains(localBranch)))
                .collect(toList());
        branchList.setModel(new CollectionListModel<>(listModel));
        branchList.setCellRenderer(new CheckboxListRenderer());
        branchList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        branchList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                JList<CheckboxListItem> list = (JList<CheckboxListItem>) event.getSource();
                int index = list.locationToIndex(event.getPoint());
                CheckboxListItem item = list.getModel().getElementAt(index);
                item.setSelected(!item.isSelected());
                list.repaint(list.getCellBounds(index, index));
            }
        });
    }

    @Override
    protected void init() {
        super.init();
        setTitle("Branches to delete");
        setHorizontalStretch(2f);
        setVerticalStretch(1f);
        setOKButtonText("Delete selected");
    }

    @Override
    protected void doOKAction() {
        CollectionListModel<CheckboxListItem> model = (CollectionListModel<CheckboxListItem>) branchList.getModel();
        branchesToDelete = model.getItems().stream()
                .filter(CheckboxListItem::isSelected)
                .map(CheckboxListItem::toString)
                .collect(toList());
        branchCleanerWorker.deleteBranches(branchesToDelete);
        super.doOKAction();
    }



    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainView;
    }


}
