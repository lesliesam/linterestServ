package com.linterest.dto;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
@Entity
@Table(name = "arrangementGuest", schema = "", catalog = "Linterest")
public class ArrangementGuestEntity {
    private int arrangementId;
    private int guestId;
    private byte isCoreHost;

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
    @Column(name = "is_core_host")
    public byte getIsCoreHost() {
        return isCoreHost;
    }

    public void setIsCoreHost(byte isCoreHost) {
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
        result = 31 * result + (int) isCoreHost;
        return result;
    }
}
