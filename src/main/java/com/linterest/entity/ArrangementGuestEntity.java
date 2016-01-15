package com.linterest.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
@Entity
@Table(name = "arrangementGuest", schema = "", catalog = "linterest")
public class ArrangementGuestEntity {
    private int arrangementId;
    private int guestId;
    private boolean isCoreHost;
    private int id;
    private int guestNum;
    private int orderStatus;
    private String comment;
    private Integer commentStar;
    private Integer updateTimes;

    @Basic
    @Column(name = "arrangement_id")
    public int getArrangementId() {
        return arrangementId;
    }

    public void setArrangementId(int arrangementId) {
        this.arrangementId = arrangementId;
    }

    @Basic
    @Column(name = "guest_id")
    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    @Basic
    @Column(name = "is_core_host", columnDefinition = "BIT", length = 1)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public boolean getIsCoreHost() {
        return isCoreHost;
    }

    public void setIsCoreHost(boolean isCoreHost) {
        this.isCoreHost = isCoreHost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrangementGuestEntity that = (ArrangementGuestEntity) o;

        if (arrangementId != that.arrangementId) return false;
        if (guestId != that.guestId) return false;
        if (isCoreHost != that.isCoreHost) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = arrangementId;
        result = 31 * result + guestId;
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

    @Basic
    @Column(name = "guest_num", nullable = false, insertable = true, updatable = true)
    public int getGuestNum() {
        return guestNum;
    }

    public void setGuestNum(int guestNum) {
        this.guestNum = guestNum;
    }

    @Basic
    @Column(name = "order_status", nullable = false, insertable = true, updatable = true)
    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Basic
    @Column(name = "comment", nullable = true, insertable = true, updatable = true, length = 1000)
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Basic
    @Column(name = "commentStar", nullable = true, insertable = true, updatable = true)
    public Integer getCommentStar() {
        return commentStar;
    }

    public void setCommentStar(Integer commentStar) {
        this.commentStar = commentStar;
    }

    @Basic
    @Column(name = "update_times", nullable = true, insertable = true, updatable = true)
    public Integer getUpdateTimes() {
        return updateTimes;
    }

    public void setUpdateTimes(Integer updateTimes) {
        this.updateTimes = updateTimes;
    }
}
