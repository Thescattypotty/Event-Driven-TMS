package org.driventask.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;


@EnableR2dbcAuditing
@EnableFeignClients
@SpringBootApplication
public class TaskServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskServiceApplication.class, args);
	}

}
