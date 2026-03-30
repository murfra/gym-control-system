package com.gym.system.network;

import com.gym.system.io.AlunoInputStream;
import com.gym.system.io.FuncionarioInputStream;
import com.gym.system.model.Aluno;
import com.gym.system.model.Funcionario;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class AcademiaServer {
    public static void main(String[] args) {
        int porta = 12345;
        int bytesPorAtributo = 30;

        try (ServerSocket server = new ServerSocket(porta)) {
            System.out.println("[SERVIDOR] Aguardando conexão na porta " + porta + "...");

            try (Socket cliente = server.accept()) {
                System.out.println("[SERVIDOR] Cliente conectado: " + cliente.getInetAddress());

                int tipo = cliente.getInputStream().read();

                if (tipo == 1) {
                    // Usando o InputStream implementado
                    AlunoInputStream ais = new AlunoInputStream(cliente.getInputStream(), bytesPorAtributo);

                    // Vamos tentar ler 2 alunos que o cliente enviará
                    List<Aluno> recebidos = ais.lerAlunos(2);

                    System.out.println("[SERVIDOR] Dados recebidos com sucesso:");
                    for (Aluno a : recebidos) {
                        System.out.println("CPF: " + a.getCpf() + ", Nome: " + a.getNome() + ", E-mail: " + a.getEmail() +
                                ", Matrícula: " + a.getNumeroMatricula());
                    }
                } else if (tipo == 2) {
                    // Usando o InputStream implementado
                    FuncionarioInputStream fis = new FuncionarioInputStream(cliente.getInputStream(), bytesPorAtributo);

                    // Vamos tentar ler 2 funcionarios que o cliente enviará
                    List<Funcionario> recebidos = fis.lerFuncionarios(2);

                    System.out.println("[SERVIDOR] Dados recebidos com sucesso:");
                    for (Funcionario f : recebidos) {
                        System.out.println("CPF: " + f.getCpf() + ", Nome: " + f.getNome() + ", Salário: " + f.getSalario());
                    }
                }

            }
        } catch (Exception e) {
            System.err.println("[SERVIDOR] Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
