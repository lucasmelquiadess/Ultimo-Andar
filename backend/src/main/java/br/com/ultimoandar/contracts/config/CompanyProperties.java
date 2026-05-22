package br.com.ultimoandar.contracts.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.company")
public record CompanyProperties(
        String name,
        String cnpj,
        String address,
        String privacyEmail,
        String defaultCity,
        String defaultForum
) {
}
