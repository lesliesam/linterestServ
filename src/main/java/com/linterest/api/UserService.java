package com.linterest.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.linterest.HibernateUtil;
import com.linterest.dto.UserEntity;
import com.linterest.error.ServerErrorPasswordMismatch;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.net.ssl.SSLEngineResult;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
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
    public Response login(@FormParam("userName") String userName, @FormParam("password") String password ) {
        Gson gson = new GsonBuilder().create();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery(QUERY_USER + " where userName = '" + userName + "'");
        List<UserEntity> list = query.list();
        session.close();

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
