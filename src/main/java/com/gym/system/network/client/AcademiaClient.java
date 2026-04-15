package com.gym.system.network.client;

import com.gym.system.io.*;
import com.gym.system.models.Aluno;
import com.gym.system.models.Funcionario;
import com.gym.system.models.Instrutor;
import com.gym.system.models.Visitante;
import com.gym.system.network.protocol.Mensagem;
import com.gym.system.proto.GymProto.*; // Gerado pelo protoc

import java.io.*;
import java.net.*;

public class AcademiaClient {
    private final String host;
    private final int port;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    // Estado para votação (Questão 5)
    private String sessionToken;
    private static final String MULTICAST_ADDR = "224.0.1.10";
    private static final int MULTICAST_PORT = 55555;

    public AcademiaClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void conectar() throws IOException {
        socket = new Socket(host, port);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        System.out.println("Conectado ao servidor em " + host + ":" + port);

        // Inicia thread de escuta multicast (Questão 5)
        new Thread(this::iniciarListenerMulticast, "MulticastSubscriber").start();
    }

    public void desconectar() throws IOException {
        if (socket != null && !socket.isClosed()) socket.close();
        System.out.println("Desconectado.");
    }

    private Mensagem enviar(Mensagem req) throws IOException {
        dos.writeUTF(req.getTipo().name());
        dos.writeInt(req.getPayload() != null ? req.getPayload().length : 0);
        if (req.getPayload() != null) dos.write(req.getPayload());
        dos.flush();

        String tipoStr = dis.readUTF();
        int len = dis.readInt();
        byte[] payload = new byte[len];
        dis.readFully(payload);
        return new Mensagem(Mensagem.Tipo.valueOf(tipoStr), payload);
    }

    // ===== NOVOS MÉTODOS (QUESTÃO 5 - VOTAÇÃO COM PROTOBUF) =====
    public boolean loginVotacao(String user, String pass) throws IOException {
        LoginRequest req = LoginRequest.newBuilder().setUsername(user).setPassword(pass).build();
        byte[] respData = enviarProtobuf("LOGIN", req.toByteArray());
        LoginResponse resp = LoginResponse.parseFrom(respData);
        if (resp.getSuccess()) {
            sessionToken = resp.getSessionToken();
            System.out.println("Votação: " + resp.getMessage());
            return true;
        }
        System.out.println("[FALHA] " + resp.getMessage());
        return false;
    }

    public void listarModalidades() throws IOException {
        byte[] respData = enviarProtobuf("MODALITY_LIST", new byte[0]);
        ModalityListResponse resp = ModalityListResponse.parseFrom(respData);
        System.out.println("\nMODALIDADES EM VOTAÇÃO:");
        resp.getModalitiesList().forEach(m -> System.out.println("  [" + m.getId() + "] " + m.getName()));
        long remaining = (resp.getDeadline() - System.currentTimeMillis()) / 1000;
        System.out.println("Prazo restante: " + remaining + "s\n");
    }

    public void votar(int modalityId) throws IOException {
        VoteRequest req = VoteRequest.newBuilder().setSessionToken(sessionToken).setModalityId(modalityId).build();
        byte[] respData = enviarProtobuf("VOTE", req.toByteArray());
        VoteResponse resp = VoteResponse.parseFrom(respData);
        System.out.println(resp.getSuccess() ? "[SUCESSO] " + resp.getMessage() : "[FALHA] " + resp.getMessage());
    }

    public void verResultados() throws IOException {
        byte[] respData = enviarProtobuf("RESULTS", new byte[0]);
        ResultsResponse resp = ResultsResponse.parseFrom(respData);
        System.out.println("\nRESULTADOS FINAIS:");
        resp.getResultsList().forEach(r ->
                System.out.printf("  %-10s | Votos: %d | %.1f%%%n", r.getName(), r.getTotalVotes(), r.getPercentage()));
        System.out.println("Vencedor: " + resp.getWinner() + "\n");
    }

    // Envelope dedicado para Protobuf (não interfere no CRUD)
    private byte[] enviarProtobuf(String type, byte[] data) throws IOException {
        dos.writeUTF(type);
        dos.writeInt(data.length);
        dos.write(data);
        dos.flush();

        String respType = dis.readUTF();
        int len = dis.readInt();
        byte[] payload = new byte[len];
        dis.readFully(payload);

        if ("ERROR".equals(respType)) throw new IOException(new String(payload));
        return payload;
    }

