package com.linterest.dto;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
@Entity
@Table(name = "userScore", schema = "", catalog = "linterest")
public class UserScoreEntity {
    private int userId;
    private int scorerId;
    private int arrangementId;
    private Integer score;
    private Timestamp scoreTime;
    private String comments;
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
    @Column(name = "scorer_id")
    public int getScorerId() {
        return scorerId;
    }

    public void setScorerId(int scorerId) {
        this.scorerId = scorerId;
    }

    @Basic
    @Column(name = "arrangement_id")
    public int getArrangementId() {
        return arrangementId;
    }

    public void setArrangementId(int arrangementId) {
        this.arrangementId = arrangementId;
    }

    @Basic
    @Column(name = "score")
    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Basic
    @Column(name = "score_time")
    public Timestamp getScoreTime() {
        return scoreTime;
    }

    public void setScoreTime(Timestamp scoreTime) {
        this.scoreTime = scoreTime;
    }

    @Basic
    @Column(name = "comments")
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserScoreEntity that = (UserScoreEntity) o;

        if (userId != that.userId) return false;
        if (scorerId != that.scorerId) return false;
        if (arrangementId != that.arrangementId) return false;
        if (score != null ? !score.equals(that.score) : that.score != null) return false;
        if (scoreTime != null ? !scoreTime.equals(that.scoreTime) : that.scoreTime != null) return false;
        if (comments != null ? !comments.equals(that.comments) : that.comments != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + scorerId;
        result = 31 * result + arrangementId;
        result = 31 * result + (score != null ? score.hashCode() : 0);
        result = 31 * result + (scoreTime != null ? scoreTime.hashCode() : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        return result;
    }

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
