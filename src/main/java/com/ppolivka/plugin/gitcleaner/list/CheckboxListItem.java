package com.ppolivka.plugin.gitcleaner.list;

public class CheckboxListItem {
    private String label;
    private boolean isSelected = true;

    public CheckboxListItem(String label, boolean isSelected) {
        this.label = label;
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String toString() {
        return label;
    }
}
