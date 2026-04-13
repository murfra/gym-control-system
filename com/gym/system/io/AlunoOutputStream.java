package com.gym.system.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.system.model.Aluno;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AlunoOutputStream extends OutputStream {
    private Aluno[] alunos;
    private int quantidade;
    private int bytesPorAtributo;
    private OutputStream out;

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public AlunoOutputStream(Aluno[] alunos, int quantidade, int bytesPorAtributo, OutputStream out) {
        this.alunos = alunos;
        this.quantidade = quantidade;
        this.bytesPorAtributo = bytesPorAtributo;
        this.out = out;
    }

    public void writeSystem() throws IOException {
        PrintStream opLocal = new PrintStream(this.out);

        int qtdAlunos = alunos.length;
        opLocal.println("Quantidade de Alunos: " + qtdAlunos);

        for (Aluno aluno : alunos) {
            if (aluno == null) continue;

            opLocal.println("CPF: " + aluno.getCpf() +
                    "\nNome: " + aluno.getNome() +
                    "\nEmail: " + aluno.getEmail() +
                    "\nExperiência: " + aluno.getNivelExperiencia() +
                    "\n---FIM_ALUNO---");
        }
    }

    private void writeJSON() throws IOException {
        int limite = Math.min(quantidade, alunos.length);
        Aluno[] subArray = new Aluno[limite];
        System.arraycopy(alunos, 0, subArray, 0, limite);

        byte[] jsonBytes = mapper.writeValueAsString(subArray).getBytes(StandardCharsets.UTF_8);
        out.write(jsonBytes);
        out.flush();
    }

    public void writeFile() throws IOException {
        writeJSON();
    }

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
        return "AlunoOutputStream{" +
                "alunos=" + Arrays.toString(alunos) +
                ", quantidade=" + quantidade +
                ", bytesPorAtributo=" + bytesPorAtributo +
                ", out=" + out +
                '}';
    }
}