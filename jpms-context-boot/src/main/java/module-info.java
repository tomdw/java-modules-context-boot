module be.tomdewolf.jpms.context.boot {
	exports be.tomdewolf.jpms.context.boot.api;
	requires transitive spring.context;
	requires transitive spring.beans;
	requires transitive spring.core;
	requires transitive javax.inject;

	opens be.tomdewolf.jpms.context.boot.internal to spring.beans, spring.core, spring.context;
}