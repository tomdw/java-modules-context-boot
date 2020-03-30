open module io.github.tomdw.java.modules.spring.integration.tests.spring.boot {
	requires spring.boot.autoconfigure;
	requires spring.context;

	requires io.github.tomdw.java.modules.context.boot.starter;
	requires io.github.tomdw.java.modules.context.boot;

	requires io.github.tomdw.java.modules.spring.samples.basicapplication.application;
}