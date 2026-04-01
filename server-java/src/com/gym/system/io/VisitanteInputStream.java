package com.gym.system.io;

import com.gym.system.model.Experiencia;
import com.gym.system.model.Visitante;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Scanner;

public class VisitanteInputStream extends InputStream {
    private Visitante[] visitantes;
    private InputStream in;

    public VisitanteInputStream(Visitante[] visitantes, InputStream in) {
        this.in = in;
        this.visitantes = visitantes;
    }

    private void processStream() {
        Scanner sc = new Scanner(this.in);

        try {
            // Primeira linha no Output: Quantidade de visitantes
            int qnt = Integer.parseInt(sc.nextLine().split(":")[1].trim());
            this.visitantes = new Visitante[qnt];

            for (int i = 0; i < qnt; i++) {
                String cpf = sc.nextLine().split(":")[1].trim();
                String nome = sc.nextLine().split(":")[1].trim();
                String email = sc.nextLine().split(":")[1].trim();
                LocalDateTime dataVisita = LocalDateTime.parse(sc.nextLine().split(":")[1].trim());
                Experiencia nivelExperiencia = Experiencia.valueOf(sc.nextLine().split(":")[1].trim());

                this.visitantes[i] = new Visitante(cpf, nome, null, null, email,
                        null, dataVisita, nivelExperiencia);
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar os dados: " + e.getMessage());
        }
    }

    public Visitante[] readSystem() {
        Scanner sc = new Scanner(in);
        System.out.println("Informe a quantidade de Visitantees a ser lido:");
        int qnt = Integer.parseInt(sc.nextLine());

        this.visitantes = new Visitante[qnt];

        for (int i = 0; i < qnt; i++) {
            System.out.println("Informe o CPF do Visitante:");
            String cpf = sc.nextLine();
            System.out.println("Informe o Nome do Visitante:");
            String nome = sc.nextLine();
            System.out.println("Informe o E-mail do Visitante:");
            String email = sc.nextLine();
            System.out.println("Informe a data de visita do Visitante:");
            LocalDateTime dataVisita = LocalDateTime.parse(sc.nextLine());
            System.out.println("Informe o nível de experiência do Visitante:");
            Experiencia nivelExperiencia = Experiencia.valueOf(sc.nextLine());

            this.visitantes[i] = new Visitante(cpf, nome, null, null, email,
                    null, dataVisita, nivelExperiencia);
        }

        return this.visitantes;
    }

    public Visitante[] readFile() {
        processStream();
        return this.visitantes;
    }

    public Visitante[] readTCP() {
        processStream();
        return this.visitantes;
    }

    @Override
    public int read() throws IOException {
        return (this.in != null) ? this.in.read() : -1;
    }
}
