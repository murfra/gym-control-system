package com.gym.system.io;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.system.model.Aluno;
import com.gym.system.model.enums.Experiencia;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Scanner;

public class AlunoInputStream extends InputStream {
    private Aluno[] alunos;
    private InputStream in;

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);

    public AlunoInputStream(InputStream in) {
        this.in = in;
    }

    public Aluno[] readSystem() {
        Scanner sc = new Scanner(in);
        System.out.println("Quantidade de Alunos:");
        int qnt = Integer.parseInt(sc.nextLine());

        this.alunos = new Aluno[qnt];

        for (int i = 0; i < qnt; i++) {
            System.out.println("Informe o CPF do Aluno:");
            String cpf = sc.nextLine();

            System.out.println("Informe o Nome do Aluno:");
            String nome = sc.nextLine();

            System.out.println("Informe a Data de Nascimento do Aluno (yyyy-MM-dd) ou deixe vazio:");
            String dataStr = sc.nextLine();
            LocalDate dataNascimento = dataStr.isBlank() ? null : LocalDate.parse(dataStr);

            System.out.println("Informe o Telefone do Aluno:");
            String telefone = sc.nextLine();

            System.out.println("Informe o E-mail do Aluno:");
            String email = sc.nextLine();

            System.out.println("Informe o nível de experiência do Aluno (iniciante, intermediario ou avancado):");
            Experiencia nivelExperiencia = Experiencia.valueOf(sc.nextLine().toUpperCase());

            alunos[i] = new Aluno(cpf, nome, dataNascimento, telefone, email, null, nivelExperiencia);
        }

        return alunos;
    }

    private Aluno[] readJSON(InputStream source) throws IOException {
        String json = new String(source.readAllBytes(), StandardCharsets.UTF_8);
        return mapper.readValue(json, Aluno[].class);
    }

    public Aluno[] readFile() throws IOException {
        return readJSON(in);
    }

    public Aluno[] readTCP() throws IOException {
        return readJSON(in);
    }

    @Override
    public int read() throws IOException {
        return (this.in != null) ? this.in.read() : -1;
    }
}