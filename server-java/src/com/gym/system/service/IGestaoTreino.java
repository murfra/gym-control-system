package com.gym.system.service;

import com.gym.system.model.Aluno;
import com.gym.system.model.TreinoDiario;

import java.time.DayOfWeek;

public interface IGestaoTreino {
    public TreinoDiario criarTreino(Aluno aluno, DayOfWeek dia, TreinoDiario treino);
    public TreinoDiario buscarTreino(Aluno aluno, DayOfWeek dia);
    public TreinoDiario atualizarTreino(Aluno aluno, DayOfWeek dia, TreinoDiario treinoDiario);
    public void excluirTreino(Aluno aluno, DayOfWeek dia);
    public String avaliarDesempenho(Aluno aluno);
}
