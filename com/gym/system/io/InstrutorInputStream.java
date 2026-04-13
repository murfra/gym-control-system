package com.gym.system.io;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.system.model.Aluno;
import com.gym.system.model.Instrutor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InstrutorInputStream extends InputStream {
    private Instrutor[] instrutores;
    private InputStream in;

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);

    public InstrutorInputStream(InputStream in) {
        this.in = in;
    }

    public Instrutor[] readSystem() {
        Scanner sc = new Scanner(in);
        System.out.println("Informe a quantidade de Instrutores a ser lido:");
        int qnt = Integer.parseInt(sc.nextLine());

        this.instrutores = new Instrutor[qnt];

        for (int i = 0; i < qnt; i++) {
            System.out.println("Informe o CPF do Instrutor:");
            String cpf = sc.nextLine();
            System.out.println("Informe o CREF do Instrutor:");
            String cref = sc.nextLine();
            System.out.println("Informe o Nome do Instrutor:");
            String nome = sc.nextLine();
            System.out.println("Informe os Alunos do Instrutor: (Vazio, por enquanto)");
            List<Aluno> alunos = new ArrayList<>();
            System.out.println("Informe o Telefone do Instrutor:");
            String telefone = sc.nextLine();
            System.out.println("Informe o E-mail do Instrutor:");
            String email = sc.nextLine();

            instrutores[i] = new Instrutor(cpf, cref, nome, null, telefone, email,
                    null, null, 0, null);
        }

        return this.instrutores;
    }

    private Instrutor[] readJSON(InputStream source) throws IOException {
        String json = new String(source.readAllBytes(), StandardCharsets.UTF_8);
        return mapper.readValue(json, Instrutor[].class);
    }

    public Instrutor[] readFile() throws IOException {
        return readJSON(in);
    }

    public Instrutor[] readTCP() throws IOException {
        return readJSON(in);
    }

    @Override
    public int read() throws IOException {
        return (this.in != null) ? this.in.read() : -1;
    }
}
