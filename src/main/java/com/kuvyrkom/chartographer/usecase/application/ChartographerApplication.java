package com.kuvyrkom.chartographer.usecase.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.kuvyrkom.chartographer.adapter.persistence"})
@EntityScan(basePackages = {"com.kuvyrkom.chartographer.domain.model"})
@ComponentScan(basePackages = {"com"})
public class ChartographerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChartographerApplication.class, args);
	}

}