    // ===== LISTENER MULTICAST (QUESTÃO 5) =====
    private void iniciarListenerMulticast() {
        new Thread(() -> {
            try (MulticastSocket mSocket = new MulticastSocket(MULTICAST_PORT)) {
                mSocket.joinGroup(InetAddress.getByName(MULTICAST_ADDR));
                byte[] buf = new byte[2048]; // Buffer maior para segurança
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                while (!socket.isClosed()) {
                    mSocket.receive(packet);
                    byte[] data = new byte[packet.getLength()];
                    System.arraycopy(packet.getData(), 0, data, 0, packet.getLength());

                    AdminNotice notice = AdminNotice.parseFrom(data);
                    System.out.println("\n[AVISO ADMIN] " + notice.getSender() + ": " + notice.getContent());
                    System.out.print("> ");
                }
            } catch (Exception e) {
                if (!socket.isClosed()) System.err.println("Erro no listener multicast: " + e.getMessage());
            }
        }, "MulticastSubscriber").start();
    }

    // ===== MÉTODOS CRUD =====
    public String cadastrarVisitantes(Visitante[] visitantes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        VisitanteOutputStream vos = new VisitanteOutputStream(visitantes, visitantes.length, 64, baos);
        vos.writeTCP();
        Mensagem resp = enviar(new Mensagem(Mensagem.Tipo.CADASTRAR_VISITANTE, baos.toByteArray()));
        return new String(resp.getPayload());
    }

    public Visitante[] listarVisitantes() throws IOException {
        Mensagem resp = enviar(new Mensagem(Mensagem.Tipo.LISTAR_VISITANTES, null));
        if (resp.getTipo() == Mensagem.Tipo.ERRO) throw new IOException(new String(resp.getPayload()));
        ByteArrayInputStream bais = new ByteArrayInputStream(resp.getPayload());
        VisitanteInputStream vis = new VisitanteInputStream(bais);
        return vis.readTCP();
    }

    public String cadastrarInstrutores(Instrutor[] instrutores) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InstrutorOutputStream ios = new InstrutorOutputStream(instrutores, instrutores.length, 64, baos);
        ios.writeTCP();
        Mensagem resp = enviar(new Mensagem(Mensagem.Tipo.CADASTRAR_INSTRUTOR, baos.toByteArray()));
        return new String(resp.getPayload());
    }

    public Instrutor[] listarInstrutores() throws IOException {
        Mensagem resp = enviar(new Mensagem(Mensagem.Tipo.LISTAR_INSTRUTORES, null));
        if (resp.getTipo() == Mensagem.Tipo.ERRO) throw new IOException(new String(resp.getPayload()));
        ByteArrayInputStream bais = new ByteArrayInputStream(resp.getPayload());
        InstrutorInputStream iis = new InstrutorInputStream(bais);
        return iis.readTCP();
    }

    public String cadastrarAlunos(Aluno[] alunos) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AlunoOutputStream aos = new AlunoOutputStream(alunos, alunos.length, 64, baos);
        aos.writeTCP();
        Mensagem resp = enviar(new Mensagem(Mensagem.Tipo.CADASTRAR_ALUNO, baos.toByteArray()));
        return new String(resp.getPayload());
    }

    public Aluno[] listarAlunos() throws IOException {
        Mensagem resp = enviar(new Mensagem(Mensagem.Tipo.LISTAR_ALUNOS, null));
        if (resp.getTipo() == Mensagem.Tipo.ERRO) throw new IOException(new String(resp.getPayload()));
        ByteArrayInputStream bais = new ByteArrayInputStream(resp.getPayload());
        AlunoInputStream ais = new AlunoInputStream(bais);
        return ais.readTCP();
    }

    public String cadastrarFuncionarios(Funcionario[] funcionarios) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FuncionarioOutputStream fos = new FuncionarioOutputStream(funcionarios, funcionarios.length, 64, baos);
        fos.writeTCP();
        Mensagem resp = enviar(new Mensagem(Mensagem.Tipo.CADASTRAR_FUNCIONARIO, baos.toByteArray()));
        return new String(resp.getPayload());
    }

    public Funcionario[] listarFuncionarios() throws IOException {
        Mensagem resp = enviar(new Mensagem(Mensagem.Tipo.LISTAR_FUNCIONARIOS, null));
        if (resp.getTipo() == Mensagem.Tipo.ERRO) throw new IOException(new String(resp.getPayload()));
        ByteArrayInputStream bais = new ByteArrayInputStream(resp.getPayload());
        FuncionarioInputStream fis = new FuncionarioInputStream(bais);
        return fis.readTCP();
    }
}