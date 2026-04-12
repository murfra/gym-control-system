package com.gym.system.network.server;

import com.gym.system.core.Academia;
import com.gym.system.io.*;
import com.gym.system.model.Instrutor;
import com.gym.system.model.Visitante;
import com.gym.system.network.protocol.Mensagem;

import java.io.*;
import java.net.*;

public class AcademiaServer {
    private final int port;
    private final Academia academia; // Contexto central do sistema

    public AcademiaServer(int port, Academia academia) {
        this.port = port;
        this.academia = academia;
    }

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("🏋️ Servidor Academia online na porta " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("🔗 Cliente conectado: " + clientSocket.getInetAddress());
                // Questão 4: Servidor multi-threaded
                new Thread(new ClienteHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("❌ Erro ao iniciar servidor: " + e.getMessage());
        }
    }

    private class ClienteHandler implements Runnable {
        private final Socket socket;
        public ClienteHandler(Socket socket) { this.socket = socket; }

        @Override
        public void run() {
            try (InputStream is = socket.getInputStream();
                 OutputStream os = socket.getOutputStream();
                 DataInputStream dis = new DataInputStream(is);
                 DataOutputStream dos = new DataOutputStream(os)) {

                // 1️⃣ DESEMPACOTAR: Ler requisição do cliente
                Mensagem request = lerMensagem(dis);
                System.out.println("📥 Recebido: " + request.getTipo());

                // 2️⃣ PROCESSAR: Executar lógica de negócio
                Mensagem response = processarRequest(request);

                // 3️⃣ EMPACOTAR: Enviar resposta ao cliente
                enviarMensagem(dos, response);

            } catch (IOException e) {
                System.err.println("❌ Erro na conexão: " + e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        private Mensagem lerMensagem(DataInputStream dis) throws IOException {
            String tipoStr = dis.readUTF();
            int payloadLen = dis.readInt();
            byte[] payload = new byte[payloadLen];
            dis.readFully(payload);
            return new Mensagem(Mensagem.Tipo.valueOf(tipoStr), payload);
        }

        private void enviarMensagem(DataOutputStream dos, Mensagem msg) throws IOException {
            dos.writeUTF(msg.getTipo().name());
            dos.writeInt(msg.getPayload() != null ? msg.getPayload().length : 0);
            if (msg.getPayload() != null) dos.write(msg.getPayload());
            dos.flush();
        }

        private Mensagem processarRequest(Mensagem req) throws IOException {
            try {
                switch (req.getTipo()) {
                    case CADASTRAR_VISITANTE: return cadastrarVisitante(req.getPayload());
                    case LISTAR_VISITANTES:   return listarVisitantes();
                    case CADASTRAR_INSTRUTOR: return cadastrarInstrutor(req.getPayload());
                    case LISTAR_INSTRUTORES:  return listarInstrutores();
                    default: return new Mensagem(Mensagem.Tipo.ERRO, "Comando desconhecido".getBytes());
                }
            } catch (Exception e) {
                return new Mensagem(Mensagem.Tipo.ERRO, ("Erro interno: " + e.getMessage()).getBytes());
            }
        }

        // ===== MÉTODOS DE NEGÓCIO USANDO OS STREAMS =====
        private Mensagem cadastrarVisitante(byte[] payload) throws IOException {
            ByteArrayInputStream bais = new ByteArrayInputStream(payload);
            // Utiliza o construtor: VisitanteInputStream(InputStream in)
            VisitanteInputStream vis = new VisitanteInputStream(bais);
            Visitante[] novos = vis.readTCP();

            for (Visitante v : novos) if (v != null) academia.adicionarVisitante(v);
            return new Mensagem(Mensagem.Tipo.SUCESSO, ("Cadastro realizado: " + novos.length + " visitante(s)").getBytes());
        }

        private Mensagem listarVisitantes() throws IOException {
            Visitante[] lista = academia.getVisitantes().toArray(new Visitante[0]);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Construtor: (array, qtd, bytesPorAtributo, out)
            VisitanteOutputStream vos = new VisitanteOutputStream(lista, lista.length, 64, baos);
            vos.writeTCP();
            return new Mensagem(Mensagem.Tipo.SUCESSO, baos.toByteArray());
        }

        private Mensagem cadastrarInstrutor(byte[] payload) throws IOException {
            ByteArrayInputStream bais = new ByteArrayInputStream(payload);
            InstrutorInputStream iis = new InstrutorInputStream(bais);
            Instrutor[] novos = iis.readTCP();

            for (Instrutor i : novos) if (i != null) academia.adicionarInstrutor(i);
            return new Mensagem(Mensagem.Tipo.SUCESSO, ("Cadastro realizado: " + novos.length + " instrutor(es)").getBytes());
        }

        private Mensagem listarInstrutores() throws IOException {
            Instrutor[] lista = academia.getInstrutores().toArray(new Instrutor[0]);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InstrutorOutputStream ios = new InstrutorOutputStream(lista, lista.length, 64, baos);
            ios.writeTCP();
            return new Mensagem(Mensagem.Tipo.SUCESSO, baos.toByteArray());
        }
    }
}