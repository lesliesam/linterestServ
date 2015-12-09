package com.linterest.api;

import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
@At("/") @Service
public class DefaultPage {
    @Get
    public Reply<String> getMessage() {
        return Reply.with("Hello world, from Linterest.");
    }
}
