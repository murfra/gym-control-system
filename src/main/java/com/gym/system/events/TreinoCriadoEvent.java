package com.gym.system.events;

import java.io.Serializable;
import java.time.DayOfWeek;

public class TreinoCriadoEvent implements Serializable {
    private String matricula;
    private DayOfWeek dia;
    private String grupoMuscular;

    public TreinoCriadoEvent() {}

    public TreinoCriadoEvent(String matricula, DayOfWeek dia, String grupoMuscular) {
        this.matricula = matricula;
        this.dia = dia;
        this.grupoMuscular = grupoMuscular;
    }

    // Getters and Setters
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public DayOfWeek getDia() { return dia; }
    public void setDia(DayOfWeek dia) { this.dia = dia; }
    public String getGrupoMuscular() { return grupoMuscular; }
    public void setGrupoMuscular(String grupoMuscular) { this.grupoMuscular = grupoMuscular; }

    @Override
    public String toString() {
        return "Aviso: Treino de " + grupoMuscular + " para " + dia + " foi criado para o aluno " + matricula;
    }
}
