package com.ppolivka.plugin.gitcleaner.list;

import javax.swing.*;
import java.awt.*;

public class CheckboxListRenderer extends JCheckBox implements ListCellRenderer<CheckboxListItem> {
    @Override
    public Component getListCellRendererComponent(JList<? extends CheckboxListItem> list, CheckboxListItem value, int index, boolean isSelected, boolean cellHasFocus) {
        setEnabled(list.isEnabled());
        setSelected(value.isSelected());
        setFont(list.getFont());
        setBackground(list.getBackground());
        setForeground(list.getForeground());
        setText(value.toString());
        return this;
    }
}
