# java-modules-context-boot
Simple framework to boot a spring context within each java module

1. [Requirements](#requirements)
2. [Maven Dependency](#maven-dependency)
3. [Define a spring context within a module](#define-a-spring-context-within-a-module)
4. [Starting the application](#starting-the-application)
5. [Integrating modules using services](#integrating-modules-using-services)
6. [Samples](#samples)

The [Boot Spring Context within each Java 11 Module](https://devcreativity.wordpress.com/2017/11/18/boot-spring-context-within-each-java-9-module/) blog post describes the approach in detail. 

## Requirements

- uses Spring as automatic modules or spring boot with the starter
- build and run using jdk11 on the modulepath
- Use jdk 11.0.11 or higher because of bug in jdk in earlier versions https://bugs.openjdk.java.net/browse/JDK-8241770.

## Maven Dependency

The dependency to use is

```xml
<dependency>
	<groupId>io.github.tomdw.java.modules.spring</groupId>
	<artifactId>java-modules-context-boot</artifactId>
	<version>0.0.4</version>
</dependency>
```

which provides you with a java module named 

`io.github.tomdw.java.modules.context.boot`

transitively providing read access to spring modules and jakarta's java.inject (still in javax.inject package).

## Define a spring context within a module

To define a spring context within a java module, you need to:
 - use the ModuleContext annotation on the module pointing to a java-based spring configuration
 - require io.github.tomdw.java.modules.context.boot
 - open the package of the module to spring
 
For example:

```
@ModuleContext(
	mainConfigurationClass = SpeakerConfiguration.class
)
module io.github.tomdw.java.modules.spring.samples.basicapplication.speaker {
	requires io.github.tomdw.java.modules.context.boot;
	opens io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.internal to spring.beans, spring.core, spring.context;
}
```

java-modules-context-boot will make sure to start a separate spring context for every module annotated with this annotation.

If you would like to use another `ApplicationContext` than the default `AnnotationConfigApplicationContext` you can specify the class using the `applicationContextClass` parameter of the `@ModuleContext` annotation. This class should extend `GenericApplicationContext` and implement the `AnnotationConfigRegistry` interface.

This could be useful when you want to use a Spring Boot specific ApplicationContext that contains starters. For example an application context supporting web servlets such as `org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext`. 

## Starting the application

### Using the built-in main class

You can use the ModuleContextBooter.main as main method to start the application.

Make sure to add the application modules to your module path. For every module on the module path annotated with @ModuleContext
we boot a spring context.

### Using your own (main) classes

When you want to control when the modules get booted with spring contexts you can call

```
ModuleContextBooter.boot();
```
from anywhere in your project's code.

### Using the Spring Boot Starter

The dependency to use is

```xml
<dependency>
	<groupId>io.github.tomdw.java.modules.spring</groupId>
	<artifactId>java-modules-context-boot-starter</artifactId>
	<version>0.0.4</version>
</dependency>
```

which provides you with a java module named 

`be.aca.platform.java.modules.context.boot.starter`

and which is a standard Spring Boot starter that automatically triggers `ModuleContextBooter.boot(springBootApplicationContext)`.
The framework uses the given `springBootApplicationContext` as default application context when retrieving beans for a Module.

This alternative enables applications that are standard spring boot applications running on the modulepath to easily integrate other modules which define their own ModuleContext for better isolation.


## Integrating modules using services

Using services through the ServiceLoader API allows to integrate modules in a loosely coupled way. The 
following sections describe how to do this between multiple spring contexts in different modules.

### Provide a module service from the spring context

To provide a service through the ServiceLoader API you need to add the 'provides' in your module-info:
```
@ModuleContext(...)
module io.github.tomdw.java.modules.spring.samples.basicapplication.speaker {
	...
	provides SpeakerService with DefaultSpeakerService;
	...
}
```

Instead of using a default contructor, leverage the 'provider' factory method alternative as follows:
```
@Named
public class DefaultSpeakerService implements SpeakerService {
	...
	public static SpeakerService provider() {
		return ModuleServiceProvider.provide(SpeakerService.class);
	}
	...
}

```

Best practice is to provide the service with the interface class.
This way a dynamic proxy is created for consumers. It prevents exceptions while providing instances of beans while certain springContexts are still inactive.
The actual bean lookup will be performed when this service is invoked.

If you have 2 services of the same type (for instance a datasource), you can provide the proper service using the bean name as follows:
```
public class SpeakerServiceProvider {

	public static SpeakerService provider() {
		return ModuleServiceProvider.provide(SpeakerService.class, "speaker-service-bean-name");
	}

	private SpeakerServiceProvider() {
		//must not be constructed
	}
}
```

The ModuleServiceProvider.provide helper method retrieves a bean of the given type from 
the spring application context associated with the module calling the provide method.

### Reference a module service from another spring context

Using a service in another module can simply be done by annotating with @ModuleServiceReference:
```
@Named
public class MessageGenerator {

	@ModuleServiceReference
	private SpeakerService speakerService;

}

```

Don't forget to add a 'uses' entry in the module-info:
```
@ModuleContext(...)
module io.github.tomdw.java.modules.spring.samples.basicapplication.application {
	...
	uses SpeakerService;
}
```

Every spring context is enriched with a processor to retrieve the necessary services 
through the ServiceLoader API and make them available for injection in the spring context of that module.

### Reference a list of module services from another spring context

Similar to using a single service, the annotation @ModuleServiceReference can be used to inject a list of all services that implement a certain interface.
```
@Named
public class MessageGenerator {

	@ModuleServiceReference
	@Named("speakerServiceList")
	private List<SpeakerService> speakerService;

}

```

The difference is that we cannot inject by type, because of generics limitations a list of speakerServices are not known by spring to inject.
To support this, we are registring the list of provided services with a bean name. By convention this name will be the type of the services suffixed by "List".
When injecting the service list you should use the @Named or @Qualifier annotation to specify the name following this naming convention.

### Sharing Spring Environment
When a default application context is provided, it's Environment will be shared with all other contexts. This allows all contexts to share configuration, including configuration set in tests.  

### Using in combination with @DirtiesContext
When using this framework in combination wtih DirtiesContext, the registry should be reset whenever the spring context is refreshed. Call `ModuleContextBooter.reset()` whenever the context is refreshed (e.g. in @AfterAll when DirtiesContext runs after each class).

## Samples
- Under 'samples' the 'basicapplication' sample shows this in a working hello world application.
	- follow the instructions regarding the java toolchains from the [java-modules-parent project](https://github.com/tomdw/java-modules-parent)
	- start the application using 'mvn toolchains:toolchain exec:exec' from within the 'basicapplication/application' module.
  