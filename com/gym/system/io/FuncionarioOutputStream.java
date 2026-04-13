package com.gym.system.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.system.model.Funcionario;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class FuncionarioOutputStream extends OutputStream {
    private Funcionario[] funcionarios;
    private int quantidade;
    private int bytesPorAtributo;
    private OutputStream out;

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public FuncionarioOutputStream(Funcionario[] funcionarios, int quantidade, int bytesPorAtributo, OutputStream out) {
        this.funcionarios = funcionarios;
        this.quantidade = quantidade;
        this.bytesPorAtributo = bytesPorAtributo;
        this.out = out;
    }

    public void writeSystem() throws IOException {
        PrintStream opLocal = new PrintStream(this.out);

        int qtdFuncionarios = funcionarios.length;
        opLocal.println("Quantidade de Funcionários: " + qtdFuncionarios);

        for (Funcionario funcionario : funcionarios) {
            if (funcionario == null) continue;

            opLocal.println("CPF: " + funcionario.getCpf() +
                    "\nNome: " + funcionario.getNome() +
                    "\nTelefone: " + funcionario.getTelefone() +
                    "\nEmail: " + funcionario.getEmail() +
                    "\nSalário: " + funcionario.getSalario() +
                    "\nTurno: " + funcionario.getTurno() +
                    "\n---FIM_FUNCIONARIO---");
        }
    }

    private void writeJSON() throws IOException {
        int limite = Math.min(quantidade, funcionarios.length);
        Funcionario[] subArray = new Funcionario[limite];
        System.arraycopy(funcionarios, 0, subArray, 0, limite);

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
        return "FuncionarioOutputStream{" +
                "funcionarios=" + Arrays.toString(funcionarios) +
                ", quantidade=" + quantidade +
                ", bytesPorAtributo=" + bytesPorAtributo +
                ", out=" + out +
                '}';
    }
}