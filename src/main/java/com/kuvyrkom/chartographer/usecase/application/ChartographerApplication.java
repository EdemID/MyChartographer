package com.kuvyrkom.chartographer.usecase.application;

import com.kuvyrkom.chartographer.usecase.service.ChartographerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.kuvyrkom.chartographer.adapter.persistence"})
@EntityScan(basePackages = {"com.kuvyrkom.chartographer.domain.model"})
@ComponentScan(basePackages = {"com"})
public class ChartographerApplication {

	public static String path;

	public static void main(String[] args) throws IOException {
		path = args[0];
		Path imagePath = Path.of(path);
		if (!Files.exists(imagePath))
			Files.createDirectories(imagePath);
		ChartographerService.tmpChartaDirectory = path;
		SpringApplication.run(ChartographerApplication.class, args);
	}

}
