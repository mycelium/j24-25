package ru.lab.parser.entities;

import java.util.Objects;

public class Image {
    public String url;
    public int width;
    public int height;

    public Image(String url, int width, int height){
        this.url = url;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return width == image.width && height == image.height && Objects.equals(url, image.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, width, height);
    }
}
