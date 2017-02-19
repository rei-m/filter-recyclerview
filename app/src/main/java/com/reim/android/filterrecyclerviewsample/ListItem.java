package com.reim.android.filterrecyclerviewsample;

import com.reim.android.filterrecyclerview.FilterableItem;

public class ListItem implements FilterableItem {

    private final String name;

    private final String category;

    public ListItem(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListItem listItem = (ListItem) o;

        return name.equals(listItem.name) && category.equals(listItem.category);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + category.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ListItem{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
