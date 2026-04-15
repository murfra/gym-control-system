package com.gym.system.network.server;

import com.gym.system.core.Academia;
import com.gym.system.io.*;
import com.gym.system.models.*;
import com.gym.system.network.protocol.Mensagem;
import com.gym.system.proto.GymProto.*;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class AcademiaServer {
    private final int port;
    private final Academia academia;
    private final int votingDurationMillis;

    // Estado da Votação (Questão 5)
    private final Map<Integer, Integer> votes = new ConcurrentHashMap<>();
    private final List<Modality> modalities = Arrays.asList(
            Modality.newBuilder().setId(1).setName("CrossFit").build(),
            Modality.newBuilder().setId(2).setName("Musculação").build(),
            Modality.newBuilder().setId(3).setName("Muay Thai").build(),
            Modality.newBuilder().setId(4).setName("Natação").build()
    );
    private volatile boolean votingOpen = true;
    private long deadline;

    // Multicast
    private MulticastSocket multicastSocket;
    private static final String MULTICAST_ADDR = "224.0.1.10";
    private static final int MULTICAST_PORT = 55555;

    public AcademiaServer(int port, Academia academia, int votingDurationMillis) {
        this.port = port;
        this.academia = academia;
        this.votingDurationMillis = votingDurationMillis;
    }

    public void iniciar() throws IOException {
        this.deadline = System.currentTimeMillis() + votingDurationMillis;

        // Thread Multicast (Admin)
        multicastSocket = new MulticastSocket(MULTICAST_PORT);
        multicastSocket.joinGroup(InetAddress.getByName(MULTICAST_ADDR));
        new Thread(this::multicastLoop, "MulticastPublisher").start();

        // Thread Timer (Deadline)
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(this::closeVoting, votingDurationMillis, TimeUnit.MILLISECONDS);

        // Servidor TCP Multithreaded
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor Academia online na porta " + port);
            System.out.println("Prazo de votação: " + (votingDurationMillis / 1000) + "s");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
                new Thread(new ClienteHandler(clientSocket), "ClientWorker-" + System.nanoTime()).start();
            }
        }
    }

    private void closeVoting() {
        votingOpen = false;
        System.out.println("Prazo de votação encerrado.");
    }

    private void multicastLoop() {
        String[] notices = {
                "[AVISO] Manutenção na piscina das 14h às 16h",
                "[AVISO] Promoção de matrícula válida até sexta!",
                "[AVISO] Aula de Sábado cancelada",
                "[AVISO] Nova máquina instalada"
        };
        int i = 0;
        while (true) {
            try {
                AdminNotice notice = AdminNotice.newBuilder()
                        .setSender("Administrador")
                        .setTimestamp(System.currentTimeMillis())
                        .setContent(notices[i % notices.length])
                        .build();
                byte[] data = notice.toByteArray();
                DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(MULTICAST_ADDR), MULTICAST_PORT);
                multicastSocket.send(packet);
                System.out.println("📡 Multicast: " + notice.getContent());
                i++;
                Thread.sleep(15000);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    // =========================================================
    // INNER CLASS: HANDLER DE CLIENTE
    // =========================================================
    private class ClienteHandler implements Runnable {
        private final Socket socket;

        public ClienteHandler(Socket socket) { this.socket = socket; }

        @Override
        public void run() {
            try (DataInputStream dis = new DataInputStream(socket.getInputStream());
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

                while (true) {
                    String tipoStr = dis.readUTF();
                    int payloadLen = dis.readInt();
                    byte[] payload = new byte[payloadLen];
                    if (payloadLen > 0) dis.readFully(payload);

                    if (isVotingType(tipoStr)) {
                        handleVoting(tipoStr, payload, dos);
                    } else {
                        handleCrud(tipoStr, payload, dos);
                    }
                }
            } catch (EOFException e) {
                System.out.println("🔌 Cliente desconectou: " + socket.getInetAddress());
            } catch (IOException e) {
                System.err.println("Erro: " + e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        private boolean isVotingType(String type) {
            return type.equals("LOGIN") || type.equals("MODALITY_LIST") ||
                    type.equals("VOTE") || type.equals("RESULTS");
        }

        private void handleVoting(String type, byte[] payload, DataOutputStream dos) throws IOException {
            try {
                switch (type) {
                    case "LOGIN" -> handleLogin(payload, dos);
                    case "MODALITY_LIST" -> handleModalityList(dos);
                    case "VOTE" -> handleVote(payload, dos);
                    case "RESULTS" -> handleResults(dos);
                }
            } catch (InvalidProtocolBufferException e) {
                writeVotingError(dos, "Payload Protobuf inválido");
            }
        }

        // ===== MÉTODOS DE VOTAÇÃO =====
        private void handleLogin(byte[] payload, DataOutputStream dos) throws InvalidProtocolBufferException, IOException {
            LoginRequest req = LoginRequest.parseFrom(payload);
            boolean ok = req.getUsername().equalsIgnoreCase("aluno") && req.getPassword().equals("123");
            String token = ok ? UUID.randomUUID().toString() : null;
            LoginResponse resp = LoginResponse.newBuilder()
                    .setSuccess(ok)
                    .setSessionToken(token != null ? token : "")
                    .setMessage(ok ? "Login aprovado" : "Credenciais inválidas")
                    .build();
            writeVotingResponse(dos, resp.toByteArray());
        }

        private void handleModalityList(DataOutputStream dos) throws IOException {
            ModalityListResponse resp = ModalityListResponse.newBuilder()
                    .addAllModalities(modalities)
                    .setDeadline(deadline)
                    .build();
            writeVotingResponse(dos, resp.toByteArray());
        }

        private void handleVote(byte[] payload, DataOutputStream dos) throws InvalidProtocolBufferException, IOException {
            if (!votingOpen) { writeVotingError(dos, "Prazo encerrado"); return; }
            VoteRequest req = VoteRequest.parseFrom(payload);
            votes.merge(req.getModalityId(), 1, Integer::sum);
            VoteResponse resp = VoteResponse.newBuilder().setSuccess(true).setMessage("Voto computado!").build();
            writeVotingResponse(dos, resp.toByteArray());
        }

        private void handleResults(DataOutputStream dos) throws IOException {
            if (votingOpen) { writeVotingError(dos, "Votação em andamento"); return; }

            int totalVotes = votes.values().stream().mapToInt(Integer::intValue).sum();

            List<VoteResult> results = new ArrayList<>();
            String winner = "Nenhum voto";
            int maxVotes = 0;

            for (Modality m : modalities) {
                int v = votes.getOrDefault(m.getId(), 0);
                double pct = totalVotes > 0 ? (double) v / totalVotes * 100 : 0;
                results.add(VoteResult.newBuilder()
                        .setModalityId(m.getId())
                        .setName(m.getName())
                        .setTotalVotes(v)
                        .setPercentage(pct).build());
                if (v > maxVotes) { maxVotes = v; winner = m.getName(); }
            }

            ResultsResponse resp = ResultsResponse.newBuilder()
                    .addAllResults(results)
                    .setWinner(winner)
                    .build();
            writeVotingResponse(dos, resp.toByteArray());
        }

        private void writeVotingResponse(DataOutputStream dos, byte[] data) throws IOException {
            dos.writeUTF("OK");
            dos.writeInt(data.length);
            dos.write(data);
            dos.flush();
        }

        private void writeVotingError(DataOutputStream dos, String msg) throws IOException {
            dos.writeUTF("ERROR");
            dos.writeInt(msg.getBytes().length);
            dos.write(msg.getBytes());
            dos.flush();
        }

        private void handleCrud(String tipoStr, byte[] payload, DataOutputStream dos) throws IOException {
            Mensagem request = new Mensagem(Mensagem.Tipo.valueOf(tipoStr), payload);
            Mensagem response = processarRequest(request);
            enviarMensagem(dos, response);
        }

        private Mensagem lerMensagem(DataInputStream dis) throws IOException {
            String tipoStr = dis.readUTF();
            int payloadLen = dis.readInt();
            byte[] payload = new byte[payloadLen];
            if (payloadLen > 0) dis.readFully(payload);
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
                    case CADASTRAR_VISITANTE: return cadastrarVisitante(req.getPayload());
                    case LISTAR_VISITANTES: return listarVisitantes();
                    case CADASTRAR_INSTRUTOR: return cadastrarInstrutor(req.getPayload());
                    case LISTAR_INSTRUTORES: return listarInstrutores();
                    case CADASTRAR_ALUNO: return cadastrarAluno(req.getPayload());
                    case LISTAR_ALUNOS: return listarAlunos();
                    case CADASTRAR_FUNCIONARIO: return cadastrarFuncionario(req.getPayload());
                    case LISTAR_FUNCIONARIOS: return listarFuncionarios();
                    default: return new Mensagem(Mensagem.Tipo.ERRO, "Comando desconhecido".getBytes());
                }
            } catch (Exception e) {
                return new Mensagem(Mensagem.Tipo.ERRO, ("Erro: " + e.getMessage()).getBytes());
            }
        }

        private Mensagem cadastrarVisitante(byte[] payload) throws IOException {
            ByteArrayInputStream bais = new ByteArrayInputStream(payload);
            VisitanteInputStream vis = new VisitanteInputStream(bais);
            Visitante[] novos = vis.readTCP();
            for (Visitante v : novos) if (v != null) academia.adicionarVisitante(v);
            return new Mensagem(Mensagem.Tipo.SUCESSO, ("Cadastro: " + novos.length + " visitante(s)").getBytes());
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
            for (Instrutor i : novos) if (i != null) academia.adicionarInstrutor(i);
            return new Mensagem(Mensagem.Tipo.SUCESSO, ("Cadastro: " + novos.length + " instrutor(es)").getBytes());
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
            for (Aluno a : novos) if (a != null) academia.adicionarAluno(a);
            return new Mensagem(Mensagem.Tipo.SUCESSO, ("Cadastro: " + novos.length + " aluno(s)").getBytes());
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
            for (Funcionario f : novos) if (f != null) academia.adicionarFuncionario(f);
            return new Mensagem(Mensagem.Tipo.SUCESSO, ("Cadastro: " + novos.length + " funcionário(s)").getBytes());
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