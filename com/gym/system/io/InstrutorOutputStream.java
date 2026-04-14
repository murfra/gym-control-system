package com.gym.system.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.system.models.Instrutor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class InstrutorOutputStream extends OutputStream {
    private Instrutor[] instrutores;
    private int quantidade;
    private int bytesPorAtributo;
    private OutputStream out;

    // ObjectMapper thread-safe e configurado para datas ISO-8601
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public InstrutorOutputStream(Instrutor[] instrutores, int quantidade, int bytesPorAtributo, OutputStream out) {
        this.instrutores = instrutores;
        this.quantidade = quantidade;
        this.bytesPorAtributo = bytesPorAtributo;
        this.out = out;
    }

    public void writeSystem() throws IOException {
        PrintStream opLocal = new PrintStream(this.out);

        int qtdInstrutores = instrutores.length;
        opLocal.println("Quantidade de Instrutores: " + qtdInstrutores);

        for (Instrutor instrutor : instrutores) {
            if (instrutor == null) continue;

            opLocal.println("CPF: " + instrutor.getCpf() +
                            "\nCREF: " + instrutor.getCref() +
                            "\nNome: " + instrutor.getNome() +
                            "\nAlunos: " + instrutor.getAlunos().toString() +
                            "\nTelefone: " + instrutor.getTelefone() +
                            "\nEmail: " + instrutor.getEmail() +
                            "\n---FIM_INSTRUTOR---");
        }
    }

    private void writeJSON() throws IOException {
        int limite = Math.min(quantidade, instrutores.length);
        Instrutor[] subArray = new Instrutor[limite];
        System.arraycopy(instrutores, 0, subArray, 0, limite);

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
        return "InstrutorOutputStream{" +
                "instrutores=" + Arrays.toString(instrutores) +
                ", quantidade=" + quantidade +
                ", bytesPorAtributo=" + bytesPorAtributo +
                ", out=" + out +
                '}';
    }
}
