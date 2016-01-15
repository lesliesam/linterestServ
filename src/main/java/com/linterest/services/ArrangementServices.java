package com.linterest.services;

import com.linterest.entity.*;

import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public interface ArrangementServices {

    ArrangementEntity setup(UserEntity host, String theme, String tag, float price, int guestNum, String address, float latitude, float longitude, String images, String facilities, MenuEntity menu);
    List<ArrangementEntity> getById(String id);
    List<ArrangementEntity> getNewByUser(UserEntity user, int limit);
    List<ArrangementEntity> getLikedByUser(UserEntity user);
    UserArrangementLikeEntity userLikeArrangement(UserEntity user, ArrangementEntity arrangementEntity, boolean like);
    ArrangementGuestEntity joinOrQuitArrangement(UserEntity user, ArrangementEntity arrangementEntity, int guestNum, boolean isCoHost, boolean isJoin);
    List<ArrangementGuestEntity> getAllGuestInArrangement(ArrangementEntity arrangementEntity);
    int getPostCommentTimes(UserEntity user, ArrangementEntity arrangementEntity);
    ArrangementGuestEntity postComments(UserEntity user, ArrangementEntity arrangementEntity, String comments, int stars);
}
