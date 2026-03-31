package com.gym.system.network;

import com.gym.system.io.VisitanteInputStream;
import com.gym.system.io.VisitanteOutputStream;
import com.gym.system.io.InstrutorInputStream;
import com.gym.system.io.InstrutorOutputStream;
import com.gym.system.model.Visitante;
import com.gym.system.model.Instrutor;
import com.gym.system.model.Endereco;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;

public class ClienteSerializacao {
    private static final String SERVIDOR = "localhost";
    private static final int PORTA = 5000;
    private static final int BYTES_POR_ATRIBUTO = 20;

    public static void main(String[] args) {
        try {
            // Teste 1: Sistema.out (Console)
            testaConsole();

            // Teste 2: Arquivo
            testaArquivo();

            // Teste 3: Servidor remoto (TCP)
            testaServidorRemoto();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Teste 1: Envia dados para System.out
    static void testaConsole() throws IOException {
        System.out.println("\n=== TESTE 1: Enviando Visitantes para Console (System.out) ===");
        
        Visitante[] visitantes = {
            new Visitante(12345678, 987654321, "João Silva", LocalDate.of(1990, 5, 15), "joao@email.com", null),
            new Visitante(87654321, 123456789, "Maria Santos", LocalDate.of(1995, 8, 20), "maria@email.com", null),
            new Visitante(55555555, 666666666, "Pedro Costa", LocalDate.of(1988, 3, 10), "pedro@email.com", null)
        };

        VisitanteOutputStream vos = new VisitanteOutputStream(visitantes, 3, BYTES_POR_ATRIBUTO, System.out);
        vos.enviarDados();
        System.out.println("\n✓ Dados enviados para console com sucesso!");
    }

    // Teste 2: Envia dados para um arquivo
    static void testaArquivo() throws IOException {
        System.out.println("\n=== TESTE 2: Enviando Instrutores para Arquivo ===");
        
        Instrutor[] instrutores = {
            new Instrutor(11111111, 111111111, "Carlos Fitness", LocalDate.of(1985, 1, 5), "carlos@gym.com", null),
            new Instrutor(22222222, 222222222, "Ana Pilates", LocalDate.of(1992, 6, 12), "ana@gym.com", null),
            new Instrutor(33333333, 333333333, "Bruno Musculação", LocalDate.of(1989, 9, 25), "bruno@gym.com", null)
        };

        String caminhoArquivo = "instrutores_dados.bin";
        FileOutputStream fos = new FileOutputStream(caminhoArquivo);
        InstrutorOutputStream ios = new InstrutorOutputStream(instrutores, 3, BYTES_POR_ATRIBUTO, fos);
        ios.enviarDados();
        fos.close();
        
        System.out.println("✓ Instrutores salvos em: " + caminhoArquivo);
        System.out.println("  Tamanho do arquivo: " + new File(caminhoArquivo).length() + " bytes");

        // Lendo de volta do arquivo
        System.out.println("\n--- Lendo dados do arquivo ---");
        FileInputStream fis = new FileInputStream(caminhoArquivo);
        InstrutorInputStream iis = new InstrutorInputStream(fis, BYTES_POR_ATRIBUTO);
        var instrutoresLidos = iis.lerInstrutores(3);
        
        for (Instrutor inst : instrutoresLidos) {
            System.out.println("Instrutor: " + inst.getNome() + " | CPF: " + inst.getCpf() + " | Email: " + inst.getEmail());
        }
        fis.close();
    }

    // Teste 3: Envia dados para servidor remoto via TCP
    static void testaServidorRemoto() throws IOException {
        System.out.println("\n=== TESTE 3: Enviando Visitantes para Servidor Remoto (TCP) ===");
        
        try {
            Socket socket = new Socket(SERVIDOR, PORTA);
            System.out.println("✓ Conectado ao servidor em " + SERVIDOR + ":" + PORTA);

            OutputStream os = socket.getOutputStream();

            Visitante[] visitantes = {
                new Visitante(99999999, 999999999, "Lucas Travel", LocalDate.of(1993, 7, 18), "lucas@email.com", null),
                new Visitante(88888888, 888888888, "Rita Viagem", LocalDate.of(1991, 11, 22), "rita@email.com", null)
            };

            // Primeiro envia a quantidade de visitantes
            os.write(2);
            os.flush();

            VisitanteOutputStream vos = new VisitanteOutputStream(visitantes, 2, BYTES_POR_ATRIBUTO, os);
            vos.enviarDados();

            System.out.println("✓ Visitantes enviados ao servidor!");

            // Recebe confirmação do servidor
            InputStream is = socket.getInputStream();
            byte[] confirmacao = new byte[50];
            int lidos = is.read(confirmacao);
            if (lidos > 0) {
                String msg = new String(confirmacao, 0, lidos).trim();
                System.out.println("Resposta do servidor: " + msg);
            }

            socket.close();

        } catch (IOException e) {
            System.out.println("✗ Erro ao conectar ao servidor. Certifique-se de que o servidor está rodando.");
        }
    }
}
