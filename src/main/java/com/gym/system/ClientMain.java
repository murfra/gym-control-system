package com.gym.system;

import com.gym.system.models.Aluno;
import com.gym.system.models.Funcionario;
import com.gym.system.models.Instrutor;
import com.gym.system.models.Visitante;
import com.gym.system.models.enums.Experiencia;
import com.gym.system.models.enums.Turno;
import com.gym.system.network.client.AcademiaClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        try {
            AcademiaClient client = new AcademiaClient("localhost", 12345);
            client.conectar();
            Scanner sc = new Scanner(System.in);

            // ==========================================
            // 1. TESTES CRUD (MANTIDOS EXATAMENTE)
            // ==========================================
            System.out.println("=== INICIANDO TESTES CRUD ===\n");

            Visitante[] visitantes = {
                    new Visitante("111.222.333-44", "Ana Silva", null, null,
                            "ana@gym.com", null, LocalDateTime.now(), Experiencia.INICIANTE)
            };
            System.out.println(client.cadastrarVisitantes(visitantes));
            Visitante[] vList = client.listarVisitantes();
            System.out.println("Visitantes cadastrados:");
            for (Visitante v : vList) System.out.println(v.getNome() + " | " + v.getEmail());

            Instrutor[] instrutores = {
                    new Instrutor("555.666.777-88", "123456-G/CE", "João Augusto",
                            null, null, "joao@gym.com", null, "123", 2500f, Turno.MANHA)
            };
            System.out.println(client.cadastrarInstrutores(instrutores));
            Instrutor[] iList = client.listarInstrutores();
            System.out.println("Instrutores cadastrados:");
            for (Instrutor i : iList) System.out.println(i.getNome() + " | " + i.getCref());

            Aluno[] alunos = {
                    new Aluno("999.888.777-66", "Carlos Lima", null, null,
                            "carlos@gym.com", null, Experiencia.INTERMEDIARIO)
            };
            System.out.println(client.cadastrarAlunos(alunos));
            Aluno[] aList = client.listarAlunos();
            System.out.println("Alunos cadastrados:");
            for (Aluno a : aList) System.out.println(a.getNome() + " | " + a.getNivelExperiencia());

            Funcionario[] funcionarios = {
                    new Funcionario("222.333.444-55", "Fernanda Costa", null, null,
                            "fernanda@gym.com", null, "abc123", 2200f, Turno.NOITE)
            };
            System.out.println(client.cadastrarFuncionarios(funcionarios));
            Funcionario[] fList = client.listarFuncionarios();
            System.out.println("Funcionários cadastrados:");
            for (Funcionario f : fList) System.out.println(f.getNome() + " | " + f.getTurno());

            System.out.println("\n✅ Testes CRUD finalizados.\n");

            // ==========================================
            // 2. QUESTÃO 5: SISTEMA DE VOTAÇÃO INTERATIVO
            // ==========================================
            System.out.println("🗳️ === INICIANDO SISTEMA DE VOTAÇÃO ===");

            System.out.print("👤 Login para votação (ex: aluno): ");
            String user = sc.next();
            System.out.print("🔑 Senha (ex: 123): ");
            String pass = sc.next();

            if (!client.loginVotacao(user, pass)) {
                System.out.println("❌ Credenciais inválidas. Encerrando...");
                client.desconectar();
                return;
            }

            // Lista as modalidades e mostra o deadline
            client.listarModalidades();

            System.out.print("👉 Digite o ID da modalidade para votar: ");
            int idVoto = sc.nextInt();
            client.votar(idVoto);

            System.out.println("\n⏳ Votação em andamento. O servidor só libera resultados após o prazo expirar.");
            System.out.println("💡 Enquanto espera, observe os avisos multicast aparecendo no console!");

            // Loop interativo para tentar ver os resultados
            while (true) {
                System.out.print("\n> Digite 'resultados' para tentar ver o placar ou 'sair' para encerrar: ");
                String cmd = sc.next();

                if ("resultados".equalsIgnoreCase(cmd)) {
                    try {
                        client.verResultados();
                        System.out.println("\n🎉 Votação concluída com sucesso!");
                        break;
                    } catch (IOException e) {
                        System.out.println("⏳ " + e.getMessage() + " (Aguarde mais um pouco ou tente novamente)");
                    }
                } else if ("sair".equalsIgnoreCase(cmd)) {
                    break;
                }
            }

            client.desconectar();

        } catch (Exception e) {
            System.err.println("❌ Erro geral no cliente:");
            e.printStackTrace();
        }
    }
}