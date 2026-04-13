package com.gym.system.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.system.model.Visitante;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class VisitanteOutputStream extends OutputStream {
    private Visitante[] visitantes;
    private int quantidade;
    private int bytesPorAtributo;
    private OutputStream out;

    // ObjectMapper thread-safe e configurado para datas ISO-8601
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public VisitanteOutputStream(Visitante[] visitantes, int quantidade, int bytesPorAtributo, OutputStream out) {
        this.visitantes = visitantes;
        this.quantidade = quantidade;
        this.bytesPorAtributo = bytesPorAtributo;
        this.out = out;
    }

    // Formato legível para console
    public void writeSystem() throws IOException {
        PrintStream opLocal = new PrintStream(this.out);

        int qtdVisitantes = visitantes.length;
        opLocal.println("Quantidade de Visitantes: " + qtdVisitantes);

        for (Visitante v : visitantes) {
            if (v == null) continue;

            opLocal.println("CPF: " + v.getCpf() +
                            "\nNome: " + v.getNome() +
                            "\nEmail: " + v.getEmail() +
                            "\nDataVisita: " + v.getDataVisita() +
                            "\nExperiência: " + v.getNivelExperiencia().name() +
                            "\n---FIM_VISITANTE---");
        }
    }

    private void writeJSON() throws IOException {
        int limite = Math.min(quantidade, visitantes.length);
        Visitante[] subArray = new Visitante[limite];
        System.arraycopy(visitantes, 0, subArray, 0, limite);

        // Serializa array para JSON e grava como bytes UTF-8
        byte[] jsonBytes = mapper.writeValueAsString(subArray).getBytes(StandardCharsets.UTF_8);
        out.write(jsonBytes);
        out.flush();
    }

    // Serializa como JSON (UTF-8 bytes)
    public void writeFile() throws IOException {
        writeJSON();
    }

    // Serializa como JSON (UTF-8 bytes)
    public void writeTCP() throws IOException {
        writeJSON();
    }

    @Override
    public void write(int b) throws IOException {
        if (this.out != null) {
            this.out.write(b);
        }
    }

    @Override
    public String toString() {
        return "VisitanteOutputStream{" +
                "visitantes=" + Arrays.toString(visitantes) +
                ", quantidade=" + quantidade +
                ", bytesPorAtributo=" + bytesPorAtributo +
                ", out=" + out +
                '}';
    }
}
