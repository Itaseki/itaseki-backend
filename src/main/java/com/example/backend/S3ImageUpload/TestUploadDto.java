package com.example.backend.S3ImageUpload;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class TestUploadDto {
    private String category;
    private MultipartFile file;
}
