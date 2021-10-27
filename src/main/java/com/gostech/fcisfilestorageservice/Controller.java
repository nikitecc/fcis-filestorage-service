package com.gostech.fcisfilestorageservice;

import com.gostech.fcisfilestorageservice.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class Controller {

    private final S3Service s3Service;

    @PostMapping(value = "upload")
    public String uploadFile(@RequestParam(value = "file") MultipartFile[] files) throws IOException {
        for (MultipartFile file : files) {
            InputStream inputStream = new BufferedInputStream(file.getInputStream());
            s3Service.uploadFile(inputStream, "WaveAccess/" + file.getOriginalFilename());
        }
        return "ok";
    }

    @DeleteMapping("delete")
    public String delete(@RequestParam(value = "key") String key) {
        s3Service.deleteFile(key);
        return "ok";
    }

    @GetMapping("files")
    public List<String> listObjects() {
        return s3Service.listFiles("WaveAccess/");
    }

    @GetMapping(value = "download")
    public byte[] downloadFile(@RequestParam("key") String key) {
        return s3Service.downloadFile(key);
    }
}
