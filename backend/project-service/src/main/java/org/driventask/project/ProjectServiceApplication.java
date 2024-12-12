package org.driventask.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

@SpringBootApplication
@EnableFeignClients
@EnableR2dbcAuditing
public class ProjectServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProjectServiceApplication.class, args);
	}

}
