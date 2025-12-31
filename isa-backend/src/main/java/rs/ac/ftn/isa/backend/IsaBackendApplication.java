package rs.ac.ftn.isa.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class IsaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(IsaBackendApplication.class, args);
	}

}
