package com.tech2invent.artefact;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ArtefactServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArtefactServiceApplication.class, args);

	}


	//TODO: Refactor Object Mapper to use pretty print
	// add custom ObjectMapper to serialize/deserialize req/res body
	@Bean
	ObjectMapper getObjectMapper(){

		return new ObjectMapper();
	}


}
