package com.gostech.fcisfilestorageservice.exception;

public class S3Exception extends RuntimeException {

    public S3Exception(String message) {
        super(message);
    }

    public S3Exception(String message, Throwable e) {
        super(message, e);
    }
}