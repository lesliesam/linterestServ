package com.linterest.dto;

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
}
