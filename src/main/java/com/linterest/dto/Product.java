package com.linterest.dto;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class Product {
    public String name;
    public String quality;
    public boolean complete;

    public Product(String name, String quality, boolean complete) {
        this.name = name;
        this.quality = quality;
        this.complete = complete;
    }
}
