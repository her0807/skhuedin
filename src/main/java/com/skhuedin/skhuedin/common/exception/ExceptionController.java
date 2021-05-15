package com.skhuedin.skhuedin.common.exception;

import com.skhuedin.skhuedin.controller.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionController {

    // 400
    @ExceptionHandler({InvalidDataAccessApiUsageException.class})
    public ResponseEntity<ErrorResponse> BadRequestException(RuntimeException e) {
        log.warn("error", e);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("잘못된 정렬 조건입니다.", "400"));
    }

    // 401
    @ExceptionHandler({EmptyTokenException.class})
    public ResponseEntity<ErrorResponse> EmptyTokenException(RuntimeException e) {
        log.warn("error", e);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage(), "401"));
    }

    // 404
    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> NotFoundException(RuntimeException e) {
        log.warn("error", e);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    // 500
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleAll(Exception e) {
        log.info(e.getClass().getName());
        log.error("error", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage(), "500"));
    }
}