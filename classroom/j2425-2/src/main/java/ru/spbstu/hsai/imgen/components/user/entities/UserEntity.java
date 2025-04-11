package ru.spbstu.hsai.imgen.components.user.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.persistence.Entity;


@Entity
@Table("users")
public class UserEntity {
    @Id
    @Column("userID")
    private Integer userId;

    @Column("login")
    private String login;
    @Column("password")
    private String password;
    @Column("quota")
    private Integer quota;

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
