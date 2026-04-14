package com.gym.system.services;

import com.gym.system.interfaces.IGestaoTreino;
import com.gym.system.models.Aluno;
import com.gym.system.models.TreinoDiario;

import java.time.DayOfWeek;

public class GestaoTreinoImpl implements IGestaoTreino {

    @Override
    public TreinoDiario criarTreino(Aluno aluno, DayOfWeek dia, TreinoDiario treino) {
        if (aluno == null) {
            return null;
        }
        // Pegamos o mapa que pertence ao aluno e guardamos o treino lá
        aluno.getCronograma().put(dia, treino);
        return treino;
    }

    @Override
    public TreinoDiario buscarTreino(Aluno aluno, DayOfWeek dia) {
        // Busca no mapa específico deste aluno
        return aluno == null ? null : aluno.getCronograma().get(dia);
    }

    @Override
    public TreinoDiario atualizarTreino(Aluno aluno, DayOfWeek dia, TreinoDiario treino) {
        if (aluno == null) {
            return null;
        }
        // No Map, o put substitui o valor se a chave (dia) já existir
        aluno.getCronograma().put(dia, treino);
        return treino;
    }

    @Override
    public void excluirTreino(Aluno aluno, DayOfWeek dia) {
        if (aluno == null) return;
        aluno.getCronograma().remove(dia);
    }

    @Override
    public String avaliarDesempenho(Aluno aluno) {
        if (aluno == null) return "Aluno não encontrado";
        int totalTreinos = aluno.getCronograma().size();
        return "O aluno " + aluno.getNome() + " tem " + totalTreinos + " treinos agendados na semana.";
    }
}
