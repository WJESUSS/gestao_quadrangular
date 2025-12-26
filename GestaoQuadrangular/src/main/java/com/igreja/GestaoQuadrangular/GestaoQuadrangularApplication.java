package com.igreja.GestaoQuadrangular;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GestaoQuadrangularApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestaoQuadrangularApplication.class, args);
	}

}
