package com.gym.system.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;

public abstract class Pessoa implements Serializable {
    private LocalDate dataAtual = LocalDate.now(ZoneId.systemDefault());
    private int idade;
    private String cpf;
    private String telefone;
    private String nome;
    private String email;
    private Endereco endereco;
    private LocalDate dataNascimento;

    public Pessoa(String cpf, String nome, LocalDate dataNascimento, String telefone, String email, Endereco endereco) {
        this.cpf = cpf;
        this.telefone = telefone;
        //this.idade = Period.between(dataNascimento, dataAtual).getYears();
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.email = email;
        this.endereco = endereco;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public int getIdade() {
        return idade;
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
