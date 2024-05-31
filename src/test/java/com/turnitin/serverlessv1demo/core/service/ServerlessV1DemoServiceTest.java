package com.turnitin.serverlessv1demo.core.service;

import org.junit.jupiter.api.Test;
import com.turnitin.commons.TurnitinContext;

import static org.assertj.core.api.Assertions.assertThat;

class ServerlessV1DemoServiceTest {
	@Test
	void testSayingHello() {
		TurnitinContext context = TurnitinContext.builder().build();
		ServerlessV1DemoService service = new ServerlessV1DemoService(context);
		assertThat(service.doHello("abc")).isEqualTo("Hello abc");
	}
}
