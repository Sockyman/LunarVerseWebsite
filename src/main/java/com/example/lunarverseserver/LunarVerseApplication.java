package com.example.lunarverseserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
public class LunarVerseApplication {
	public static void main(String[] args) {
		SpringApplication.run(LunarVerseApplication.class, args);
	}

	public void run(String[] args) {
		System.out.println("run");
	}

	@GetMapping("/hello")
	public String hello() {
		System.out.println("/hello accessed");
		return "asdf";
	}
}
