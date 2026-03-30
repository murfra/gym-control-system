package com.gym.system.network;

import com.gym.system.io.AlunoOutputStream;
import com.gym.system.io.FuncionarioOutputStream;
import com.gym.system.model.Aluno;
import com.gym.system.model.Experiencia;
import com.gym.system.model.Funcionario;
import com.gym.system.model.Turno;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class AcademiaClient {
    public static void main(String[] args) {
        testarAlunos();
        testarFuncionarios();
    }

    private static void testarAlunos() {
        System.out.println("\n=== Testando Alunos ===");
        // Preparando dados de teste
        Aluno a1 = new Aluno(111, 9991, "Aluno 1", null, "aluno1@email.com", null, Experiencia.INICIANTE);
        Aluno a2 = new Aluno(222, 9992, "Aluno 2", null, "aluno2@email.com", null, Experiencia.INTERMEDIARIO);
        Aluno[] lista = { a1, a2 };

        int qtdParaEnviar = 2;
        int bytesPorAtributo = 30;

        System.out.println("\n=== INICIANDO TESTES ===");

        // Teste: Saída Padrão (System.out)
        System.out.println("\n--- Enviando para System.out ---");
        try {
            AlunoOutputStream aosConsole = new AlunoOutputStream(lista, qtdParaEnviar, bytesPorAtributo, System.out);
            aosConsole.enviarDados();
            System.out.println("\n[OK] Visualização no console concluída.");
        } catch (IOException e) { e.printStackTrace(); }


        // Teste: Arquivo (FileOutputStream)
        System.out.println("\n--- Gravando em 'alunos_saida.txt' ---");
        try (FileOutputStream fos = new FileOutputStream("alunos_saida.txt")) {
            AlunoOutputStream aosFile = new AlunoOutputStream(lista, qtdParaEnviar, bytesPorAtributo, fos);
            aosFile.enviarDados();
            System.out.println("[OK] Arquivo gerado com sucesso.");
        } catch (IOException e) { e.printStackTrace(); }


        // Teste: Servidor TCP (Socket)
        System.out.println("\n--- Enviando para Servidor TCP ---");
        // O SERVIDOR PRECISA ESTAR RODANDO ANTES DESTA PARTE
        try (Socket socket = new Socket("localhost", 12345)) {
            OutputStream out = socket.getOutputStream();
            out.write(1); // Enviando byte para verificação
            out.flush();

            AlunoOutputStream aosSocket = new AlunoOutputStream(lista, qtdParaEnviar, bytesPorAtributo, socket.getOutputStream());
            aosSocket.enviarDados();
            System.out.println("[OK] Dados despachados para o servidor.");
        } catch (IOException e) {
            System.err.println("[ERRO] Não foi possível conectar ao servidor. Ele está rodando?");
        }

        System.out.println("\n=== FIM DOS TESTES ===");
    }

    private static void testarFuncionarios() {
        System.out.println("\n=== Testando Funcionários ===");
        // Preparando dados de teste
        Funcionario f1 = new Funcionario(333, 9090, "Funcionario 1", null,
                                    "fun1@email.com", null, "abc", 1600, Turno.MANHA);
        Funcionario f2 = new Funcionario(444, 7070, "Funcionario 2", null,
                                    "fun2@email.com", null, "def", 1650, Turno.NOITE);
        Funcionario[] lista = { f1, f2 };

        int qtdParaEnviar = 2;
        int bytesPorAtributo = 30;

        System.out.println("\n=== INICIANDO TESTES ===");

        // Teste: Saída Padrão (System.out)
        System.out.println("\n--- Enviando para System.out ---");
        try {
            FuncionarioOutputStream fosConsole = new FuncionarioOutputStream(lista, qtdParaEnviar, bytesPorAtributo, System.out);
            fosConsole.enviarDados();
            System.out.println("\n[OK] Visualização no console concluída.");
        } catch (IOException e) { e.printStackTrace(); }


        // Teste: Arquivo (FileOutputStream)
        System.out.println("\n--- Gravando em 'funcionarios_saida.txt' ---");
        try (FileOutputStream fos = new FileOutputStream("funcionarios_saida.txt")) {
            FuncionarioOutputStream fosFile = new FuncionarioOutputStream(lista, qtdParaEnviar, bytesPorAtributo, fos);
            fosFile.enviarDados();
            System.out.println("[OK] Arquivo gerado com sucesso.");
        } catch (IOException e) { e.printStackTrace(); }


        // Teste: Servidor TCP (Socket)
        System.out.println("\n--- Enviando para Servidor TCP ---");
        // O SERVIDOR PRECISA ESTAR RODANDO ANTES DESTA PARTE
        try (Socket socket = new Socket("localhost", 12345)) {
            OutputStream out = socket.getOutputStream();
            out.write(2); // Enviando byte para verificação
            out.flush();

            FuncionarioOutputStream fosSocket = new FuncionarioOutputStream(lista, qtdParaEnviar, bytesPorAtributo, socket.getOutputStream());
            fosSocket.enviarDados();
            System.out.println("[OK] Dados despachados para o servidor.");
        } catch (IOException e) {
            System.err.println("[ERRO] Não foi possível conectar ao servidor. Ele está rodando?");
        }

        System.out.println("\n=== FIM DOS TESTES ===");
    }
}

