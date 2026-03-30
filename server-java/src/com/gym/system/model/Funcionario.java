package com.gym.system.model;

import java.time.LocalDate;

public class Funcionario extends Pessoa {
    private String senha; // Senha para acessar o sistema
    private float salario;
    private Turno turno;

    public Funcionario(int cpf, int telefone, String nome, LocalDate dataNascimento,
                       String email, Endereco endereco, String senha, float salario, Turno turno) {
        this.senha = senha;
        this.salario = salario;
        this.turno = turno;
        super(cpf, telefone, nome, dataNascimento, email, endereco);
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
