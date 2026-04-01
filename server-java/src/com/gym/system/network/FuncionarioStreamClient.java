package com.gym.system.network;

import com.gym.system.io.FuncionarioOutputStream;
import com.gym.system.model.Funcionario;
import com.gym.system.model.Turno;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class FuncionarioStreamClient {
    public static void main(String[] args) {
        Funcionario f1 = new Funcionario(333, 9090, "Funcionario 1", null,
                "fun1@email.com", null, "abc", 1600, Turno.MANHA);

        Funcionario f2 = new Funcionario(444, 7070, "Funcionario 2", null,
                "fun2@email.com", null, "def", 1650, Turno.NOITE);

        Funcionario[] funcionarios = {f1, f2};
        int qtdParaEnviar = funcionarios.length;
        int bytesPorAtributo = 30;

        // TESTE 1: Saída Padrão (System.out)
        try {
            FuncionarioOutputStream fosConsole = new FuncionarioOutputStream(funcionarios, qtdParaEnviar, bytesPorAtributo, System.out);
            fosConsole.enviarDados();
            System.out.println("\n[OK] Visualização no console concluída.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // TESTE 2: Arquivo (FileOutputStream)
        try (FileOutputStream fos = new FileOutputStream("funcionarios_saida.txt")) {
            FuncionarioOutputStream fosFile = new FuncionarioOutputStream(funcionarios, qtdParaEnviar, bytesPorAtributo, fos);
            fosFile.enviarDados();
            System.out.println("[OK] Arquivo gerado com sucesso.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // TESTE 3: Servidor TCP (Socket)
        // O SERVIDOR PRECISA ESTAR RODANDO ANTES DESTA PARTE
        try (Socket socket = new Socket("localhost", 12345)) {
            FuncionarioOutputStream fosSocket = new FuncionarioOutputStream(funcionarios, qtdParaEnviar, bytesPorAtributo, socket.getOutputStream());
            fosSocket.enviarDados();
            System.out.println("[OK] Dados despachados para o servidor.");
        } catch (IOException e) {
            System.err.println("[ERRO] Não foi possível conectar ao servidor. Ele está rodando?");
        }
    }
}
