package com.gym.system.network;

import com.gym.system.io.VisitanteOutputStream;
import com.gym.system.model.Experiencia;
import com.gym.system.model.Visitante;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class VisitanteStreamClient {
    public static void main(String[] args) {
        Visitante v1 = new Visitante("123.456.789-01", "Visitante 1", null,
                "(85) 99091-9988", "visitante1@example.com", null, Experiencia.INICIANTE);

        Visitante v2 = new Visitante("100.400.700-02", "Visitante 2", null,
                "(85) 99091-7711", "visitante2@example.com", null, Experiencia.INTERMEDIARIO);

        Visitante[] visitantes = { v1, v2 };
        int qtd = visitantes.length;
        int bytesPorAtributo = 30;

        // TESTE 1: Saída Padrão (System.out)
        try {
            VisitanteOutputStream vosConsole = new VisitanteOutputStream(visitantes, qtd, bytesPorAtributo, System.out);
            vosConsole.writeSystem();
            System.out.println("\n[OK] Visualização no console concluída!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // TESTE 2: Arquivo (FileOutputStream)
        try {
            FileOutputStream fos = new FileOutputStream("visitantes.out");
            VisitanteOutputStream vosFile = new VisitanteOutputStream(visitantes, qtd, bytesPorAtributo, fos);
            vosFile.writeFile();
            System.out.println("\n[OK] Arquivo gerado com sucesso!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // TESTE 3: Servidor TCP (Socket)
        try {
            Socket socket = new Socket("localhost", 12345);
            VisitanteOutputStream vosTCP = new VisitanteOutputStream(visitantes, qtd, bytesPorAtributo, socket.getOutputStream());
            vosTCP.writeTCP();
            System.out.println("\n[OK] Dados enviados ao servidor!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
