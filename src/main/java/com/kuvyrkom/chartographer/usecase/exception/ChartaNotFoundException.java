package com.kuvyrkom.chartographer.usecase.exception;

public class ChartaNotFoundException extends RuntimeException {
    public ChartaNotFoundException(String fileUUID) {
        super("Харта с UUID " + fileUUID + " не найдена");
    }
}
