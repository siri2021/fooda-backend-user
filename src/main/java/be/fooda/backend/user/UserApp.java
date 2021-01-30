package be.fooda.backend.user;

import be.fooda.backend.user.config.RestClientConfig;
import be.fooda.backend.user.config.SwaggerConfig;
import be.fooda.backend.user.config.WebSecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
//@EnableEurekaClient
@EnableSwagger2
@Import({
        RestClientConfig.class,
        SwaggerConfig.class,
        WebSecurityConfig.class
})
public class UserApp {

    public static void main(String[] args) {
        SpringApplication.run(UserApp.class, args);
    }

}
