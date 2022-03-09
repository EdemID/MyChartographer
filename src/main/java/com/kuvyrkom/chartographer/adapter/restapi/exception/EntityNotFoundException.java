package com.kuvyrkom.chartographer.adapter.restapi.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String filePath) {
        super(filePath);
    }
}
