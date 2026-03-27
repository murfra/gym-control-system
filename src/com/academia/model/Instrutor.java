package com.academia.model;

import java.time.LocalDate;

public class Instrutor extends Pessoa {

    public Instrutor(int cpf, int telefone, String nome, LocalDate dataNascimento, String email, Endereco endereco) {
        super(cpf, telefone, nome, dataNascimento, email, endereco);
    }
}
