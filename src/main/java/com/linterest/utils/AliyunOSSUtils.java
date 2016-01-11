package com.linterest.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class AliyunOSSUtils {

    public static void main(String[] args) {
        String endpoint = "http://oss.aliyuncs.com";
        String accessKeyId = "w8yS2NoNTJwd6Rgt";
        String accessKeySecret = "bV7PLOBptPuHg8RozpBV7SkO1R1yWv";
        String bucketName = "linterest-image";
        HashMap<String, String> files = new HashMap<>();

        files.put("1.pic.jpg", "/Users/trdehero/Downloads/1.pic.jpg");
        files.put("2.pic.jpg", "/Users/trdehero/Downloads/2.pic.jpg");
        files.put("3.pic.jpg", "/Users/trdehero/Downloads/3.pic.jpg");
        files.put("4.pic.jpg", "/Users/trdehero/Downloads/4.pic.jpg");
        files.put("5.pic.jpg", "/Users/trdehero/Downloads/5.pic.jpg");
        files.put("6.pic.jpg", "/Users/trdehero/Downloads/6.pic.jpg");
        files.put("7.pic.jpg", "/Users/trdehero/Downloads/7.pic.jpg");
        files.put("8.pic.jpg", "/Users/trdehero/Downloads/8.pic.jpg");
        files.put("9.pic.jpg", "/Users/trdehero/Downloads/9.pic.jpg");
        files.put("10.pic.jpg", "/Users/trdehero/Downloads/10.pic.jpg");

// Create a new OSSClient instance
        OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret);

// Do some operations with the instance...
        for (Bucket bkt : client.listBuckets()) {
            System.out.println(" - " + bkt.getName());
        }

        Iterator<String> iterator = files.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = files.get(key);

            client.putObject(bucketName, key, new File(value));
        }


// Shutdown the instance to release any allocated resources
        client.shutdown();
    }
}
