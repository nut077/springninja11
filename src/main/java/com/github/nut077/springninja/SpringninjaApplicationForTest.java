package com.github.nut077.springninja;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Log4j2
@SpringBootApplication
@RequiredArgsConstructor
@ConfigurationPropertiesScan
public class SpringninjaApplicationForTest {

	public static void main(String[] args) {
		SpringApplication.run(SpringninjaApplicationForTest.class, args);
	}
}
