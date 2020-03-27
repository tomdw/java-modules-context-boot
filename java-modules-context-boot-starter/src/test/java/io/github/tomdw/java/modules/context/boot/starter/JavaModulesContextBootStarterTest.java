package io.github.tomdw.java.modules.context.boot.starter;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import io.github.tomdw.java.modules.context.boot.starter.test.app.SpringBootTestApplication;


@SpringBootTest(classes = SpringBootTestApplication.class)
public class JavaModulesContextBootStarterTest {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void test() {
		assertThat(applicationContext).isNotNull();
	}
}
