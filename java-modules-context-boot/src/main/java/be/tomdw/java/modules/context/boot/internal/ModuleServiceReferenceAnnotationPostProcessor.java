package be.tomdw.java.modules.context.boot.internal;

import java.lang.reflect.Field;

import javax.inject.Named;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.serviceloader.ServiceFactoryBean;
import org.springframework.lang.Nullable;

import be.tomdw.java.modules.context.boot.api.ModuleServiceReference;

@Named
public class ModuleServiceReferenceAnnotationPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements BeanFactoryAware {

	private ConfigurableBeanFactory beanFactory;

	@Nullable
	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		Module usingModule = beanClass.getModule();
		for (Field field : beanClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(ModuleServiceReference.class)) {
				Class<?> serviceType = field.getType();
				System.out.println("ModuleServiceReference from module " + usingModule.getName() + " resolved for service of type " + serviceType);
				if (!usingModule.canUse(serviceType)) {
					throw new IllegalStateException("Module with name " + usingModule.getName() + " does not define a 'use' dependency on service of type " + serviceType.getName());
				}
				ServiceFactoryBean serviceFactoryBean = new ServiceFactoryBean();
				serviceFactoryBean.setServiceType(serviceType);
				try {
					serviceFactoryBean.afterPropertiesSet();
				} catch (Exception e) {
					throw new BeanCreationException(beanName, e.getMessage(), e);
				}
				this.beanFactory.registerSingleton(serviceType.getSimpleName(), serviceFactoryBean);
			}
		}
		return null;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = (ConfigurableBeanFactory) beanFactory;
	}
}
