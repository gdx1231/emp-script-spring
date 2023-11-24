# emp-script-spring


## Official web
https://www.gdxsoft.com 

## Github
https://github.com/gdx1231/emp-script-spring


## Main

```java
@SpringBootApplication
@ComponentScan(basePackages = { 
		  "com.gdxsoft.easyweb.spring.controllers" /* ewa and define */
		, "com.gdxsoft.easyweb.spring.staticResources.controllers" /* ewa static resources ,css js images ... */
		, "com.gdxsoft.easyweb.spring.restful.controllers" /* RESTful  */
		, "com.gdxsoft.easyweb.spring.staticResources.controllers" /* RESTful cloud */
		, "com.gdxsoft.yourApp.controllers" })
public class YourAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(YourAppApplication.class, args);
	}
)
```

## maven
```
<dependency>
  <groupId>com.gdxsoft.easyweb</groupId>
  <artifactId>emp-script-utils</artifactId>
  <version>1.1.8</version>
</dependency>
```