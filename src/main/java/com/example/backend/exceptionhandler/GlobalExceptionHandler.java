package com.example.backend.exceptionhandler;

import com.example.backend.user.exception.WrongAuthorizationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 모든 컨트롤러에 대해 예외 처리를 해준다.
    // ex) user 객체가 존재하는지 확인: 모든 컨트롤러에서 수행하는 작업
    // user 가 없다는 예외가 UserService 에서 던져진다면? 따로 컨트롤러에서 예외 처리를 하지 않아도, 해당 클래스의 handler 에서 알아서 처리한다
    @ExceptionHandler(WrongAuthorizationException.class)
    ResponseEntity<String> handleWrongAuthorizationException(WrongAuthorizationException exception) {
        return new ResponseEntity<> (exception.getMessage(), HttpStatus.FORBIDDEN);
    }
}
