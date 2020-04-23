# 0.0.4
## Features
- Introduce Spring Boot Starter to enable standard spring boot application complemented with more isolated modules with their own spring context
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