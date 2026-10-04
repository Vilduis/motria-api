package com.motria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MotriaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MotriaApiApplication.class, args);
	}

}
