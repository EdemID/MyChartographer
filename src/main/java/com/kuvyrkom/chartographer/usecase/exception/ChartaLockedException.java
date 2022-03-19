package com.kuvyrkom.chartographer.usecase.exception;

public class ChartaLockedException extends RuntimeException {
    public ChartaLockedException(String id) {
        super("Чарта с id " + id + " занята. Подождите");
    }
}
