package com.gym.system.interfaces;

import com.gym.system.models.Aluno;
import com.gym.system.models.TreinoDiario;

import java.time.DayOfWeek;

public interface IGestaoTreino {
    public TreinoDiario criarTreino(Aluno aluno, DayOfWeek dia, TreinoDiario treino);
    public TreinoDiario buscarTreino(Aluno aluno, DayOfWeek dia);
    public TreinoDiario atualizarTreino(Aluno aluno, DayOfWeek dia, TreinoDiario treinoDiario);
    public void excluirTreino(Aluno aluno, DayOfWeek dia);
    public String avaliarDesempenho(Aluno aluno);
}
