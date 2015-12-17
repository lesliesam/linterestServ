package com.linterest.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.linterest.HibernateUtil;
import com.linterest.dto.UserEntity;
import com.linterest.error.ServerErrorParamEmpty;
import com.linterest.error.ServerErrorPasswordMismatch;
import com.linterest.error.ServerErrorUserNotFound;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
@Api(value = "User service")
@Path("/user")
public class UserService {

    private static final String QUERY_USER = "from UserEntity";

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllUser() {
        Gson gson = new GsonBuilder().create();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery(QUERY_USER);
        List<UserEntity> list = query.list();
        session.close();

        return gson.toJson(list);
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@FormParam("userName")  String userName, @FormParam("password") String password ) {
        Gson gson = new GsonBuilder().create();

        if (userName == null || userName.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("userName"))).build();
        }

        if (password == null || password.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("password"))).build();
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery(QUERY_USER + " where userName = '" + userName + "'");
        List<UserEntity> list = query.list();
        session.close();

        if (list.size() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorUserNotFound())).build();
        }

        UserEntity user = null;
        boolean passwordMatch = false;
        Iterator<UserEntity> it = list.iterator();
        while (it.hasNext()) {
            user = it.next();
            if (user.getPassword().equals(password)) {
                passwordMatch = true;
            }
            // Only check the first.
            break;
        }

        if (passwordMatch && user != null) {
            return Response.status(Response.Status.OK).entity(gson.toJson(user)).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorPasswordMismatch())).build();
        }
    }
}
