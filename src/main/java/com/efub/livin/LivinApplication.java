package com.efub.livin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LivinApplication {

	public static void main(String[] args) {
		SpringApplication.run(LivinApplication.class, args);
	}

}
