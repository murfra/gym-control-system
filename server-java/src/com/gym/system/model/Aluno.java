package com.gym.system.model;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

public class Aluno extends Pessoa {
    // AtomicInteger é utilizado para caso haja concorrência (multithreading)
    private static final AtomicInteger contadorMatricula = new AtomicInteger(1);

    private int numeroMatricula;
    private LocalDate dataMatricula;
    private LocalDate dataUltimoPagamento;

    public Aluno(int cpf, int telefone, String nome, LocalDate dataNascimento, String email, Endereco endereco) {
        this.numeroMatricula = contadorMatricula.getAndIncrement();
        this.dataMatricula = LocalDate.now();
        super(cpf, telefone, nome, dataNascimento, email, endereco);
    }

    public int getNumeroMatricula() {
        return numeroMatricula;
    }

    public LocalDate getDataMatricula() {
        return dataMatricula;
    }

    public LocalDate getDataUltimoPagamento() {
        return dataUltimoPagamento;
    }

    public void setDataUltimoPagamento(LocalDate dataUltimoPagamento) {
        this.dataUltimoPagamento = dataUltimoPagamento;
    }
}
