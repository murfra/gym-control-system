package com.gym.system.test;

import com.gym.system.io.VisitanteInputStream;
import com.gym.system.io.VisitanteOutputStream;
import com.gym.system.model.enums.Experiencia;
import com.gym.system.model.Visitante;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;

public class TesteStreamsVisitante {
    private static final int BYTES_POR_ATRIBUTO = 64;

    public static void main(String[] args) throws Exception {
        Visitante[] dadosOriginais = criarVisitantesMock();

        System.out.println("══════════════════════════════════════");
        System.out.println("        VALIDAÇÃO : Visitante");
        System.out.println("══════════════════════════════════════\n");

        System.out.println("System.in");
        testarSystem(dadosOriginais);

        System.out.println("\nwriteFile / readFile");
        testarArquivo(dadosOriginais);

        System.out.println("\nwriteTCP / readTCP");
        testarTCP(dadosOriginais);
    }

    // ─────────────────────────────────────────────────────
    // writeSystem() | readSystem()
    // ─────────────────────────────────────────────────────
    private static void testarSystem(Visitante[] dados) throws Exception {
        System.out.println("Enviando para System.out:");
        try (var out = new VisitanteOutputStream(dados, dados.length, BYTES_POR_ATRIBUTO, System.out)) {
            out.writeSystem();
        }

        System.out.println("\n(readSystem) requer digitação manual no terminal.");
        Visitante[] novosDados = new VisitanteInputStream(System.in).readSystem();
        try (var novoOut = new VisitanteOutputStream(novosDados, novosDados.length, BYTES_POR_ATRIBUTO, System.out)) {
            novoOut.writeSystem();
        }
    }

    // ─────────────────────────────────────────────────────
    // writeFile() | readFile()
    // ─────────────────────────────────────────────────────
    private static void testarArquivo(Visitante[] dados) throws Exception {
        String caminho = "visitantes.json";

        // Escrita
        try (var fos = new FileOutputStream(caminho);
             var vos = new VisitanteOutputStream(dados, dados.length, BYTES_POR_ATRIBUTO, fos)) {
            vos.writeFile();
        }
        System.out.println("Arquivo gravado: " + caminho + " (" + new File(caminho).length() + " bytes)");

        // Leitura
        try (var fis = new FileInputStream(caminho);
             var vis = new VisitanteInputStream(fis)) {

            Visitante[] lidos = vis.readFile();

            System.out.println("Visitantes lidos do arquivo:");
            for (Visitante v : lidos) {
                if (v != null) System.out.println("   • " + v.getNome() + " | " + v.getEmail());
            }
        }
        new File(caminho).delete(); // Limpeza automática
    }

    // ─────────────────────────────────────────────────────
    // writeTCP() | readTCP()
    // ─────────────────────────────────────────────────────
    private static void testarTCP(Visitante[] dados) throws Exception {
        int porta = 9876;

        // Thread Servidor (readTCP)
        Thread servidor = new Thread(() -> {
            try (ServerSocket ss = new ServerSocket(porta);
                 Socket cliente = ss.accept();
                 var vis = new VisitanteInputStream(cliente.getInputStream())) {

                System.out.println("Servidor TCP aguardando na porta " + porta + "...");
                Visitante[] recebidos = vis.readTCP();
                System.out.println("Servidor recebeu " + recebidos.length + " visitante(s):");
                for (Visitante v : recebidos) {
                    if (v != null) System.out.println("   • " + v.getNome() + " | " + v.getNivelExperiencia());
                }
            } catch (IOException e) {
                System.err.println("Erro servidor TCP: " + e.getMessage());
            }
        });
        servidor.start();
        Thread.sleep(500); // Aguarda bind

        // Thread Cliente (writeTCP)
        try (Socket s = new Socket("localhost", porta);
             var vos = new VisitanteOutputStream(dados, dados.length, BYTES_POR_ATRIBUTO, s.getOutputStream())) {
            System.out.println("Cliente TCP enviando...");
            vos.writeTCP();
        }
        servidor.join(2000);
    }

    private static Visitante[] criarVisitantesMock() {
        return new Visitante[] {
                new Visitante("111.222.333-44", "Ana Silva", null, null, "ana@gym.com", null, LocalDateTime.now(), Experiencia.INICIANTE),
                new Visitante("555.567.777-88", "Carlos Lima", null, null, "carlos@gym.com", null, LocalDateTime.now(), Experiencia.AVANCADO)
        };
    }
}