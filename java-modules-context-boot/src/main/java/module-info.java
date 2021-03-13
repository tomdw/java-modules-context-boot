module io.github.tomdw.java.modules.context.boot {
	exports io.github.tomdw.java.modules.context.boot.api;
	requires transitive spring.context;
	requires transitive spring.beans;
	requires transitive spring.core;
	requires transitive java.inject;

	opens io.github.tomdw.java.modules.context.boot.internal to spring.beans, spring.core, spring.context;
}