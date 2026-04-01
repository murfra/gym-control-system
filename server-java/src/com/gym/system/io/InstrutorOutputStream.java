package com.gym.system.io;

import com.gym.system.model.Instrutor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class InstrutorOutputStream extends OutputStream {
    private Instrutor[] instrutores;
    private int quantidade;
    private int bytesPorAtributo;
    private OutputStream out;

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
            if (instrutor != null) {
                String cpf = instrutor.getCpf();
                String cref = instrutor.getCref();
                String nome = instrutor.getNome();
                String alunos = instrutor.getAlunos().toString();
                String telefone = instrutor.getTelefone();
                String email = instrutor.getEmail();

                opLocal.println("CPF: " + cpf +
                                "\nCREF: " + cref +
                                "\nNome: " + nome +
                                "\nAlunos: " + alunos +
                                "\nTelefone: " + telefone +
                                "\nE-mail: " + email);
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
        return "InstrutorOutputStream{" +
                "instrutores=" + Arrays.toString(instrutores) +
                ", quantidade=" + quantidade +
                ", bytesPorAtributo=" + bytesPorAtributo +
                ", out=" + out +
                '}';
    }
}
