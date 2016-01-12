package com.linterest.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.linterest.GuiceInstance;
import com.linterest.entity.UserEntity;
import com.linterest.error.ServerErrorParamEmpty;
import com.linterest.error.ServerErrorUserNotFound;
import com.linterest.error.ServerErrorValidationCodeError;
import com.linterest.services.CacheClientImpl;
import com.linterest.services.UserServices;
import com.linterest.utils.SmsApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */

@Api(value = "Phone number Binding service")
@Path("/phoneBinding")
public class PhoneBindingModule {

    @POST
    @Path("/sendValidationCode")
    @ApiOperation(value = "获取手机验证码")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendValidationCode(@FormParam("phoneNumber") String phoneNumber) {
        Gson gson = new GsonBuilder().create();

        CacheClientImpl cacheClient = GuiceInstance.getGuiceInjector().getInstance(CacheClientImpl.class);
        int randomCode = (int)(Math.random() * 1000000);
        String code = String.format("%06d", randomCode);

        String response = "";
        try {
            response = SmsApi.sendPhoneBindingValidationCode(code, phoneNumber);
            cacheClient.storeValidationCodeWithPhoneNum(phoneNumber, code);
        } catch (IOException e) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(gson.toJson(e)).build();
        }

        return Response.ok().entity(response).build();
    }

    @POST
    @Path("/checkValidationCode")
    @ApiOperation(value = "绑定手机", notes = "验证手机号码与验证码是否匹配")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkValidationCode(@HeaderParam("authSession") String authSession, @FormParam("phoneNumber") String phoneNumber, @FormParam("validationCode") String validationCode) {
        Gson gson = new GsonBuilder().create();

        if (authSession == null || authSession.length() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorParamEmpty("authSession"))).build();
        }

        CacheClientImpl cacheClient = GuiceInstance.getGuiceInjector().getInstance(CacheClientImpl.class);
        if (!validationCode.equals(cacheClient.getValidationCodeWithPhoneNum(phoneNumber))) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorValidationCodeError())).build();
        }

        UserServices services = GuiceInstance.getGuiceInjector().getInstance(UserServices.class);
        List<UserEntity> list = services.getUserWithAuthSession(authSession);
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorUserNotFound())).build();
        }

        UserEntity user = list.get(0);
        services.updatePhoneNumber(user, phoneNumber);

        return Response.status(Response.Status.OK).entity(gson.toJson(user)).build();
    }
}
