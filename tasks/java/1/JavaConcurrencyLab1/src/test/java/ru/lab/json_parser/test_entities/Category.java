package ru.lab.json_parser.test_entities;

import java.util.Objects;

public class Category {
    public int categoryID;
    public String categoryName;

    public Category(int categoryID, String categoryName){
        this.categoryID = categoryID;
        this.categoryName = categoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return categoryID == category.categoryID && Objects.equals(categoryName, category.categoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryID, categoryName);
    }
}
