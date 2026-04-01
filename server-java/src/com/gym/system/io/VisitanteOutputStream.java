package com.gym.system.io;

import com.gym.system.model.Visitante;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class VisitanteOutputStream extends OutputStream {
    private Visitante[] visitantes;
    private int quantidade;
    private int bytesPorAtributo;
    private OutputStream out;

    public VisitanteOutputStream(Visitante[] visitantes, int quantidade, int bytesPorAtributo, OutputStream out) {
        this.visitantes = visitantes;
        this.quantidade = quantidade;
        this.bytesPorAtributo = bytesPorAtributo;
        this.out = out;
    }

    public void writeSystem() throws IOException {
        PrintStream opLocal = new PrintStream(out);

        int qtdVisitantes = visitantes.length;
        opLocal.println("Quantidade de Visitantes: " + qtdVisitantes);

        for (Visitante visitante : visitantes) {
            if (visitante != null) {
                String cpf = visitante.getCpf();
                String nome = visitante.getNome();
                String email = visitante.getEmail();
                String dataVisita = visitante.getDataVisita().toString();
                String nivelExperiencia = visitante.getNivelExperiencia().name();

                opLocal.println("CPF: " + cpf +
                                "\nNome: " + nome +
                                "\nE-mail: " + email +
                                "\nData de Visita: " + dataVisita +
                                "\nNível de Experiência: " + nivelExperiencia);
            }
        }

    }

    public void writeFile() throws IOException {
        writeSystem();
    }

    public void writeTCP() throws IOException {
        writeSystem();
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
