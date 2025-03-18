package ru.spbstu.telematics.java;
import java.util.List;

public class LiJsonUser {
    private String name;
    private String surname;
    private long age;
    private boolean isOnline;
    private List<String> favMovies;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public List<String> getFavMovies() {
        return favMovies;
    }

    public void setFavMovies(List<String> favMovies) {
        this.favMovies = favMovies;
    }
}
