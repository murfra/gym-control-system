package com.gym.system.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gym.system.models.enums.Turno;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Instrutor extends Funcionario {
    private String cref;
    private List<Aluno> alunos; // Alunos sob a supervisão/consultoria do instrutor

    public Instrutor(String cpf, String nome, LocalDate dataNascimento, String telefone,
                     String email, Endereco endereco, String senha, float salario, Turno turno) {
        super(cpf, nome, dataNascimento, telefone, email, endereco, senha, salario, turno);
        this.alunos = new ArrayList<>();
    }

    public Instrutor(@JsonProperty("cpf") String cpf,
                     @JsonProperty("cref") String cref,
                     @JsonProperty("nome") String nome,
                     @JsonProperty("dataNascimento") LocalDate dataNascimento,
                     @JsonProperty("telefone") String telefone,
                     @JsonProperty("email") String email,
                     @JsonProperty("endereco") Endereco endereco,
                     @JsonProperty("senha") String senha,
                     @JsonProperty("salario") float salario,
                     @JsonProperty("turno") Turno turno) {
        super(cpf, nome, dataNascimento, telefone, email, endereco, senha, salario, turno);
        this.cref = cref;
        this.alunos = new ArrayList<>();
    }

    public String getCref() {
        return cref;
    }

    public void setCref(String cref) {
        this.cref = cref;
    }

    public List<Aluno> getAlunos() {
        return alunos;
    }

    public void setAlunos(List<Aluno> alunos) {
        this.alunos = alunos;
    }
}
