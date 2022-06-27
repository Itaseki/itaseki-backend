package com.example.backend.reservation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code= HttpStatus.BAD_REQUEST, reason="잘못된 날짜 포맷")
public class WrongDateFormatException extends RuntimeException{
    private static final String msg="잘못된 날짜 포맷입니다.";
    public WrongDateFormatException(){
        super(msg);
    }
}
