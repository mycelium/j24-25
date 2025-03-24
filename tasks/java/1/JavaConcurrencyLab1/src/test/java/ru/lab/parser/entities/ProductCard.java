package ru.lab.parser.entities;

import java.util.*;

public class ProductCard {
    public int id;
    public String type;
    public String name;
    public Image image;
    public Image thumbnail;
    public List<Category> categories;

    public ProductCard(
        int id,
        String type,
        String name,
        Image image,
        Image thumbnail,
        List<Category> categories
    ){
        this.id = id;
        this.type = type;
        this.name = name;
        this.image = image;
        this.thumbnail = thumbnail;
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductCard that = (ProductCard) o;
        return id == that.id && Objects.equals(type, that.type) && Objects.equals(name, that.name) &&
                Objects.equals(image, that.image) && Objects.equals(thumbnail, that.thumbnail) &&
                Objects.equals(categories, that.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, name, image, thumbnail, categories);
    }
}
