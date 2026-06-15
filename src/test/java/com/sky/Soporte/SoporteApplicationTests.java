package com.sky.Soporte;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SoporteApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainclassExists(){
		assertNotNull(SoporteApplication.class);

	}
}
