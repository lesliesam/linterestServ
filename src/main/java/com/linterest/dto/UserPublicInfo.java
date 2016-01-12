package com.linterest.dto;

import com.linterest.entity.UserEntity;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class UserPublicInfo {
    public int id;
    public boolean isHost;
    public String displayName;
    public String gender;
    public String phoneNum;

    public UserPublicInfo(UserEntity userEntity) {
        id = userEntity.getId();
        isHost = userEntity.getIsHost();
        displayName = userEntity.getDisplayName();
        gender = userEntity.getGender();
        phoneNum = userEntity.getPhoneNum();
    }
}
