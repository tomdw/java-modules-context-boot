package io.github.tomdw.java.modules.context.boot.internal;

import static java.lang.System.Logger.Level.ERROR;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Qualifier;

import io.github.tomdw.java.modules.context.boot.api.ModuleServiceReference;

public class ModuleServiceReferenceElement {

	private static final System.Logger LOGGER = System.getLogger(ModuleServiceReferenceElement.class.getName());

	private final Class<?> declaredType;
	private final Class<?> moduleServiceType;

	public static ModuleServiceReferenceElement of(Field field) {
		return new ModuleServiceReferenceElement(field, field.getType(), field.getGenericType());
	}

	public static ModuleServiceReferenceElement of(Parameter parameter) {
		return new ModuleServiceReferenceElement(parameter, parameter.getType(), parameter.getParameterizedType());
	}

	private ModuleServiceReferenceElement(AnnotatedElement annotatedElement, Class<?> declaredType, Type genericTypeForServicesList) {
		validateModuleServiceReferenceAnnotationIsPresent(annotatedElement);
		this.declaredType = declaredType;
		this.moduleServiceType = determineModuleServiceType(declaredType, genericTypeForServicesList);
		if (isServicesList()) {
			validateListIsReferencedUsingAName(annotatedElement);
		}
	}

	private void validateModuleServiceReferenceAnnotationIsPresent(AnnotatedElement annotatedElement) {
		if (!annotatedElement.isAnnotationPresent(ModuleServiceReference.class)) {
			throw new IllegalArgumentException(ModuleServiceReferenceElement.class.getSimpleName() + " needs to be annotated with annotation " + ModuleServiceReference.class.getSimpleName());
		}
	}

	private void validateListIsReferencedUsingAName(AnnotatedElement annotatedElement) {
		if(!this.isReferencedUsingName(annotatedElement)) {
			LOGGER.log(ERROR, "Injecting a list of " + moduleServiceType.getName() + " requires a Qualifier or Named annotation with value " + getBeanNameToUseForServiceFactory());
			throw new IllegalStateException("Injecting a list of " + moduleServiceType.getName() + " requires a Qualifier or Named annotation with value " + getBeanNameToUseForServiceFactory());
		}
	}

	String getBeanNameToUseForServiceFactory() {
		String name = decapitalizeFirstLetter(moduleServiceType.getSimpleName());
		if (isServicesList()) {
			name += "List";
		}
		return name;
	}

	private String decapitalizeFirstLetter(String input) {
		return Character.toLowerCase(input.charAt(0)) + input.substring(1);
	}

	boolean isServicesList() {
		return declaredType.isAssignableFrom(List.class);
	}

	Class<?> getModuleServiceType() {
		return moduleServiceType;
	}

	private Class<?> determineModuleServiceType(Class<?> declaredType, Type genericTypeForServicesList) {
		if (isServicesList()) {
			ParameterizedType listGenericType = (ParameterizedType) genericTypeForServicesList;
			if (checkIfContentOfGenericListHasGenerics(listGenericType)) {
				return (Class<?>) ((ParameterizedType) listGenericType.getActualTypeArguments()[0]).getRawType();
			} else {
				return (Class<?>) listGenericType.getActualTypeArguments()[0];
			}
		} else {
			return declaredType;
		}
	}

	private boolean checkIfContentOfGenericListHasGenerics(ParameterizedType listGenericType) {
		return listGenericType.getActualTypeArguments()[0] instanceof ParameterizedType;
	}

	private boolean isReferencedUsingName(AnnotatedElement annotatedElement) {
		String referenceName = getBeanNameToUseForServiceFactory();
		if(annotatedElement.isAnnotationPresent(Named.class)) {
			return annotatedElement.getAnnotation(Named.class).value().equals(referenceName);
		}
		if(annotatedElement.isAnnotationPresent(Qualifier.class)) {
			return annotatedElement.getAnnotation(Qualifier.class).value().equals(referenceName);
		}
		return false;
	}
}
