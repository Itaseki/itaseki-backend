package com.example.backend.globalexception;

import com.example.backend.playlist.exception.NoSuchPlaylistException;
import com.example.backend.s3Image.exception.EmptyFileException;
import com.example.backend.s3Image.exception.FileUploadFailException;
import com.example.backend.user.exception.NoSuchUserException;
import com.example.backend.user.exception.WrongAuthorizationException;
import com.example.backend.video.exception.NoSuchCommentException;
import com.example.backend.video.exception.NoSuchVideoException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ExceptionHeader exceptionHeader;

    /*     모든 컨트롤러에 대해 예외 처리를 해준다.
         ex) 요청을 보낸 사용자가 해당 글에 대한 삭제 및 수정 권한이 있는지 확인하는 작업: 모든 컨트롤러에서 필요
         권한이 없다는 예외가 UserService 에서 던져진다면? 따로 컨트롤러에서 예외 처리를 하지 않아도, 해당 클래스의 handler 에서 알아서 처리한다 */
    @ExceptionHandler(WrongAuthorizationException.class)
    ResponseEntity<String> handleWrongAuthorizationException(WrongAuthorizationException exception) {
        return new ResponseEntity<>(exception.getMessage(), exceptionHeader.header, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({NoSuchCommentException.class,
            NoSuchUserException.class,
            NoSuchVideoException.class,
            NoSuchPlaylistException.class})
    ResponseEntity<String> handleNoSuchElementException(RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), exceptionHeader.header, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmptyFileException.class)
    ResponseEntity<String> handleEmptyFileException(EmptyFileException exception) {
        return new ResponseEntity<>(exception.getMessage(), exceptionHeader.header, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileUploadFailException.class)
    ResponseEntity<String> handleFileUploadException(FileUploadFailException exception) {
        return new ResponseEntity<>(exception.getMessage(), exceptionHeader.header, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongParamException.class)
    ResponseEntity<String> handleWrongParamException(WrongParamException exception) {
        return new ResponseEntity<>(exception.getMessage(), exceptionHeader.header, HttpStatus.BAD_REQUEST);
    }
}
