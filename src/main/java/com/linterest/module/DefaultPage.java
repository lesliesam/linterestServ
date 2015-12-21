package com.linterest.module;

import io.swagger.annotations.Api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */

@Api(value = "Welcome Page", hidden = true)
@Path("/")
public class DefaultPage {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getDefaultString() {
        return "Hello from Linterest.";
    }
}
