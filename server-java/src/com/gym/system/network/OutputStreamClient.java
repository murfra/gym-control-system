package com.gym.system.network;

import com.gym.system.io.AlunoOutputStream;
import com.gym.system.model.Aluno;
import com.gym.system.model.enums.Experiencia;

import java.io.IOException;
import java.net.Socket;

public class OutputStreamClient {
    public static void main(String[] args) {
        try {
            Aluno[] lista = { new Aluno("12345678901", 889090,"Aluno 1", null,
                    "aluno@mail.com", null, Experiencia.INICIANTE)};

            Socket socket = new Socket("localhost", 12345);
            AlunoOutputStream aos = new AlunoOutputStream(lista, 1, 20, socket.getOutputStream());

            System.out.println("Enviando dados via TCP...");
            aos.enviarDados();

            socket.close();
            System.out.println("Envio concluído");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}