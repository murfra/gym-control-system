package com.gym.system.network;

import com.gym.system.io.InstrutorOutputStream;
import com.gym.system.model.Aluno;
import com.gym.system.model.enums.Experiencia;
import com.gym.system.model.Instrutor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class InstrutorStreamClient {
    public static void main(String[] args) {
        List<Aluno> alunos = new ArrayList<>();
        Aluno a1 = new Aluno("000.000.000-01", "Aluno 1", null, "(88) 99999-0000", "alu1@example.com", null);
        Aluno a2 = new Aluno("000.000.000-11", "Aluno 2", null, "(88) 91234-0000", "alu2@example.com", null, Experiencia.INICIANTE);
        alunos.add(a1);
        alunos.add(a2);

        Instrutor i1 = new Instrutor("123.456.789-01", "012345-G/CE", "Instrutor 1", null,
                "(85) 90900-1234", "email@example.com", null, null, 0, null);
        i1.setAlunos(alunos);

        Instrutor i2 = new Instrutor("000.111.222-01", "000001-P/SP", "Instrutor 2", null,
                "(11) 97070-1234", "email2@example.com", null, null, 0, null);

        Instrutor[] instrutores = {i1, i2};
        int qtd = instrutores.length;
        int bytesPorAtributo = 30;

        // TESTE 1: Saída Padrão (System.out)
        try {
            InstrutorOutputStream iosConsole = new InstrutorOutputStream(instrutores, qtd, bytesPorAtributo, System.out);
            iosConsole.writeSystem();
            System.out.println("\n[OK] Visualização no console concluída!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // TESTE 2: Arquivo (FileOutputStream)
        try {
            FileOutputStream fos = new FileOutputStream("instrutores.json");
            InstrutorOutputStream iosFile = new InstrutorOutputStream(instrutores, qtd, bytesPorAtributo, fos);
            iosFile.writeFile();
            System.out.println("\n[OK] Arquivo gerado com sucesso!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // TESTE 3: Servidor TCP (Socket)
        try {
            Socket socket = new Socket("localhost", 12345);
            InstrutorOutputStream iosTCP = new InstrutorOutputStream(instrutores, qtd, bytesPorAtributo, socket.getOutputStream());
            iosTCP.writeTCP();
            System.out.println("\n[OK] Dados enviados ao servidor!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
