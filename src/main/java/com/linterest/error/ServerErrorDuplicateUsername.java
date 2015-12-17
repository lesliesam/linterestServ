package com.linterest.error;

import java.text.MessageFormat;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class ServerErrorDuplicateUsername {

    public String message = "{0} already exists.";

    public ServerErrorDuplicateUsername(String name) {
        message = MessageFormat.format(message, name);
    }
}
