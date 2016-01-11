package com.linterest.services;

import com.linterest.entity.*;

import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public interface UserServices {
    List<UserEntity> getAllUsers();
    List<UserEntity> getUser(String userName);
    List<UserEntity> getUserWithAuthSession(String authSession);
    List<UserHobbyEntity> getUserHobby(UserEntity user);
    UserEntity userSignup(String userName, String password);
    UserEntity userSignupWithDevice(String deviceName, String deviceId);
    UserEntity updateUserSession(UserEntity user);
    UserEntity updateUserGender(UserEntity user, String gender);
    UserEntity updateUserPersonality(UserEntity user, PersonalityEntity personality);
    UserEntity updateUserHobby(UserEntity user, HobbyEntity hobby, boolean deleted);
    UserEntity updateUserDisplayName(UserEntity user, String displayName);
}
