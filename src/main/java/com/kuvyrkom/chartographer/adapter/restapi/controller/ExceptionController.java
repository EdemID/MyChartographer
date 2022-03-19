package com.kuvyrkom.chartographer.adapter.restapi.controller;

import com.kuvyrkom.chartographer.adapter.persistence.exception.ChartaNotFoundException;
import com.kuvyrkom.chartographer.usecase.exception.ChartaAlreadyCreatedException;
import com.kuvyrkom.chartographer.usecase.exception.ChartaLockedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.imageio.IIOException;
import javax.validation.ConstraintViolationException;
import java.awt.image.RasterFormatException;
import java.io.IOException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Response> handleConstraintViolationException(RuntimeException e) {
        Response response = new Response(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IIOException.class, IOException.class, ChartaNotFoundException.class, ChartaLockedException.class, ChartaAlreadyCreatedException.class})
    public ResponseEntity<Response> handleException(Exception e) {
        Response response = new Response(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = RasterFormatException.class)
    public ResponseEntity<Response> handleRasterFormatException(RasterFormatException e) {
        Response response = new Response("Проверь размеры и координаты возвращаемого фрагмента " + e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    static class Response {
        private String message;

        public Response() {
        }

        public Response(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
