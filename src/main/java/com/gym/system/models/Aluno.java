package com.gym.system.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gym.system.models.enums.Experiencia;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Aluno extends Pessoa {
    private String matricula;
    private Experiencia nivelExperiencia;
    private Map<DayOfWeek, TreinoDiario> cronograma;

    public Aluno(String cpf, String nome, LocalDate dataNascimento, String telefone,
                 String email, Endereco endereco, Experiencia nivelExperiencia) {
        super(cpf, nome, dataNascimento, telefone, email, endereco);
        this.nivelExperiencia = nivelExperiencia;
        this.matricula = gerarMatricula();
        this.cronograma = new HashMap<>();
    }

    public Aluno(@JsonProperty("cpf") String cpf,
                 @JsonProperty("nome") String nome,
                 @JsonProperty("dataNascimento") LocalDate dataNascimento,
                 @JsonProperty("telefone") String telefone,
                 @JsonProperty("email") String email,
                 @JsonProperty("endereco") Endereco endereco,
                 @JsonProperty("matricula") String matricula,
                 @JsonProperty("nivelExperiencia") Experiencia nivelExperiencia,
                 @JsonProperty("cronograma") Map<DayOfWeek, TreinoDiario> cronograma) {
        super(cpf, nome, dataNascimento, telefone, email, endereco);
        this.matricula = matricula;
        this.nivelExperiencia = nivelExperiencia;
        this.cronograma = (cronograma != null) ? cronograma : new HashMap<>();
    }

    private String gerarMatricula() {
        return "ALU-" + System.currentTimeMillis();
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Experiencia getNivelExperiencia() {
        return nivelExperiencia;
    }

    public void setNivelExperiencia(Experiencia nivelExperiencia) {
        this.nivelExperiencia = nivelExperiencia;
    }

    public Map<DayOfWeek, TreinoDiario> getCronograma() {
        return cronograma;
    }

    public void setCronograma(Map<DayOfWeek, TreinoDiario> cronograma) {
        this.cronograma = cronograma;
    }
}