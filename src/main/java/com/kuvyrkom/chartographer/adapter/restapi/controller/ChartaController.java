package com.kuvyrkom.chartographer.adapter.restapi.controller;

import com.kuvyrkom.chartographer.usecase.service.ChartographerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


@RestController
@RequestMapping("/chartas")
@Validated
public class ChartaController {

    private ChartographerService chartographerService;

    public ChartaController(ChartographerService chartographerService) {
        this.chartographerService = chartographerService;
    }

    @PostMapping
    public ResponseEntity<String> createNewCharta(@RequestParam @Max(20000) @Min(1) int width,
                                                  @RequestParam @Max(50000) @Min(1) int height) {
        String id = chartographerService.createNewCharta(width, height);

        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }
}
