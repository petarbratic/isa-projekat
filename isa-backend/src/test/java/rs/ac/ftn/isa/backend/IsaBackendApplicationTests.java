package rs.ac.ftn.isa.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.jpa.properties.hibernate.cache.use_second_level_cache=false",
		"spring.jpa.properties.hibernate.cache.use_query_cache=false"
})

class IsaBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
