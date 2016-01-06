package com.linterest.interceptor;

import com.google.inject.Inject;
import com.linterest.GuiceInstance;
import com.linterest.annotation.CacheEnabled;
import com.linterest.annotation.UserSession;
import com.linterest.entity.UserEntity;
import com.linterest.services.CacheClientImpl;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class UserSessionCacheInterceptor implements MethodInterceptor {
    public static final String STORE = "store";
    public static final String GET = "get";

    public UserSessionCacheInterceptor() {

    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        UserSession annotation = invocation.getMethod().getAnnotation(UserSession.class);
        CacheClientImpl cacheClient = GuiceInstance.getGuiceInjector().getInstance(CacheClientImpl.class);
        if (annotation.type().equals(GET)) {
            Object[] arguments = invocation.getArguments();
            String session = (String) arguments[0];

            UserEntity entity = cacheClient.getUserEntityWithSession(session);
            if (entity != null) {
                List<UserEntity> result = new ArrayList<UserEntity>();
                result.add(entity);
                return result;
            } else {
                return invocation.proceed();
            }
        } else if (annotation.type().equals(STORE)) {
            Object[] arguments = invocation.getArguments();
            UserEntity entity = (UserEntity) arguments[0];
            cacheClient.storeUserEntityWithSession(entity.getSession(), entity);
            return invocation.proceed();
        }

        return null;
    }
}
