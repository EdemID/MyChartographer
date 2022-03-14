package com.kuvyrkom.chartographer.adapter.restapi.exception;

public class ChartaNotFoundException extends RuntimeException {
    public ChartaNotFoundException(String filePath) {
        super(filePath);
    }
}
