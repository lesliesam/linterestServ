package com.linterest.error;

import java.text.MessageFormat;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class ServerErrorParamEmpty {

    public String message = "Param {0} is empty.";

    public ServerErrorParamEmpty(String paramName) {
        message = MessageFormat.format(message, paramName);
    }
}
