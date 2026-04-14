package com.gym.system.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gym.system.models.enums.Turno;

import java.time.LocalDate;

public class Funcionario extends Pessoa {
    private String senha;
    private float salario;
    private Turno turno;

    public Funcionario(@JsonProperty("cpf") String cpf,
                       @JsonProperty("nome") String nome,
                       @JsonProperty("dataNascimento") LocalDate dataNascimento,
                       @JsonProperty("telefone") String telefone,
                       @JsonProperty("email") String email,
                       @JsonProperty("endereco") Endereco endereco,
                       @JsonProperty("senha") String senha,
                       @JsonProperty("salario") float salario,
                       @JsonProperty("turno") Turno turno) {
        super(cpf, nome, dataNascimento, telefone, email, endereco);
        this.senha = senha;
        this.salario = salario;
        this.turno = turno;
    }

    public String getSenha() {
        return senha;
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