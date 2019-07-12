package myApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages={"controller"})

public class DryCleanerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DryCleanerApiApplication.class, args);
	}

}
