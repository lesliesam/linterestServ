package com.linterest.module;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.linterest.Constants;
import com.linterest.dto.ImageDto;
import com.linterest.error.ServerErrorWithString;
import com.linterest.utils.AliyunOSSUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.digest.DigestUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
@Api(value = "Image service")
@Path("/image")
public class ImageModule {

    @POST
    @Path("/uploadImage")
    @ApiOperation(value = "上传图片", notes = "检查后缀名是否为jpg或png")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadImage(@FormDataParam("file") InputStream uploadedInputStream,
                                @FormDataParam("file") FormDataContentDisposition fileDetail) {
        Gson gson = new GsonBuilder().create();

        String fileName = fileDetail.getFileName();
        String[] fileNameSplits = fileName.split("[.]");
        String appendix = fileNameSplits[fileNameSplits.length - 1];
        if (fileNameSplits.length < 2 || !appendix.equalsIgnoreCase("jpg") && !appendix.equalsIgnoreCase("png")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorWithString("You can only upload images."))).build();
        }

        String storedFileName = DigestUtils.md5Hex(String.valueOf(System.currentTimeMillis())) + "_" + fileName;

        ImageDto imageDto = null;
        try {
            String storeETag = AliyunOSSUtils.uploadFile(storedFileName, uploadedInputStream);
            imageDto = new ImageDto(storeETag, Constants.CDN_URL + storedFileName);
        } catch (OSSException | ClientException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(exception)).build();
        }

        return Response.ok().entity(gson.toJson(imageDto)).build();
    }
}
