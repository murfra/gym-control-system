package com.gym.system.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public abstract class Pessoa implements Serializable {
    private LocalDate dataAtual = LocalDate.now(ZoneId.systemDefault());
    private int cpf;
    private int telefone;
    private int idade;
    private String nome;
    private String email;
    private Endereco endereco;
    private LocalDate dataNascimento;

    public Pessoa(int cpf, int telefone, String nome, LocalDate dataNascimento, String email, Endereco endereco) {
        this.cpf = cpf;
        this.telefone = telefone;
        //this.idade = Period.between(dataNascimento, dataAtual).getYears();
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.email = email;
        this.endereco = endereco;
    }

    public int getCpf() {
        return cpf;
    }

    public void setCpf(int cpf) {
        this.cpf = cpf;
    }

    public int getTelefone() {
        return telefone;
    }

    public void setTelefone(int telefone) {
        this.telefone = telefone;
    }

    public int getIdade() {
        return idade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    @Override
    public String toString() {
        return "Pessoa {" +
                "dataAtual=" + dataAtual +
                ", cpf=" + cpf +
                ", telefone=" + telefone +
                ", idade=" + idade +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", endereco=" + endereco +
                ", dataNascimento=" + dataNascimento +
                '}';
    }
}
