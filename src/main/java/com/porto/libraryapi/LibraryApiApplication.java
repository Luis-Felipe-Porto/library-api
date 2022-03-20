package com.porto.libraryapi;

import com.porto.libraryapi.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {
//    @Autowired
//    private EmailService emailService;
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
//    @Bean
//    public CommandLineRunner runner(){
//        return args -> {
//            List<String> mails = Arrays.asList("0832f1e3e3-d1778b@inbox.mailtrap.io");
//            emailService.sendEmail("Testando servico de email",mails);
//            System.out.println("EMAIL ENVIADO");
//        };
//    }
    public static void main(String[] args) {
        SpringApplication.run(LibraryApiApplication.class, args);
    }

}
