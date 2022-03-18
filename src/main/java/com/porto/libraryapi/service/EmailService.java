package com.porto.libraryapi.service;

import java.util.List;

public interface EmailService {

    void sendEmail(String mensagem,List<String> emailList);
}
