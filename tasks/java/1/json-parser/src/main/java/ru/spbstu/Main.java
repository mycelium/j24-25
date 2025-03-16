package ru.spbstu;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        String json = "{\"skills\":[\"Teamwork\",\"Leadership\",\"Initiative\"], \"name\":\"John\",\"age\":22,\"isStudent\":null,\"hobbies\":[\"reading\",\"coding\"]}";
        // String json2 = "{address:[1, 2, { nestedKey: [true, false] }, [3,4]],\"array\":[1,\"12\",3,14,15,61], \"bool\":false}";

        Map<String, Object> map = JsonParser.fromJsonToMap(json);
        System.out.println(map);

        User user = JsonParser.fromJsonToClass(json, User.class);
        System.out.println(user);

        System.out.println(JsonParser.fromObjToJson(user));

        var user2 = new User();
        System.out.println(user2);
    }
}

class User {
    private String name;
    private Integer age;
    private Boolean isStudent;
    private String[] hobbies;
    private Set<String> skills;

    User() {
        this.name = "exampleName";
        this.age = 3;
        this.isStudent = false;
        this.hobbies = new String[]{"hobby1", "hobby2"};
        this.skills = new HashSet<>(Arrays.asList("skill1", "skill2"));
    }

    @Override
    public String toString() {
        return "User{name='" + name + "', age=" + age + ", isStudent=" + isStudent +
                ", hobbies=" + Arrays.toString(hobbies) + ", skills=" + skills +  "}";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Boolean getIsStudent() {
        return isStudent;
    }

    public void setIsStudent(Boolean isStudent) {
        this.isStudent = isStudent;
    }

    public String[] getHobbies() {
        return hobbies;
    }

    public void setHobbies(String[] hobbies) {
        this.hobbies = hobbies;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }
}