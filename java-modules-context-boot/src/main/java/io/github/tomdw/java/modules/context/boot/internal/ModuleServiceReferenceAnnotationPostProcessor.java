package io.github.tomdw.java.modules.context.boot.internal;

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

import javax.inject.Named;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.serviceloader.AbstractServiceLoaderBasedFactoryBean;
import org.springframework.beans.factory.serviceloader.ServiceFactoryBean;
import org.springframework.beans.factory.serviceloader.ServiceListFactoryBean;
import org.springframework.lang.Nullable;

import io.github.tomdw.java.modules.context.boot.api.ModuleServiceReference;

@Named
public class ModuleServiceReferenceAnnotationPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements BeanFactoryAware {
	private static final System.Logger LOGGER = System.getLogger(ModuleServiceReferenceAnnotationPostProcessor.class.getName());

	private ConfigurableBeanFactory beanFactory;

	@Nullable
	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		Module usingModule = beanClass.getModule();
		for (Field field : beanClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(ModuleServiceReference.class)) {
				Class<?> injectionType = field.getType();
				if(injectionType.isAssignableFrom(List.class)) {
					registerServiceListFactoryBean(usingModule, field);
				} else {
					registerServiceFactoryBean(usingModule, field);
				}
			}
		}
		return null;
	}

	private void registerServiceFactoryBean(Module usingModule, Field field) {
		Class<?> serviceType = field.getType();
		LOGGER.log(INFO, "ModuleServiceReference from module " + usingModule.getName() + " found for service of type " + serviceType);
		String newBeanName = serviceFactoryBeanName(serviceType);
		validateModuleUsesServiceType(usingModule, serviceType);
		if(!this.beanFactory.containsBean(newBeanName)) {
			registerAbstractServiceFactoryBean(serviceType, newBeanName, new ServiceFactoryBean());
		}
	}

	private void registerServiceListFactoryBean(Module usingModule, Field field) {
		Class<?> serviceType = extractTypeOfField(field);
		LOGGER.log(INFO, "ModuleServiceReference from module " + usingModule.getName() + " found for service of type List of " + serviceType);
		validateModuleUsesServiceType(usingModule, serviceType);
		validateNamedOrQualifierAnnotationPresent(field, serviceType);
		String newBeanName = serviceListFactoryBeanName(serviceType);
		if (!this.beanFactory.containsBean(newBeanName)) {
			registerAbstractServiceFactoryBean(serviceType, newBeanName, new ServiceListFactoryBean());
		}
	}

	private Class<?> extractTypeOfField(Field field) {
		ParameterizedType listGenericType = (ParameterizedType) field.getGenericType();
		if (checkIfContentOfGenericListHasGenerics(listGenericType)) {
			return (Class<?>) ((ParameterizedType) listGenericType.getActualTypeArguments()[0]).getRawType();
		} else {
			return (Class<?>) listGenericType.getActualTypeArguments()[0];
		}
	}

	private boolean checkIfContentOfGenericListHasGenerics(ParameterizedType listGenericType) {
		return listGenericType.getActualTypeArguments()[0] instanceof ParameterizedType;
	}

	private String serviceFactoryBeanName(Class<?> serviceType) {
		return decapitalizeFirstLetter(serviceType.getSimpleName());
	}

	private String serviceListFactoryBeanName(Class<?> serviceType) {
		return decapitalizeFirstLetter(serviceType.getSimpleName() + "List");
	}

	private String decapitalizeFirstLetter(String input) {
		return Character.toLowerCase(input.charAt(0)) + input.substring(1);
	}

	private void registerAbstractServiceFactoryBean(Class<?> serviceType, String newBeanName, AbstractServiceLoaderBasedFactoryBean serviceFactoryBean) {
		serviceFactoryBean.setServiceType(serviceType);
		try {
			serviceFactoryBean.afterPropertiesSet();
		} catch (Exception e) {
			throw new BeanCreationException(newBeanName, e.getMessage(), e);
		}
		this.beanFactory.registerSingleton(newBeanName, serviceFactoryBean);
	}

	private void validateModuleUsesServiceType(Module usingModule, Class<?> serviceType) {
		if (!usingModule.canUse(serviceType)) {
			LOGGER.log(ERROR, "Module with name " + usingModule.getName() + " does not define a 'use' dependency on service of type: use " + serviceType.getName());
			throw new IllegalStateException("Module with name " + usingModule.getName() + " does not define a 'use' dependency on service of type: use " + serviceType.getName());
		}
	}

	private void validateNamedOrQualifierAnnotationPresent(Field field, Class<?> serviceType) {
		if(getNamedOrQualifierValue(field).isEmpty() || !getNamedOrQualifierValue(field).orElseThrow().equals(serviceListFactoryBeanName(serviceType))) {
			LOGGER.log(ERROR, "Injecting a list of " + serviceType.getName() + " requires a Qualifier or Named annotation with value " + serviceListFactoryBeanName(serviceType));
			throw new IllegalStateException("Injecting a list of " + serviceType.getName() + " requires a Qualifier or Named annotation with value " + serviceListFactoryBeanName(serviceType));
		}
	}

	private Optional<String> getNamedOrQualifierValue(Field field) {
		if(field.isAnnotationPresent(Named.class)) {
			return Optional.of(field.getAnnotation(Named.class).value());
		}
		if(field.isAnnotationPresent(Qualifier.class)) {
			return Optional.of(field.getAnnotation(Qualifier.class).value());
		}
		return Optional.empty();
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = (ConfigurableBeanFactory) beanFactory;
	}
}