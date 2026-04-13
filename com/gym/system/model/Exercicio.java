package com.gym.system.model;

public class Exercicio {
    private String nome;
    private String descricao;
    private int series;
    private int repeticoes;
    private int descanso; // Em segundos
    private int carga;

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
}
