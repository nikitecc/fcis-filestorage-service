package com.gostech.fcisfilestorageservice.service;

import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service
public interface S3Service {

    String uploadFile(InputStream is, String s3Key);

    byte[] downloadFile(String s3Key);

    String deleteFile(String s3Key);

    List<String> listFiles(String s3Key);
}
