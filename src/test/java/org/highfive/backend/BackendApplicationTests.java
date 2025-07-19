package org.highfive.backend;

import org.highfive.backend.config.RabbitMQMockConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Import(RabbitMQMockConfig.class)
@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
