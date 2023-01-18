# emp-script-spring
## v1.1.5
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