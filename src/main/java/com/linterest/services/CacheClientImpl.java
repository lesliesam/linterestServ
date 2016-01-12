package com.linterest.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.linterest.entity.UserEntity;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.CASValue;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
@Singleton
public class CacheClientImpl {

    private MemcachedClient mClient = null;
    private static final int USER_SESSION_TIME_OUT = 60 * 60 * 24 * 15;
    private static final int PHONE_NUM_VALIDATION_CODE_TIME_OUT = 60 * 60;
    private static final int OPERATION_TIME = 15;

    @Inject
    public CacheClientImpl() {
    }

    private synchronized MemcachedClient getClient() {
        if (mClient == null) {
            try {
                mClient = new MemcachedClient(
                        new BinaryConnectionFactory(),
                        AddrUtil.getAddresses("120.55.87.231:11211"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mClient;
    }

    public void storeUserEntityWithSession(String session, UserEntity entity) {
        Gson gson = new GsonBuilder().create();
        String userEntityStr = gson.toJson(entity);
        OperationFuture future = getClient().set(session, USER_SESSION_TIME_OUT, userEntityStr);
        try {
            future.get(OPERATION_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            future.cancel();
        }
    }

    public UserEntity getUserEntityWithSession(String session) {
        Gson gson = new GsonBuilder().create();
        OperationFuture<CASValue<Object>> future =  getClient().asyncGetAndTouch(session, USER_SESSION_TIME_OUT);
        UserEntity entity = null;
        try {
            CASValue<Object> value = future.get(OPERATION_TIME, TimeUnit.SECONDS);
            if (value != null) {
                String entityStr = (String)value.getValue();
                entity = gson.fromJson(entityStr, UserEntity.class);
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            future.cancel();
        }

        return entity;
    }

    public void storeValidationCodeWithPhoneNum(String phoneNum, String validationCode) {
        OperationFuture future = getClient().set(phoneNum, PHONE_NUM_VALIDATION_CODE_TIME_OUT, validationCode);
        try {
            future.get(OPERATION_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            future.cancel();
        }
    }

    public String getValidationCodeWithPhoneNum(String phoneNum) {
        OperationFuture<CASValue<Object>> future =  getClient().asyncGetAndTouch(phoneNum, PHONE_NUM_VALIDATION_CODE_TIME_OUT);
        String validationCode = null;
        try {
            CASValue<Object> value = future.get(OPERATION_TIME, TimeUnit.SECONDS);
            if (value != null) {
                validationCode = (String) value.getValue();
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            future.cancel();
        }

        return validationCode;
    }
}
