package rs.ac.ftn.isa.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableCaching
@EnableScheduling
public class IsaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(IsaBackendApplication.class, args);
	}

}
