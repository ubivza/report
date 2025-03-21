package com.mobile.network.report.handler;

import com.mobile.network.report.exception.NotFoundException;
import com.mobile.network.report.exception.ServerException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseBody
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
        return ErrorResponse.builder()
            .message(ex.getMessage())
            .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseBody
    public ErrorResponse handleConstraintCheck(ConstraintViolationException ex) {
        return ErrorResponse.builder()
            .message(ex.getMessage())
            .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundException.class})
    @ResponseBody
    public ErrorResponse handleNotFound(NotFoundException ex) {
        return ErrorResponse.builder()
            .message(ex.getMessage())
            .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({ServerException.class})
    @ResponseBody
    public ErrorResponse handleServerException(ServerException ex) {
        return ErrorResponse.builder()
            .message(ex.getMessage())
            .build();
    }
}
