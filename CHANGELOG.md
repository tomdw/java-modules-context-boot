# 0.0.4
## Features
- Introduce Spring Boot Starter to enable standard spring boot application complemented with more isolated modules with their own spring context
- Make Spring Boot starter act as an ApplicationContextInitializer to get Module Contexts booted as early as possible. 
- Introduce default application context to retrieve beans from when the Module has no @ModuleContext.
- Spring Boot starter sets the main spring boot context as default application context (e.g. enables using ModuleServiceProvider from spring boot context modules).
- Set Application Context Id to include the java module name. Allows usefull visualisation in e.g. IntelliJ spring support.
- Also support @ModuleServiceReference in the default application context (i.e. which is the main spring boot application context when used with the spring boot starter)
- Support using @ModuleServiceReference on constructor parameters for constructor injection of services loaded from other module contexts
- The Environment of the default spring context is now shared with all other context. This allows other contexts to also read spring properties.
- An exception is thrown when trying to load a bean from the default application context before it is active

## Bug fixes
- Some logging statements logged the wrong Module name for which services where retrieved
- Make sure registering a new application context for the same module is not possible
- Make sure triggering ModuleContextBooter.boot multiple times does not register application contexts multiple times
- Make sure using ModuleServiceProvider.provide is possible before an explicit ModuleContextBooter.boot without multiple application contexts as result
- Get rid of the INFO log statement for the ModuleServiceReferenceAnnotationPostProcessorConfiguration not being eligible for post processing

# 0.0.3
- Implement workaround for JDK bug https://bugs.openjdk.java.net/browse/JDK-8241770
- Upgrade spring version
- Upgrade test dependencies
- Centralise dependency management in poms
- Use java-modules-parent 0.0.3

# 0.0.2
- Upgrade spring version
- Support for generics in an object inside a list
- less logging by logging retrieval of beans on debug level
- add ability to selectively provide a bean or service based on the bean name when multiple beans of the same type are present in an application context

# 0.0.1 
- Initial Version