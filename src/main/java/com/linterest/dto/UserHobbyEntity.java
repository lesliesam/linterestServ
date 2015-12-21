package com.linterest.dto;

import javax.persistence.*;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
@Entity
@Table(name = "userHobby", schema = "", catalog = "linterest")
public class UserHobbyEntity {
    private int userId;
    private int hobbyId;
    private int id;

    @Basic
    @Column(name = "user_id")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "hobby_id")
    public int getHobbyId() {
        return hobbyId;
    }

    public void setHobbyId(int hobbyId) {
        this.hobbyId = hobbyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserHobbyEntity that = (UserHobbyEntity) o;

        if (userId != that.userId) return false;
        if (hobbyId != that.hobbyId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + hobbyId;
        return result;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
