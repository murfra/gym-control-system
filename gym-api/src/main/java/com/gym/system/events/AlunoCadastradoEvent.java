package com.gym.system.events;

import java.io.Serializable;

public class AlunoCadastradoEvent implements Serializable {
    private String matricula;
    private String nome;
    private String email;

    public AlunoCadastradoEvent() {}

    public AlunoCadastradoEvent(String matricula, String nome, String email) {
        this.matricula = matricula;
        this.nome = nome;
        this.email = email;
    }

    // Getters and Setters
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Bem-vindo à academia, " + nome + "! Seu e-mail de boas-vindas será enviado para " + email;
    }
}
