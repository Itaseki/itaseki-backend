package com.example.backend.S3ImageUpload;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

@RequiredArgsConstructor
@RestController
public class TestImageController {
    private final AwsS3Service s3Service;

    @PostMapping("/test")
    public String uploadFile(UploadVO uploadVO){
        System.out.println("controller called");
        String category=uploadVO.getCategory();
        MultipartFile file= uploadVO.getFile();

        //url.toString 에러 해결 해야함
        URL url = s3Service.uploadFile(category, file);
        System.out.println("url = " + url);
        return "success";
    }

}
