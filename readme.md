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

## Elements
### 1. CamelConfigurationElement
Allows configure Camel context with XML format. Plugin will start context at scenario start and gracefully stop it on scenario end.
Multiple contexts are allowed within one thread use unique name for each of context.

### 2. CamelSampler
Allows to configure and send exchange before directly sand into camel endpoint.
Only "direct" endpoints supported.

Properties:

1. _Context name_ - name of Camel context specified in configuration element
2. _Direct endpoint_ name - name of direct endpoint available within specified context
3. _Converter class_ - class which implements Converter interface to convert string before send into endpoint
4. _Result variable name_ - if not empty, sampler will save result into thread variable
5. Save result as:
* _STRING_ - calls toString() on exchange body then saves into variable
* _OBJECT_ - saves exchange body as is
* _EXCHANGE_ - save whole exchange into thread variable
6. Body to send - string representation of message
7. Exchange headers - header map to put into exchange before send


### Access Context and SampleResults
You can access to your CamelContext through JSR223Sampler:
```groovy
((org.apache.camel.CamelContext)vars.getObject("jm-camel-context-1"))
.createFluentProducerTemplate()
.to("direct:test")
.withBody("from jsr script")
.send()
```

When result saved as Object or Exchange you can access it within JSR223Assertion:
```groovy
vars.getObject("<VARIABLE NAME>")
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
