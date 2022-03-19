package com.kuvyrkom.chartographer.usecase.exception;

public class BigChartaAlreadyCreatedException extends RuntimeException {
    public BigChartaAlreadyCreatedException(String message) {
        super("Создание большой чарты возможно один раз. Большая чарта уже создана: id = " + message);
    }
}
