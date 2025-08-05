package org.example.kdt_bank_client2;


import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KdtBankServerProject2Application {

	public static void main(String[] args) {
		// SpringBoot 웹서버 비활성화
		System.setProperty("spring.main.web-application-type", "none");
		System.setProperty("javafx.preloader", "");

		Application.launch(ChatClientApp.class, args);
	}
}
