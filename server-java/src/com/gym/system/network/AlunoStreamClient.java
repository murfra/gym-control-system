package com.gym.system.network;

import com.gym.system.io.AlunoOutputStream;
import com.gym.system.model.Aluno;
import com.gym.system.model.enums.Experiencia;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class AlunoStreamClient {
    public static void main() {
        System.out.println("\n=== Testando Alunos ===");
        // Preparando dados de teste
        Aluno a1 = new Aluno(111, 9991, "Aluno 1", null, "aluno1@email.com", null, Experiencia.INICIANTE);
        Aluno a2 = new Aluno(222, 9992, "Aluno 2", null, "aluno2@email.com", null, Experiencia.INTERMEDIARIO);

        Aluno[] lista = {a1, a2};
        int qtdParaEnviar = lista.length;
        int bytesPorAtributo = 30;

        System.out.println("\n=== INICIANDO TESTES ===");

        // TESTE 1: Saída Padrão (System.out)
        System.out.println("\n--- Enviando para System.out ---");
        try {
            AlunoOutputStream aosConsole = new AlunoOutputStream(lista, qtdParaEnviar, bytesPorAtributo, System.out);
            aosConsole.enviarDados();
            System.out.println("\n[OK] Visualização no console concluída.");
        } catch (IOException e) {
            e.printStackTrace();
        }


        // TESTE 2: Arquivo (FileOutputStream)
        System.out.println("\n--- Gravando em 'alunos_saida.txt' ---");
        try (FileOutputStream fos = new FileOutputStream("alunos_saida.txt")) {
            AlunoOutputStream aosFile = new AlunoOutputStream(lista, qtdParaEnviar, bytesPorAtributo, fos);
            aosFile.enviarDados();
            System.out.println("[OK] Arquivo gerado com sucesso.");
        } catch (IOException e) {
            e.printStackTrace();
        }


        // TESTE 3: Servidor TCP (Socket)
        System.out.println("\n--- Enviando para Servidor TCP ---");
        // O SERVIDOR PRECISA ESTAR RODANDO ANTES DESTA PARTE
        try (Socket socket = new Socket("localhost", 12345)) {
            AlunoOutputStream aosSocket = new AlunoOutputStream(lista, qtdParaEnviar, bytesPorAtributo, socket.getOutputStream());
            aosSocket.enviarDados();
            System.out.println("[OK] Dados despachados para o servidor.");
        } catch (IOException e) {
            System.err.println("[ERRO] Não foi possível conectar ao servidor. Ele está rodando?");
        }

        System.out.println("\n=== FIM DOS TESTES ===");
    }
}
