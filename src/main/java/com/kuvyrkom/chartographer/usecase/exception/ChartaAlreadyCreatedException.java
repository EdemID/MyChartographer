package com.kuvyrkom.chartographer.usecase.exception;

public class ChartaAlreadyCreatedException extends RuntimeException {
    public ChartaAlreadyCreatedException(String message) {
        super("Чарта уже создана: id = " + message);
    }
}
