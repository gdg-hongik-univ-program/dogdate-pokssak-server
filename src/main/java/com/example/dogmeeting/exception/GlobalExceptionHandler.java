package com.example.dogmeeting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice// 모든 컨트롤러에서 발생하는 예외를 한곳에서 처리
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateNicknameException.class)
    public ResponseEntity<String> handleDuplicateNicknameException(DuplicateNicknameException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT); // 409 Conflict
    }

    @ExceptionHandler(DuplicateUserIdException.class)
    public ResponseEntity<String> handleDuplicateUserIdException(DuplicateUserIdException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT); // 409 Conflict
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<String> handlePasswordMismatchException(PasswordMismatchException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST); // 400 Bad Request
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND); // 404 Not Found
    }

    // @Valid 유효성 검사 실패 시 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<String> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE); // 415 Unsupported Media Type
    }


    // 파라미터 타입 변환 오류 처리 (예: "undefined"를 Long으로 변환할 수 없는 경우)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        String paramValue = String.valueOf(ex.getValue());
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown";
        
        String message = String.format("파라미터 '%s'의 값 '%s'을(를) %s 타입으로 변환할 수 없습니다.", 
                                       paramName, paramValue, requiredType);
        
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST); // 400 Bad Request
    }

    // 그 외 예상치 못한 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        // 실제 운영 환경에서는 로그를 남기고, 사용자에게는 일반적인 오류 메시지를 전달
        return new ResponseEntity<>("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}