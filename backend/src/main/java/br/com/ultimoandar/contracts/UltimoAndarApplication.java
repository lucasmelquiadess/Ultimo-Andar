package br.com.ultimoandar.contracts;

import br.com.ultimoandar.contracts.config.CompanyProperties;
import br.com.ultimoandar.contracts.config.CorsProperties;
import br.com.ultimoandar.contracts.config.CryptoProperties;
import br.com.ultimoandar.contracts.config.RetentionProperties;
import br.com.ultimoandar.contracts.config.SecurityProperties;
import br.com.ultimoandar.contracts.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({
        StorageProperties.class,
        SecurityProperties.class,
        CorsProperties.class,
        CompanyProperties.class,
        CryptoProperties.class,
        RetentionProperties.class
})
public class UltimoAndarApplication {

    public static void main(String[] args) {
        SpringApplication.run(UltimoAndarApplication.class, args);
    }
}
