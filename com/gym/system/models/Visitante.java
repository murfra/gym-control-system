package com.gym.system.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gym.system.models.enums.Experiencia;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Visitante extends Pessoa {
    private LocalDateTime dataVisita;
    private Experiencia nivelExperiencia;

    public Visitante(String cpf, String nome, LocalDate dataNascimento, String telefone, String email,
                     Endereco endereco) {
        super(cpf, nome, dataNascimento, telefone, email, endereco);
        this.dataVisita = LocalDateTime.now();
        nivelExperiencia = Experiencia.INICIANTE;
    }

    public Visitante(String cpf, String nome, LocalDate dataNascimento, String telefone, String email,
                     Endereco endereco, Experiencia nivelExperiencia) {
        super(cpf, nome, dataNascimento, telefone, email, endereco);
        this.dataVisita = LocalDateTime.now();
        this.nivelExperiencia = nivelExperiencia;
    }

    public Visitante(@JsonProperty("cpf") String cpf,
                     @JsonProperty("nome") String nome,
                     @JsonProperty("dataNascimento") LocalDate dataNascimento,
                     @JsonProperty("telefone") String telefone,
                     @JsonProperty("email") String email,
                     @JsonProperty("endereco") Endereco endereco,
                     @JsonProperty("dataVisita") LocalDateTime dataVisita,
                     @JsonProperty("nivelExperiencia") Experiencia nivelExperiencia) {
        super(cpf, nome, dataNascimento, telefone, email, endereco);
        this.dataVisita = dataVisita;
        this.nivelExperiencia = nivelExperiencia;
    }

    public LocalDateTime getDataVisita() {
        return dataVisita;
    }

    public void setDataVisita(LocalDateTime dataVisita) {
        this.dataVisita = dataVisita;
    }

    public Experiencia getNivelExperiencia() {
        return nivelExperiencia;
    }

    public void setNivelExperiencia(Experiencia nivelExperiencia) {
        this.nivelExperiencia = nivelExperiencia;
    }
}
