package com.motria;

import org.springframework.boot.SpringApplication;

public class TestMotriaApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(MotriaApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
