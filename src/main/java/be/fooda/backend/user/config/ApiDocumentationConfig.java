package be.fooda.backend.user.config;

import io.swagger.annotations.*;

@SwaggerDefinition(
        info = @Info(
                description = "Fooda REST API Documentation",
                version = "V1.0.0",
                title = "Fooda Backend User Microservice",
                contact = @Contact(
                        name = "Team Fooda",
                        email = "info@fooda.be",
                        url = "https://www.fooda.be/"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        consumes = {"application/json", "application/xml"},
        produces = {"application/json", "application/xml"},
        schemes = {SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS},
        externalDocs = @ExternalDocs(value = "View for more information.", url = "https://fooda.be/docs")
)

public interface ApiDocumentationConfig {

}