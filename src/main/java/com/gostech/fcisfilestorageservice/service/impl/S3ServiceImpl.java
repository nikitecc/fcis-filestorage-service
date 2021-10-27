package com.gostech.fcisfilestorageservice.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.gostech.fcisfilestorageservice.exception.S3Exception;
import com.gostech.fcisfilestorageservice.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 s3client;

    @Value("${aws.bucket-name}")
    private String bucketName;

    @Override
    public String uploadFile(InputStream is, String s3Key) {
        if (s3client.doesObjectExist(bucketName, s3Key)) {
            throw new S3Exception("Файл с ключом " + s3Key + " уже существует в хранилище S3", HttpStatus.CONFLICT);
        }
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();

            s3client.putObject(bucketName, s3Key, is, objectMetadata);
            log.info("Файл с ключом: " + s3Key + " упешно отправлен в корзину: " + bucketName + " хранилища S3");
        } catch (Exception e) {
            throw new S3Exception(
                    "Ошибка загрузки файла с ключом: " + s3Key + " в корзину: " + bucketName + " хранилища S3",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e);

        }
        return s3Key;
    }

    @Override
    public byte[] downloadFile(String s3Key) {
        S3Object s3object;
        try {
            s3object = s3client.getObject(bucketName, s3Key);
        } catch (AmazonS3Exception e) {
            throw new S3Exception("Файл с ключом " + s3Key + " не найден в хранилище S3", HttpStatus.NOT_FOUND, e);
        }

        S3ObjectInputStream inputStream = s3object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            log.info("Скачан файл: " + s3Key + " из корзины: " + bucketName);
            return content;
        } catch (IOException e) {
            throw new S3Exception("Ошибка записи файла в массив байтов.", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    public String deleteFile(String s3Key) {
        if (!s3client.doesObjectExist(bucketName, s3Key)) {
            throw new S3Exception("Файл с ключом " + s3Key + " не найден в хранилище S3", HttpStatus.NOT_FOUND);
        }
        try {
            s3client.deleteObject(bucketName, s3Key);
            DeleteObjectsRequest delObjReq = new DeleteObjectsRequest(bucketName)
                    .withKeys(s3Key);
            s3client.deleteObjects(delObjReq);
            log.info("Файл: " + s3Key + " удален из корзины: " + bucketName);
        } catch (Exception e) {
            throw new S3Exception(
                    "Ошибка удаления файла " + s3Key + " из корзины: " + bucketName,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e);
        }
        return "Файл: " + s3Key + " удален из корзины: " + bucketName;
    }

    @Override
    public List<String> listFiles(String s3Key) {
        if (!s3client.doesObjectExist(bucketName, s3Key)) {
            throw new S3Exception("Файл с ключом " + s3Key + " не найден в хранилище S3", HttpStatus.NOT_FOUND);
        }
        List<String> list = new LinkedList<>();
        try {
        s3client.listObjects(bucketName, s3Key).getObjectSummaries().forEach(itemResult -> {
            list.add(itemResult.getKey());
            System.out.println(itemResult.getKey());
        });
        } catch (Exception e) {
            throw new S3Exception(
                    "Ошибка просмотра файлов " + s3Key + " из корзины: " + bucketName,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e);
        }
        return list;
    }
}
