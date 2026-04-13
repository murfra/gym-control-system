package com.gym.system.network.client;

import com.gym.system.io.*;
import com.gym.system.model.Instrutor;
import com.gym.system.model.Visitante;
import com.gym.system.network.protocol.Mensagem;

import com.gym.system.model.Aluno;
import com.gym.system.model.Funcionario;

import java.io.*;
import java.net.*;

public class AcademiaClient {
    private final String host;
    private final int port;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public AcademiaClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void conectar() throws IOException {
        socket = new Socket(host, port);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        System.out.println("✅ Conectado ao servidor em " + host + ":" + port);
    }

    public void desconectar() throws IOException {
        if (socket != null && !socket.isClosed()) socket.close();
        System.out.println("🔌 Desconectado.");
    }

    /** Método base de empacotamento/envio e desempacotamento/recebimento */
    private Mensagem enviar(Mensagem req) throws IOException {
        // Empacotar request
        dos.writeUTF(req.getTipo().name());
        dos.writeInt(req.getPayload() != null ? req.getPayload().length : 0);
        if (req.getPayload() != null) dos.write(req.getPayload());
        dos.flush();

        // Desempacotar reply
        String tipoStr = dis.readUTF();
        int len = dis.readInt();
        byte[] payload = new byte[len];
        dis.readFully(payload);
        return new Mensagem(Mensagem.Tipo.valueOf(tipoStr), payload);
    }

    // ===== OPERAÇÕES DE NEGÓCIO =====
    public String cadastrarVisitantes(Visitante[] visitantes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        VisitanteOutputStream vos = new VisitanteOutputStream(visitantes, visitantes.length, 64, baos);
        vos.writeTCP(); // Empacota usando SEU stream

        Mensagem resp = enviar(new Mensagem(Mensagem.Tipo.CADASTRAR_VISITANTE, baos.toByteArray()));
        return new String(resp.getPayload());
    }

    public Visitante[] listarVisitantes() throws IOException {
        Mensagem resp = enviar(new Mensagem(Mensagem.Tipo.LISTAR_VISITANTES, null));
        if (resp.getTipo() == Mensagem.Tipo.ERRO) throw new IOException(new String(resp.getPayload()));

        ByteArrayInputStream bais = new ByteArrayInputStream(resp.getPayload());
        VisitanteInputStream vis = new VisitanteInputStream(bais); // Desempacota usando SEU stream
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