package com.coffeeMachine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CoffeeMachineApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoffeeMachineApplication.class, args);
	}

}
