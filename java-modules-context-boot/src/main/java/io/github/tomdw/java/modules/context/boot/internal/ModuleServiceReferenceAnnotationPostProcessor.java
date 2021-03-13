package io.github.tomdw.java.modules.context.boot.internal;

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import javax.inject.Named;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.serviceloader.AbstractServiceLoaderBasedFactoryBean;
import org.springframework.beans.factory.serviceloader.ServiceFactoryBean;
import org.springframework.beans.factory.serviceloader.ServiceListFactoryBean;
import org.springframework.lang.Nullable;

import io.github.tomdw.java.modules.context.boot.api.ModuleServiceReference;

@Named
public class ModuleServiceReferenceAnnotationPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {
	private static final System.Logger LOGGER = System.getLogger(ModuleServiceReferenceAnnotationPostProcessor.class.getName());

	private ConfigurableBeanFactory beanFactory;

	@Nullable
	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		Module usingModule = beanClass.getModule();
		for (Field field : beanClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(ModuleServiceReference.class)) {
				postProcessModuleServiceReference(usingModule, ModuleServiceReferenceElement.of(field));
			}
		}
		for (Constructor<?> constructor: beanClass.getDeclaredConstructors()) {
			for(Parameter constructorParameter: constructor.getParameters()) {
				if (constructorParameter.isAnnotationPresent(ModuleServiceReference.class)) {
					postProcessModuleServiceReference(usingModule, ModuleServiceReferenceElement.of(constructorParameter));
				}
			}
		}
		return null;
	}

	private void postProcessModuleServiceReference(Module usingModule, ModuleServiceReferenceElement serviceReferenceElement) {
		if(serviceReferenceElement.isServicesList()) {
			registerServiceListFactoryBean(usingModule, serviceReferenceElement);
		} else {
			registerServiceFactoryBean(usingModule, serviceReferenceElement);
		}
	}

	private void registerServiceFactoryBean(Module usingModule, ModuleServiceReferenceElement serviceReferenceElement) {
		Class<?> serviceType = serviceReferenceElement.getModuleServiceType();
		LOGGER.log(INFO, "ModuleServiceReference from module " + usingModule.getName() + " found for service of type " + serviceType);
		validateModuleUsesServiceType(usingModule, serviceType);
		String newBeanName = serviceReferenceElement.getBeanNameToUseForServiceFactory();
		if(!this.beanFactory.containsBean(newBeanName)) {
			registerAbstractServiceFactoryBean(serviceType, newBeanName, new ServiceFactoryBean());
		}
	}

	private void registerServiceListFactoryBean(Module usingModule, ModuleServiceReferenceElement serviceReferenceElement) {
		Class<?> serviceType = serviceReferenceElement.getModuleServiceType();
		LOGGER.log(INFO, "ModuleServiceReference from module " + usingModule.getName() + " found for service of type List of " + serviceType);
		validateModuleUsesServiceType(usingModule, serviceType);
		String newBeanName = serviceReferenceElement.getBeanNameToUseForServiceFactory();
		if (!this.beanFactory.containsBean(newBeanName)) {
			registerAbstractServiceFactoryBean(serviceType, newBeanName, new ServiceListFactoryBean());
		}
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

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = (ConfigurableBeanFactory) beanFactory;
	}
}