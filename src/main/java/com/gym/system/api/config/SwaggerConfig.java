package com.gym.system.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean 
    public OpenAPI gymSystemOpenAPI()  {
        Server localServer = new Server()
        .url("http://localhost:8080")
        .description("Servidor local de desenvolvimento");
        Contact contact = new Contact()
        .name("Equipe Gym System")
        .email("contato@gym-system.com");
        Info info = new Info()
        .title("Gym System API")
        .version("1.0.0")
        .description("API REST para gerenciamento de alunos, treinos, exercícios e avaliações de desempenho.")
        .contact(contact)
        .license(new License()
        .name("Uso acadêmico")
        .url("https://example.com/license"));
        return new OpenAPI()
        .info(info)
        .servers(List.of(localServer));
    }
}
