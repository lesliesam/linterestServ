package com.linterest.dto;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class ImageDto {
    public String storeETag;
    public String path;

    public  ImageDto(String storeETag, String path) {
        this.storeETag = storeETag;
        this.path = path;
    }
}
