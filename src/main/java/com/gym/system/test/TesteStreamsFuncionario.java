package com.gym.system.test;

import com.gym.system.io.FuncionarioInputStream;
import com.gym.system.io.FuncionarioOutputStream;
import com.gym.system.models.Funcionario;
import com.gym.system.models.enums.Turno;

import java.io.*;
import java.net.*;

public class TesteStreamsFuncionario {
    private static final int BYTES_POR_ATRIBUTO = 64;

    public static void main(String[] args) throws Exception {
        Funcionario[] dadosOriginais = criarFuncionariosMock();

        System.out.println("══════════════════════════════════════");
        System.out.println("       VALIDAÇÃO : Funcionario");
        System.out.println("══════════════════════════════════════\n");

        System.out.println("System.in");
        testarSystem(dadosOriginais);

        System.out.println("\nwriteFile / readFile");
        testarArquivo(dadosOriginais);

        System.out.println("\nwriteTCP / readTCP");
        testarTCP(dadosOriginais);
    }

    private static void testarSystem(Funcionario[] dados) throws Exception {
        System.out.println("Enviando para System.out:");
        try (var out = new FuncionarioOutputStream(dados, dados.length, BYTES_POR_ATRIBUTO, System.out)) {
            out.writeSystem();
        }

        System.out.println("\n(readSystem) requer digitação manual no terminal.");
        Funcionario[] novosDados = new FuncionarioInputStream(System.in).readSystem();
        try (var novoOut = new FuncionarioOutputStream(novosDados, novosDados.length, BYTES_POR_ATRIBUTO, System.out)) {
            novoOut.writeSystem();
        }
    }

    private static void testarArquivo(Funcionario[] dados) throws Exception {
        String caminho = "funcionarios.json";

        try (var fos = new FileOutputStream(caminho);
             var fos2 = new FuncionarioOutputStream(dados, dados.length, BYTES_POR_ATRIBUTO, fos)) {
            fos2.writeFile();
        }
        System.out.println("Arquivo gravado: " + caminho + " (" + new File(caminho).length() + " bytes)");

        try (var fis = new FileInputStream(caminho);
             var fis2 = new FuncionarioInputStream(fis)) {

            Funcionario[] lidos = fis2.readFile();

            System.out.println("Funcionários lidos do arquivo:");
            for (Funcionario f : lidos) {
                if (f != null) System.out.println("   • " + f.getNome() + " | " + f.getEmail());
            }
        }

        new File(caminho).delete();
    }

    private static void testarTCP(Funcionario[] dados) throws Exception {
        int porta = 9878;

        Thread servidor = new Thread(() -> {
            try (ServerSocket ss = new ServerSocket(porta);
                 Socket cliente = ss.accept();
                 var fis = new FuncionarioInputStream(cliente.getInputStream())) {

                System.out.println("Servidor TCP aguardando na porta " + porta + "...");
                Funcionario[] recebidos = fis.readTCP();
                System.out.println("Servidor recebeu " + recebidos.length + " funcionário(s):");
                for (Funcionario f : recebidos) {
                    if (f != null) System.out.println("   • " + f.getNome() + " | " + f.getTurno());
                }
            } catch (IOException e) {
                System.err.println("Erro servidor TCP: " + e.getMessage());
            }
        });
        servidor.start();
        Thread.sleep(500);

        try (Socket s = new Socket("localhost", porta);
             var fos = new FuncionarioOutputStream(dados, dados.length, BYTES_POR_ATRIBUTO, s.getOutputStream())) {
            System.out.println("Cliente TCP enviando...");
            fos.writeTCP();
        }

        servidor.join(2000);
    }

    private static Funcionario[] criarFuncionariosMock() {
        return new Funcionario[] {
                new Funcionario("111.222.333-44", "Fernanda Costa", null, null, "fernanda@gym.com", null, "123", 2200f, Turno.MANHA),
                new Funcionario("999.888.777-66", "Ricardo Alves", null, null, "ricardo@gym.com", null, "456", 2500f, Turno.NOITE)
        };
    }
}