package com.gym.system.test;

import com.gym.system.io.AlunoInputStream;
import com.gym.system.io.AlunoOutputStream;
import com.gym.system.models.Aluno;
import com.gym.system.models.enums.Experiencia;

import java.io.*;
import java.net.*;

public class TesteStreamsAluno {
    private static final int BYTES_POR_ATRIBUTO = 64;

    public static void main(String[] args) throws Exception {
        Aluno[] dadosOriginais = criarAlunosMock();

        System.out.println("══════════════════════════════════════");
        System.out.println("          VALIDAÇÃO : Aluno");
        System.out.println("══════════════════════════════════════\n");

        System.out.println("System.in");
        testarSystem(dadosOriginais);

        System.out.println("\nwriteFile / readFile");
        testarArquivo(dadosOriginais);

        System.out.println("\nwriteTCP / readTCP");
        testarTCP(dadosOriginais);
    }

    private static void testarSystem(Aluno[] dados) throws Exception {
        System.out.println("Enviando para System.out:");
        try (var out = new AlunoOutputStream(dados, dados.length, BYTES_POR_ATRIBUTO, System.out)) {
            out.writeSystem();
        }

        System.out.println("\n(readSystem) requer digitação manual no terminal.");
        Aluno[] novosDados = new AlunoInputStream(System.in).readSystem();
        try (var novoOut = new AlunoOutputStream(novosDados, novosDados.length, BYTES_POR_ATRIBUTO, System.out)) {
            novoOut.writeSystem();
        }
    }

    private static void testarArquivo(Aluno[] dados) throws Exception {
        String caminho = "alunos.json";

        try (var fos = new FileOutputStream(caminho);
             var aos = new AlunoOutputStream(dados, dados.length, BYTES_POR_ATRIBUTO, fos)) {
            aos.writeFile();
        }
        System.out.println("Arquivo gravado: " + caminho + " (" + new File(caminho).length() + " bytes)");

        try (var fis = new FileInputStream(caminho);
             var ais = new AlunoInputStream(fis)) {

            Aluno[] lidos = ais.readFile();

            System.out.println("Alunos lidos do arquivo:");
            for (Aluno a : lidos) {
                if (a != null) System.out.println("   • " + a.getNome() + " | " + a.getEmail());
            }
        }

        new File(caminho).delete();
    }

    private static void testarTCP(Aluno[] dados) throws Exception {
        int porta = 9877;

        Thread servidor = new Thread(() -> {
            try (ServerSocket ss = new ServerSocket(porta);
                 Socket cliente = ss.accept();
                 var ais = new AlunoInputStream(cliente.getInputStream())) {

                System.out.println("Servidor TCP aguardando na porta " + porta + "...");
                Aluno[] recebidos = ais.readTCP();
                System.out.println("Servidor recebeu " + recebidos.length + " aluno(s):");
                for (Aluno a : recebidos) {
                    if (a != null) System.out.println("   • " + a.getNome() + " | " + a.getNivelExperiencia());
                }
            } catch (IOException e) {
                System.err.println("Erro servidor TCP: " + e.getMessage());
            }
        });
        servidor.start();
        Thread.sleep(500);

        try (Socket s = new Socket("localhost", porta);
             var aos = new AlunoOutputStream(dados, dados.length, BYTES_POR_ATRIBUTO, s.getOutputStream())) {
            System.out.println("Cliente TCP enviando...");
            aos.writeTCP();
        }

        servidor.join(2000);
    }

    private static Aluno[] criarAlunosMock() {
        return new Aluno[] {
                new Aluno("111.222.333-44", "Marina Souza", null, null, "marina@gym.com", null, Experiencia.INICIANTE),
                new Aluno("555.666.777-88", "Lucas Pereira", null, null, "lucas@gym.com", null, Experiencia.INTERMEDIARIO)
        };
    }
}