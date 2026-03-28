package com.gym.system.model;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class TreinoDiario {
    private DayOfWeek diaDaSemana;
    private String grupoMuscular;
    private List<Exercicio> exercicios= new ArrayList<>();

    // Construtor vazio, os treinos serão gerenciados pelo serviço
    public TreinoDiario() {}

    public DayOfWeek getDiaDaSemana() {
        return diaDaSemana;
    }

    public void setDiaDaSemana(DayOfWeek diaDaSemana) {
        this.diaDaSemana = diaDaSemana;
    }

    public String getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(String grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }

    public List<Exercicio> getExercicios() {
        return exercicios;
    }

    public void setExercicios(List<Exercicio> exercicios) {
        this.exercicios = exercicios;
    }
}
