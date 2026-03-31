package com.gym.system.network;

import com.gym.system.io.VisitanteInputStream;
import com.gym.system.io.InstrutorInputStream;
import com.gym.system.model.Visitante;
import com.gym.system.model.Instrutor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ServidorSerializacao {
    private static final int PORTA = 5000;
    private static final int BYTES_POR_ATRIBUTO = 20;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORTA);
            System.out.println("=== SERVIDOR DE SERIALIZAÇÃO ===");
            System.out.println("✓ Servidor aguardando conexões em porta " + PORTA);
            System.out.println("Aguardando clientes...\n");

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ManipuladorCliente(socket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ManipuladorCliente implements Runnable {
        private Socket socket;

        public ManipuladorCliente(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                System.out.println("→ Cliente conectado: " + socket.getInetAddress().getHostAddress());

                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();

                // Lê a quantidade de objetos a serem recebidos
                int quantidade = is.read();

                if (quantidade > 0) {
                    // Lê visitantes ou instrutores (neste caso, visitantes)
                    VisitanteInputStream vis = new VisitanteInputStream(is, BYTES_POR_ATRIBUTO);
                    List<Visitante> visitantes = vis.lerVisitantes(quantidade);

                    System.out.println("  Recebidos " + visitantes.size() + " visitante(s):");
                    for (Visitante v : visitantes) {
                        System.out.println("    • " + v.getNome() + " (CPF: " + v.getCpf() + ")");
                    }

                    // Envia confirmação
                    String confirmacao = "OK - Recebidos " + visitantes.size() + " registro(s)";
                    os.write(confirmacao.getBytes());
                    os.flush();

                    System.out.println("  ✓ Confirmação enviada\n");
                }

                socket.close();

            } catch (IOException e) {
                System.err.println("  ✗ Erro ao processar cliente: " + e.getMessage());
            }
        }
    }
}
