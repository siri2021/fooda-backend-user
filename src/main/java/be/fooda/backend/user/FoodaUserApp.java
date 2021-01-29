package be.fooda.backend.user;

import be.fooda.backend.user.config.RestClientConfig;
import be.fooda.backend.user.config.SwaggerConfig;
import be.fooda.backend.user.config.WebSecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableEurekaClient
@EnableSwagger2
@Import({
        RestClientConfig.class,
        SwaggerConfig.class,
        WebSecurityConfig.class
})
public class FoodaUserApp {

    public static void main(String[] args) {
        SpringApplication.run(FoodaUserApp.class, args);
    }

}
