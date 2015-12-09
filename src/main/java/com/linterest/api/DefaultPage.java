package com.linterest.api;

import com.google.sitebricks.At;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
@At("/")
public class DefaultPage {
    private boolean appear = true;
    public String getMessage() { return "Hello,"; }
}
