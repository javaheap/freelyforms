package com.utbm.da50.freelyform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class FreelyformApplication {

	public static void main(String[] args) {
		SpringApplication.run(FreelyformApplication.class, args);
	}

}
