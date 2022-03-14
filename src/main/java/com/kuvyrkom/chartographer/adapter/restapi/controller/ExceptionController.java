package com.kuvyrkom.chartographer.adapter.restapi.controller;

import com.kuvyrkom.chartographer.adapter.restapi.exception.ChartaNotFoundException;
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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response> handleConstraintViolationException(ConstraintViolationException e) {
        Response response = new Response(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RasterFormatException.class)
    public ResponseEntity<Response> handleRasterFormatException(RasterFormatException e) {
        Response response = new Response("Проверь ширину и высоту: " + e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ChartaNotFoundException.class)
    public ResponseEntity<Response> handleEntityNotFoundException(ChartaNotFoundException e) {
        Response response = new Response(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {IIOException.class, IOException.class})
    public ResponseEntity<Response> handleFileNotFoundException(Exception e) {
        Response response = new Response("Проверь наличие файла: " + e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
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
