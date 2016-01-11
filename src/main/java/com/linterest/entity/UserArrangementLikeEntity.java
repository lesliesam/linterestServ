package com.linterest.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
@Entity
@Table(name = "userArrangementLike", schema = "", catalog = "linterest")
public class UserArrangementLikeEntity {
    private UserEntity user;
    private ArrangementEntity arrangement;
    private Timestamp updateTime;
    private int id;
    private Boolean isLike;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "arrangement_id", nullable = false)
    public ArrangementEntity getArrangement() {
        return arrangement;
    }

    public void setArrangement(ArrangementEntity arrangement) {
        this.arrangement = arrangement;
    }

    @Basic
    @Column(name = "update_time", nullable = false, insertable = true, updatable = true)
    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserArrangementLikeEntity that = (UserArrangementLikeEntity) o;

        if (user.equals(that.user)) return false;
        if (arrangement.equals(that.arrangement)) return false;
        if (updateTime != null ? !updateTime.equals(that.updateTime) : that.updateTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + arrangement.hashCode();
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        return result;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "isLike", columnDefinition = "BIT", length = 1, nullable = true, insertable = true, updatable = true)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public boolean getIsLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }
}
