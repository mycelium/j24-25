package ru.spbstu.hsai.imgen.components.user.entities;

public class UserEntity {

   private Integer userId;
   private  String login;
   private  String password;
   private Integer quota;

    public UserEntity(Integer userId, String login, Integer quota) {
        this.login = login;
        this.userId = userId;
        this.quota = quota;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getQuota() {
        return quota;
    }

    public void setQuota(Integer quota) {
        this.quota = quota;
    }
}
