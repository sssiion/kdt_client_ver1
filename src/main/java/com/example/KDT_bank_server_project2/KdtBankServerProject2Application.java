package com.example.KDT_bank_server_project2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
public class KdtBankServerProject2Application {

	public static void main(String[] args) {
		SpringApplication.run(KdtBankServerProject2Application.class, args);
	}

}
