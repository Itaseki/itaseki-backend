package com.example.backend.exceptionhandler;

import com.example.backend.user.exception.NoSuchUserException;
import com.example.backend.user.exception.WrongAuthorizationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 모든 컨트롤러에 대해 예외 처리를 해준다.
    // ex) 요청을 보낸 사용자가 해당 글에 대한 삭제 및 수정 권한이 있는지 확인하는 작업: 모든 컨트롤러에서 필요
    // 권한이 없다는 예외가 UserService 에서 던져진다면? 따로 컨트롤러에서 예외 처리를 하지 않아도, 해당 클래스의 handler 에서 알아서 처리한다
    @ExceptionHandler(WrongAuthorizationException.class)
    ResponseEntity<String> handleWrongAuthorizationException(WrongAuthorizationException exception) {
        return new ResponseEntity<> (exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NoSuchUserException.class)
    ResponseEntity<String> handleNoSuchUserException(NoSuchUserException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}
