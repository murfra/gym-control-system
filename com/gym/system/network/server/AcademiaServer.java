package com.gym.system.network.server;

import com.gym.system.core.Academia;
import com.gym.system.io.*;
import com.gym.system.model.Aluno;
import com.gym.system.model.Funcionario;
import com.gym.system.model.Instrutor;
import com.gym.system.model.Visitante;
import com.gym.system.network.protocol.Mensagem;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class AcademiaServer {
    private final int port;
    private final Academia academia;

    public AcademiaServer(int port, Academia academia) {
        this.port = port;
        this.academia = academia;
    }

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor Academia online na porta " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                new Thread(new ClienteHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
        }
    }

    private class ClienteHandler implements Runnable {
        private final Socket socket;

        public ClienteHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    InputStream is = socket.getInputStream();
                    OutputStream os = socket.getOutputStream();
                    DataInputStream dis = new DataInputStream(is);
                    DataOutputStream dos = new DataOutputStream(os)
            ) {
                while (true) {
                    Mensagem request;

                    try {
                        request = lerMensagem(dis);
                    } catch (EOFException e) {
                        System.out.println("Cliente desconectou: " + socket.getInetAddress());
                        break;
                    }

                    System.out.println("Recebido: " + request.getTipo());

                    Mensagem response = processarRequest(request);
                    enviarMensagem(dos, response);
                }

            } catch (IOException e) {
                System.err.println("Erro na conexão com " + socket.getInetAddress() + ": " + e.getMessage());
            } finally {
                try {
                    if (!socket.isClosed()) {
                        socket.close();
                    }
                } catch (IOException ignored) {}
            }
        }

        private Mensagem lerMensagem(DataInputStream dis) throws IOException {
            String tipoStr = dis.readUTF();
            int payloadLen = dis.readInt();

            byte[] payload = new byte[payloadLen];
            if (payloadLen > 0) {
                dis.readFully(payload);
            }

            return new Mensagem(Mensagem.Tipo.valueOf(tipoStr), payload);
        }

        private void enviarMensagem(DataOutputStream dos, Mensagem msg) throws IOException {
            dos.writeUTF(msg.getTipo().name());
            dos.writeInt(msg.getPayload() != null ? msg.getPayload().length : 0);

            if (msg.getPayload() != null && msg.getPayload().length > 0) {
                dos.write(msg.getPayload());
            }

            dos.flush();
        }

        private Mensagem processarRequest(Mensagem req) {
            try {
                switch (req.getTipo()) {
                    case CADASTRAR_VISITANTE:
                        return cadastrarVisitante(req.getPayload());

                    case LISTAR_VISITANTES:
                        return listarVisitantes();

                    case CADASTRAR_INSTRUTOR:
                        return cadastrarInstrutor(req.getPayload());

                    case LISTAR_INSTRUTORES:
                        return listarInstrutores();

                    case CADASTRAR_ALUNO:
                        return cadastrarAluno(req.getPayload());

                    case LISTAR_ALUNOS:
                        return listarAlunos();

                    case CADASTRAR_FUNCIONARIO:
                        return cadastrarFuncionario(req.getPayload());

                    case LISTAR_FUNCIONARIOS:
                        return listarFuncionarios();

                    default:
                        return new Mensagem(
                                Mensagem.Tipo.ERRO,
                                "Comando desconhecido".getBytes()
                        );
                }
            } catch (Exception e) {
                return new Mensagem(
                        Mensagem.Tipo.ERRO,
                        ("Erro interno: " + e.getMessage()).getBytes()
                );
            }
        }

        private Mensagem cadastrarVisitante(byte[] payload) throws IOException {
            ByteArrayInputStream bais = new ByteArrayInputStream(payload);
            VisitanteInputStream vis = new VisitanteInputStream(bais);
            Visitante[] novos = vis.readTCP();

            for (Visitante v : novos) {
                if (v != null) {
                    academia.adicionarVisitante(v);
                }
            }

            return new Mensagem(
                    Mensagem.Tipo.SUCESSO,
                    ("Cadastro realizado: " + novos.length + " visitante(s)").getBytes()
            );
        }

        private Mensagem listarVisitantes() throws IOException {
            Visitante[] lista = academia.getVisitantes().toArray(new Visitante[0]);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            VisitanteOutputStream vos = new VisitanteOutputStream(lista, lista.length, 64, baos);
            vos.writeTCP();

            return new Mensagem(Mensagem.Tipo.SUCESSO, baos.toByteArray());
        }

        private Mensagem cadastrarInstrutor(byte[] payload) throws IOException {
            ByteArrayInputStream bais = new ByteArrayInputStream(payload);
            InstrutorInputStream iis = new InstrutorInputStream(bais);
            Instrutor[] novos = iis.readTCP();

            for (Instrutor i : novos) {
                if (i != null) {
                    academia.adicionarInstrutor(i);
                }
            }

            return new Mensagem(
                    Mensagem.Tipo.SUCESSO,
                    ("Cadastro realizado: " + novos.length + " instrutor(es)").getBytes()
            );
        }

        private Mensagem listarInstrutores() throws IOException {
            Instrutor[] lista = academia.getInstrutores().toArray(new Instrutor[0]);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InstrutorOutputStream ios = new InstrutorOutputStream(lista, lista.length, 64, baos);
            ios.writeTCP();

            return new Mensagem(Mensagem.Tipo.SUCESSO, baos.toByteArray());
        }

        private Mensagem cadastrarAluno(byte[] payload) throws IOException {
            ByteArrayInputStream bais = new ByteArrayInputStream(payload);
            AlunoInputStream ais = new AlunoInputStream(bais);
            Aluno[] novos = ais.readTCP();

            for (Aluno a : novos) {
                if (a != null) {
                    academia.adicionarAluno(a);
                }
            }

            return new Mensagem(
                    Mensagem.Tipo.SUCESSO,
                    ("Cadastro realizado: " + novos.length + " aluno(s)").getBytes()
            );
        }

        private Mensagem listarAlunos() throws IOException {
            Aluno[] lista = academia.getAlunos().toArray(new Aluno[0]);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            AlunoOutputStream aos = new AlunoOutputStream(lista, lista.length, 64, baos);
            aos.writeTCP();

            return new Mensagem(Mensagem.Tipo.SUCESSO, baos.toByteArray());
        }

        private Mensagem cadastrarFuncionario(byte[] payload) throws IOException {
            ByteArrayInputStream bais = new ByteArrayInputStream(payload);
            FuncionarioInputStream fis = new FuncionarioInputStream(bais);
            Funcionario[] novos = fis.readTCP();

            for (Funcionario f : novos) {
                if (f != null) {
                    academia.adicionarFuncionario(f);
                }
            }

            return new Mensagem(
                    Mensagem.Tipo.SUCESSO,
                    ("Cadastro realizado: " + novos.length + " funcionário(s)").getBytes()
            );
        }

        private Mensagem listarFuncionarios() throws IOException {
            Funcionario[] lista = academia.getFuncionarios().toArray(new Funcionario[0]);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FuncionarioOutputStream fos = new FuncionarioOutputStream(lista, lista.length, 64, baos);
            fos.writeTCP();

            return new Mensagem(Mensagem.Tipo.SUCESSO, baos.toByteArray());
        }
    }
}