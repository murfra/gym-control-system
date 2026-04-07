package com.gym.system.model;

import com.gym.system.model.enums.Turno;

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

    public Instrutor(String cpf, String cref, String nome, LocalDate dataNascimento, String telefone,
                     String email, Endereco endereco, String senha, float salario, Turno turno) {
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
