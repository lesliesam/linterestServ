package com.linterest.entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
@Entity
@Table(name = "arrangement", schema = "", catalog = "linterest")
public class ArrangementEntity {
    private int id;
    private UserEntity host;
    private Timestamp date;
    private String location;
    private String description;
    private MenuEntity menu;
    private String city;
    private Float latitude;
    private Float longitude;
    private Float price;
    private int statusId;
    private String theme;
    private String tag;
    private Integer guestNum;
    private String images;
    private String facilities;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "host_id", nullable = false)
    public UserEntity getHost() {
        return host;
    }

    public void setHost(UserEntity host) {
        this.host = host;
    }

    @Basic
    @Column(name = "date", nullable = true, insertable = true, updatable = true)
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Basic
    @Column(name = "location", nullable = true, insertable = true, updatable = true, length = 100)
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Basic
    @Column(name = "description", nullable = true, insertable = true, updatable = true, length = 1000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_id", nullable = false)
    public MenuEntity getMenu() {
        return menu;
    }

    public void setMenu(MenuEntity menu) {
        this.menu = menu;
    }

    @Basic
    @Column(name = "city", nullable = true, insertable = true, updatable = true, length = 20)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Basic
    @Column(name = "latitude", nullable = true, insertable = true, updatable = true, precision = 0)
    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    @Basic
    @Column(name = "longitude", nullable = true, insertable = true, updatable = true, precision = 0)
    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    @Basic
    @Column(name = "price", nullable = true, insertable = true, updatable = true, precision = 0)
    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    @Basic
    @Column(name = "status_id", nullable = false, insertable = true, updatable = true)
    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrangementEntity that = (ArrangementEntity) o;

        if (id != that.id) return false;
        if (statusId != that.statusId) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (city != null ? !city.equals(that.city) : that.city != null) return false;
        if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null) return false;
        if (longitude != null ? !longitude.equals(that.longitude) : that.longitude != null) return false;
        if (price != null ? !price.equals(that.price) : that.price != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + statusId;
        return result;
    }

    @Basic
    @Column(name = "theme", nullable = true, insertable = true, updatable = true, length = 50)
    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    @Basic
    @Column(name = "tag", nullable = true, insertable = true, updatable = true, length = 100)
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Basic
    @Column(name = "guest_num", nullable = true, insertable = true, updatable = true)
    public Integer getGuestNum() {
        return guestNum;
    }

    public void setGuestNum(Integer guestNum) {
        this.guestNum = guestNum;
    }

    @Basic
    @Column(name = "images", nullable = true, insertable = true, updatable = true, length = 1000)
    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    @Basic
    @Column(name = "facilities", nullable = true, insertable = true, updatable = true, length = 200)
    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }
}
