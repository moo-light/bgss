package com.server.blockchainserver.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

//@OpenAPIDefinition(
//        info = @Info(
//                title = "Blockchain Server API",
//                version = "1.0",
//                description = "OPEN API"
//        ),
//        servers = {
//                @Server(url = "http://localhost:8080", description = "Local Development Server"),
//                @Server(url = "http://172.104.61.211:8080", description = "Production Server"),
//        }
//)
//@SecurityScheme(
//        name = "bearer-key",
//        type = SecuritySchemeType.HTTP,
//        scheme = "bearer",
//        bearerFormat = "JWT",
//        in = SecuritySchemeIn.HEADER
//)
@Configuration
public class OpenApiConfig {

    @Value("${blockchain.openapi.dev-url}")
    private String devUrl;

    @Value("${blockchain.openapi.prod-url}")
    private String prodUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("minhpqse140884@fpt.edu.vn");
        contact.setName("Admin");
        contact.setUrl("https://www.facebook.com/pq.minh2309");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Management OPEN API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to manage.").termsOfService("https://choosealicense.com/licenses/mit/")
                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
    }
}
