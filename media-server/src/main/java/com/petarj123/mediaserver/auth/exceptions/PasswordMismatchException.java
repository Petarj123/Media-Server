package com.petarj123.mediaserver.auth.exceptions;

public class PasswordMismatchException extends Exception{
    public PasswordMismatchException(String message){
        super(message);
    }
}
