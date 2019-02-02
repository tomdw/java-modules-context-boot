module be.tomdw.java.modules.context.boot {
	exports be.tomdw.java.modules.context.boot.api;
	requires transitive spring.context;
	requires transitive spring.beans;
	requires transitive spring.core;
	requires transitive javax.inject;

	opens be.tomdw.java.modules.context.boot.internal to spring.beans, spring.core, spring.context;
}