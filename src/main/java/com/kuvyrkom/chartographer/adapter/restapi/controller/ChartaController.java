package com.kuvyrkom.chartographer.adapter.restapi.controller;

import com.kuvyrkom.chartographer.usecase.service.ChartographerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
                                                  @RequestParam @Max(50000) @Min(1) int height) throws Exception {
        String id = chartographerService.createNewCharta(width, height);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PostMapping("/{id}/")
    public ResponseEntity<String> saveRestoredFragmentCharta(@PathVariable String id,
                                                             @RequestParam @Min(0) int x,
                                                             @RequestParam @Min(0) int y,
                                                             @RequestParam @Min(1) int width,
                                                             @RequestParam @Min(1) int height,
                                                             @RequestPart(name = "file", required = false) MultipartFile multipartFile
    ) throws Exception {
        chartographerService.saveRestoredFragmentCharta(id, x, y, width, height, multipartFile);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/", produces = "image/bmp")
    public ResponseEntity<byte[]> getRestoredPartOfCharta(@PathVariable String id,
                                                          @RequestParam @Min(0) int x,
                                                          @RequestParam @Min(0) int y,
                                                          @RequestParam @Max(5000) @Min(1) int width,
                                                          @RequestParam @Max(5000) @Min(1) int height
    ) throws Exception {
        byte[] restoredPartOfCharta = chartographerService.getRestoredPartOfCharta(id, x, y, width, height);
        return ResponseEntity.ok().body(restoredPartOfCharta);
    }
}
