package com.linterest.error;

import java.text.MessageFormat;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class ServerErrorParamInvalid {
    public String message = "Param {0} invalid. Please see the example.";
    public String example = "ex: {0}";

    public ServerErrorParamInvalid(String message, String example) {
        this.message = MessageFormat.format(this.message, message);
        this.example = MessageFormat.format(this.example, example);
    }
}
