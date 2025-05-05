package com.neoinvo.invoice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class NeoinvoApplication {

	@GetMapping("/welcome")
	public String welcome() {
		return "Welcome to billing app";
	}

	public static void main(String[] args) {
		SpringApplication.run(NeoinvoApplication.class, args);
	}

}
