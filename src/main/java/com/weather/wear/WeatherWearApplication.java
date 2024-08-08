package com.weather.wear;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.weather.wear.member.repository")
@ComponentScan(basePackages = "com.weather.wear")

public class WeatherWearApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherWearApplication.class, args);
	}

}
