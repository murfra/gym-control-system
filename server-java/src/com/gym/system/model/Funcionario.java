package com.gym.system.model;

import java.time.LocalDate;

public class Funcionario extends Pessoa {

    public Funcionario(int cpf, int telefone, String nome, LocalDate dataNascimento, String email, Endereco endereco) {
        super(cpf, telefone, nome, dataNascimento, email, endereco);
    }
}
