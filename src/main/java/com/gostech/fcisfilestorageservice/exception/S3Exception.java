package com.gostech.fcisfilestorageservice.exception;

import org.springframework.http.HttpStatus;

public class S3Exception extends RuntimeException {

    private HttpStatus httpStatus;

    public HttpStatus getHttpStatus() {
        if (httpStatus == null) {
            return HttpStatus.BAD_REQUEST;
        }
        return httpStatus;
    }

    public S3Exception(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public S3Exception(String message, HttpStatus httpStatus, Throwable e) {
        super(message, e);
        this.httpStatus = httpStatus;
    }
}