package com.gym.system.models;

import java.io.Serializable;
import java.util.Objects;

public class Exercicio implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nome;
    private String descricao;
    private int series;
    private int repeticoes;
    private int descanso; // Em segundos
    private int carga;

    public Exercicio() {
        this.nome = "";
        this.descricao = "Sem descrição";
        this.series = 0;
        this.repeticoes = 0;
        this.descanso = 0;
        this.carga = 0;
    }

    public Exercicio(String nome, int series, int repeticoes, int descanso, int carga) {
        this.nome = nome;
        this.descricao = "Sem descrição";
        this.series = series;
        this.repeticoes = repeticoes;
        this.descanso = descanso;
        this.carga = carga;
    }

    public Exercicio(String nome, String descricao, int series, int repeticoes, int descanso, int carga) {
        this.nome = nome;
        this.descricao = descricao;
        this.series = series;
        this.repeticoes = repeticoes;
        this.descanso = descanso;
        this.carga = carga;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public int getRepeticoes() {
        return repeticoes;
    }

    public void setRepeticoes(int repeticoes) {
        this.repeticoes = repeticoes;
    }

    public int getDescanso() {
        return descanso;
    }

    public void setDescanso(int descanso) {
        this.descanso = descanso;
    }

    public int getCarga() {
        return carga;
    }

    public void setCarga(int carga) {
        this.carga = carga;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Exercicio exercicio = (Exercicio) o;
        return series == exercicio.series && repeticoes == exercicio.repeticoes && descanso == exercicio.descanso && carga == exercicio.carga && Objects.equals(nome, exercicio.nome) && Objects.equals(descricao, exercicio.descricao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, descricao, series, repeticoes, descanso, carga);
    }
}
