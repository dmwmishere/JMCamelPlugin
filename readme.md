# Jmeter Camel plugin
use of camel context within jmeter tests

# Features
1. Configure camel routes through configuration element
2. Automatic registry configuration through DI
3. Send samples into direct endpoints

# Installation
1. run build task
2. copy jar into <jmeter_home>/lib/ext

# Usage
See scenario/TestPlugin.jmx for usage examples

You can access to your CamelContext through JSR223Sampler:
```groovy
((org.apache.camel.CamelContext)vars.getObject("jm-camel-context-1"))
.createFluentProducerTemplate()
.to("direct:test")
.withBody("from jsr script")
.send()
```

## Writing custom extensions
Add dependency to your project:
```groovy
compileOnly "org.dmwm.jmeter:CamelPlugin:0.8"
```
write custom beans annotated with @JCBean("name"). Build extension jar and add it to scenario class path.

Use property "bean_class_path" to put your extension package for bean classes scan.
You can also create Config classes - any annotated with JCBean methods with non-void result and with none parameters will be accepted as beans.
Use "name" to refer your beans in route definition.

# TODO:

1. Configuration:
 * JSR223 stuff to camel context

2. Sampler:

3. Listener:
* Mock listener
