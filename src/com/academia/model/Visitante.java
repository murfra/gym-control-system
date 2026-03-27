package com.academia.model;

import java.time.LocalDate;

public class Visitante extends Pessoa {
    private LocalDate dataVisita;

    public Visitante(int cpf, int telefone, String nome, LocalDate dataNascimento, String email, Endereco endereco) {
        this.dataVisita = LocalDate.now();
        super(cpf, telefone, nome, dataNascimento, email, endereco);
    }

    public LocalDate getDataVisita() {
        return dataVisita;
    }
}
