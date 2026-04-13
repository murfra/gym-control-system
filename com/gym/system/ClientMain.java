package com.gym.system;

import com.gym.system.model.Aluno;
import com.gym.system.model.Funcionario;
import com.gym.system.model.Instrutor;
import com.gym.system.model.Visitante;
import com.gym.system.model.enums.Experiencia;
import com.gym.system.model.enums.Turno;
import com.gym.system.network.client.AcademiaClient;

import java.time.LocalDateTime;

public class ClientMain {

    public static void main(String[] args) {
        try {
            AcademiaClient client = new AcademiaClient("localhost", 12345);
            client.conectar();

            // ===== TESTE VISITANTE =====
            Visitante[] visitantes = {
                    new Visitante("111.222.333-44", "Ana Silva", null, null,
                            "ana@gym.com", null, LocalDateTime.now(), Experiencia.INICIANTE)
            };

            System.out.println(client.cadastrarVisitantes(visitantes));

            Visitante[] visitantesListados = client.listarVisitantes();
            System.out.println("Visitantes cadastrados:");
            for (Visitante v : visitantesListados) {
                System.out.println(v.getNome() + " | " + v.getEmail());
            }

            // ===== TESTE INSTRUTOR =====
            Instrutor[] instrutores = {
                    new Instrutor("555.666.777-88", "123456-G/CE", "João Augusto",
                            null, null, "joao@gym.com", null, "123", 2500f, Turno.MANHA)
            };

            System.out.println(client.cadastrarInstrutores(instrutores));

            Instrutor[] instrutoresListados = client.listarInstrutores();
            System.out.println("Instrutores cadastrados:");
            for (Instrutor i : instrutoresListados) {
                System.out.println(i.getNome() + " | " + i.getCref());
            }

            // ===== TESTE ALUNO =====
            Aluno[] alunos = {
                    new Aluno("999.888.777-66", "Carlos Lima", null, null,
                            "carlos@gym.com", null, Experiencia.INTERMEDIARIO)
            };

            System.out.println(client.cadastrarAlunos(alunos));

            Aluno[] alunosListados = client.listarAlunos();
            System.out.println("Alunos cadastrados:");
            for (Aluno a : alunosListados) {
                System.out.println(a.getNome() + " | " + a.getNivelExperiencia());
            }

            // ===== TESTE FUNCIONARIO =====
            Funcionario[] funcionarios = {
                    new Funcionario("222.333.444-55", "Fernanda Costa", null, null,
                            "fernanda@gym.com", null, "abc123", 2200f, Turno.NOITE)
            };

            System.out.println(client.cadastrarFuncionarios(funcionarios));

            Funcionario[] funcionariosListados = client.listarFuncionarios();
            System.out.println("Funcionários cadastrados:");
            for (Funcionario f : funcionariosListados) {
                System.out.println(f.getNome() + " | " + f.getTurno());
            }

            client.desconectar();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
