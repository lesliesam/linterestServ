package com.linterest.dto;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
@Entity
@Table(name = "userPersonality", schema = "", catalog = "Linterest")
public class UserPersonalityEntity {
    private int userId;
    private int personalityId;

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
}
