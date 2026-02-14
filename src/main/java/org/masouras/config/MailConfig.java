package org.masouras.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Map;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("your.email@gmail.com");
        mailSender.setPassword("your-app-password");

        mailSender.getJavaMailProperties().putAll(Map.of(
                "mail.smtp.auth", "true",
                "mail.smtp.starttls.enable", "true"));

        return mailSender;
    }
}

