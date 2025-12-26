package com.igreja.GestaoQuadrangular.servicce;

// src/main/java/com/igreja/GestaoQuadrangular/service/EmailService.java

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmail(String para, String assunto, String texto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(para);
        message.setSubject(assunto);
        message.setText(texto);
        message.setFrom("washingtonjesus.santos@ucsal.edu.br");  // mesmo do application.yml

        mailSender.send(message);
    }
    // Método apenas para teste rápido
    public void enviarEmailTeste() {
        enviarEmail(
                "washingtonjesus.santos@ucsal.edu.br",  // ← coloque aqui o seu e-mail pessoal para testar
                "Teste do Sistema GestaoQuadrangular",
                "Olá! Este é um e-mail de teste automático do sistema. Tudo funcionando perfeitamente! 🙌\n\nData: " + LocalDateTime.now()
        );
        System.out.println("E-mail de teste enviado para washingtonjesus.santos@ucsal.edu.br");
    }

}
