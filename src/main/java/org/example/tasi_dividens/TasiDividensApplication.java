package org.example.tasi_dividens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TasiDividensApplication {

	public static void main(String[] args) {
		SpringApplication.run(TasiDividensApplication.class, args);
	}

}
