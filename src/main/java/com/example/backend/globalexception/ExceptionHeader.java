package com.example.backend.globalexception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHeader {
    public HttpHeaders header;
    public ExceptionHeader() {
        header = new HttpHeaders();
        header.setContentType(MediaType.valueOf("text/html; charset=UTF-8"));
    }
}
