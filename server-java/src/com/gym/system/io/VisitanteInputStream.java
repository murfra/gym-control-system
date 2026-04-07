package com.gym.system.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.system.model.enums.Experiencia;
import com.gym.system.model.Visitante;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Scanner;

public class VisitanteInputStream extends InputStream {
    private Visitante[] visitantes;
    private InputStream in;

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public VisitanteInputStream(Visitante[] visitantes, InputStream in) {
        this.in = in;
        this.visitantes = visitantes;
    }

    // Leitura interativa do terminal
    public Visitante[] readSystem() {
        Scanner sc = new Scanner(in);
        System.out.println("Quantidade de Visitantes:");
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

    private Visitante[] readJSON(InputStream source) throws IOException {
        String json = new String(source.readAllBytes(), StandardCharsets.UTF_8);
        return mapper.readValue(json, Visitante[].class);
    }

    // Lê JSON do arquivo ou TCP
    public Visitante[] readFile() throws IOException {
        return readJSON(in);
    }

    // Lê JSON do arquivo ou TCP
    public Visitante[] readTCP() throws IOException {
        return readJSON(in);
    }

    @Override
    public int read() throws IOException {
        return (this.in != null) ? this.in.read() : -1;
    }
}
