package com.linterest.entity;

import javax.persistence.*;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
@Entity
@Table(name = "userPersonality", schema = "", catalog = "linterest")
public class UserPersonalityEntity {
    private int userId;
    private int personalityId;
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
    @Column(name = "personality_id")
    public int getPersonalityId() {
        return personalityId;
    }

    public void setPersonalityId(int personalityId) {
        this.personalityId = personalityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserPersonalityEntity that = (UserPersonalityEntity) o;

        if (userId != that.userId) return false;
        if (personalityId != that.personalityId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + personalityId;
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
