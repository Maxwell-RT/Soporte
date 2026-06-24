package com.sky.Soporte;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SoporteApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainclassExists(){
		assertNotNull(SoporteApplication.class);

	}
}
