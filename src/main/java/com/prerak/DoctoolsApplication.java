package com.prerak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DoctoolsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoctoolsApplication.class, args);
	}

}