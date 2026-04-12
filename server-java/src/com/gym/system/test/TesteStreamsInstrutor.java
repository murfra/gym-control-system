package com.gym.system.test;

import com.gym.system.io.InstrutorInputStream;
import com.gym.system.io.InstrutorOutputStream;
import com.gym.system.model.Instrutor;
import com.gym.system.model.enums.Turno;

import java.io.*;
import java.net.*;
//import java.time.LocalDateTime;

public class TesteStreamsInstrutor {
    private static final int BYTES_POR_ATRIBUTO = 64;

    public static void main(String[] args) throws Exception {
        Instrutor[] dadosOriginais = criarInstrutorsMock();

        System.out.println("══════════════════════════════════════");
        System.out.println("        VALIDAÇÃO : Instrutor");
        System.out.println("══════════════════════════════════════\n");

        System.out.println("📌 System.in");
        testarSystem(dadosOriginais);

        System.out.println("\n📌 writeFile / readFile");
        testarArquivo(dadosOriginais);

        System.out.println("\n📌 writeTCP / readTCP");
        testarTCP(dadosOriginais);
    }

    // ─────────────────────────────────────────────────────
    // writeSystem() | readSystem()
    // ─────────────────────────────────────────────────────
    private static void testarSystem(Instrutor[] dados) throws Exception {
        System.out.println("📤 Enviando para System.out:");
        try (var out = new InstrutorOutputStream(dados, dados.length, BYTES_POR_ATRIBUTO, System.out)) {
            out.writeSystem();
        }

        System.out.println("\n(readSystem) requer digitação manual no terminal.");
        Instrutor[] novosDados = new InstrutorInputStream(System.in).readSystem();
        try (var novoOut = new InstrutorOutputStream(novosDados, novosDados.length, BYTES_POR_ATRIBUTO, System.out)) {
            novoOut.writeSystem();
        }
    }

    // ─────────────────────────────────────────────────────
    // writeFile() | readFile()
    // ─────────────────────────────────────────────────────
    private static void testarArquivo(Instrutor[] dados) throws Exception {
        String caminho = "instrutores.json";

        // Escrita
        try (var fos = new FileOutputStream(caminho);
             var vos = new InstrutorOutputStream(dados, dados.length, BYTES_POR_ATRIBUTO, fos)) {
            vos.writeFile();
        }
        System.out.println("✅ Arquivo gravado: " + caminho + " (" + new File(caminho).length() + " bytes)");

        // Leitura
        try (var fis = new FileInputStream(caminho);
             var vis = new InstrutorInputStream(fis)) {

            Instrutor[] lidos = vis.readFile();

            System.out.println("📥 Instrutores lidos do arquivo:");
            for (Instrutor v : lidos) {
                if (v != null) System.out.println("   • " + v.getNome() + " | " + v.getEmail());
            }
        }
        new File(caminho).delete(); // Limpeza automática
    }

    // ─────────────────────────────────────────────────────
    // writeTCP() | readTCP()
    // ─────────────────────────────────────────────────────
    private static void testarTCP(Instrutor[] dados) throws Exception {
        int porta = 9876;

        // Thread Servidor (readTCP)
        Thread servidor = new Thread(() -> {
            try (ServerSocket ss = new ServerSocket(porta);
                 Socket cliente = ss.accept();
                 var vis = new InstrutorInputStream(cliente.getInputStream())) {

                System.out.println("🟢 Servidor TCP aguardando na porta " + porta + "...");
                Instrutor[] recebidos = vis.readTCP();
                System.out.println("📦 Servidor recebeu " + recebidos.length + " instrutor(es):");
                for (Instrutor v : recebidos) {
                    if (v != null) System.out.println("   • " + v.getNome() + " | " + v.getCref());
                }
            } catch (IOException e) {
                System.err.println("❌ Erro servidor TCP: " + e.getMessage());
            }
        });
        servidor.start();
        Thread.sleep(500); // Aguarda bind

        // Thread Cliente (writeTCP)
        try (Socket s = new Socket("localhost", porta);
             var vos = new InstrutorOutputStream(dados, dados.length, BYTES_POR_ATRIBUTO, s.getOutputStream())) {
            System.out.println("🔵 Cliente TCP enviando...");
            vos.writeTCP();
        }
        servidor.join(2000);
    }

    private static Instrutor[] criarInstrutorsMock() {
        return new Instrutor[] {
                new Instrutor("111.222.333-44", "123456-G/CE", "João Augusto", null, null, "joaoaugusto@ptreiner.com", null, null, 2000, Turno.MANHA),
                new Instrutor("555.567.777-88", "012345-G/SP", "Pedro Mascarenhas", null, null, "mascarenhas@ptreiner.com", null, null, 2000, Turno.TARDE)
        };
    }
}