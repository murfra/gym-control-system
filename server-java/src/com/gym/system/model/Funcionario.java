package com.gym.system.model;

import com.gym.system.model.enums.Turno;

import java.time.LocalDate;

public class Funcionario extends Pessoa {
    private String senha; // Senha para acessar o sistema
    private float salario;
    private Turno turno;

    public Funcionario(String cpf, String nome, LocalDate dataNascimento, String telefone,
                       String email, Endereco endereco, String senha, float salario, Turno turno) {
        super(cpf, nome, dataNascimento, telefone, email, endereco);
        this.senha = senha;
        this.salario = salario;
        this.turno = turno;
    }

    public String getSenha() {
        return senha; // Apenas para testes
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public float getSalario() {
        return salario;
    }

    public void setSalario(float salario) {
        this.salario = salario;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }
}
