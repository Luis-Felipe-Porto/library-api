package com.porto.libraryapi.service.impl;

import com.porto.libraryapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${application.mail.default-remetente}")
    private String remetente;

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String mensagem, List<String> emailList) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        String[] mails = emailList.toArray(new String[emailList.size()]);
        simpleMailMessage.setFrom(remetente);
        simpleMailMessage.setSubject("Livro com emprestimo atrasado");
        simpleMailMessage.setText(mensagem);
        simpleMailMessage.setTo(mails);
        javaMailSender.send(simpleMailMessage);
    }
}
