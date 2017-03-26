package com.eju.zejia.data.models;

import java.util.HashSet;
import java.util.List;


public class Filter {

    String title;
    String description;
    List<FilterBean.FilterItemBean> items;
    HashSet<Integer> checked;

    boolean isCollapse;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FilterBean.FilterItemBean> getItems() {
        return items;
    }

    public void setItems(List<FilterBean.FilterItemBean> items) {
        this.items = items;
    }

    public HashSet<Integer> getChecked() {
        return checked;
    }

    public void setChecked(HashSet<Integer> checked) {
        this.checked = checked;
    }

    public boolean isCollapse() {
        return isCollapse;
    }

    public void setCollapse(boolean collapse) {
        isCollapse = collapse;
    }

    public void toggleCollapse() {
        isCollapse = !isCollapse;
    }
}
