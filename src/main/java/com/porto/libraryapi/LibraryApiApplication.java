package com.porto.libraryapi;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
    @Scheduled(cron = "0 20 0 1/1 * ?")
    public void testeAgendamentoTarefa(){
        System.out.println("Agendamento de tarefas funcionando");
    }
    public static void main(String[] args) {
        SpringApplication.run(LibraryApiApplication.class, args);
    }

}
