package io.github.tomdw.java.modules.context.boot.internal;

public class InactiveDefaultApplicationContextException extends IllegalStateException {

	public <SERVICETYPE> InactiveDefaultApplicationContextException(Module module, Class<SERVICETYPE> serviceClass) {
		super(String.format("Unable to provide instance of %s from module %s that is part of the default application context which is still inactive. Beans cannot be loaded from the default application context before it is active. Lazy load services or retry loading after startup.",
				serviceClass.getSimpleName(), module.getName()));
	}
}

