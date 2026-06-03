package com.gym.system.interfaces;

import com.gym.system.models.Aluno;
import com.gym.system.models.TreinoDiario;
import java.time.DayOfWeek;

/**
 * Interface para os métodos de negócio de gestão de treinos
 */
public interface IGestaoTreino {

    /**
     * Cria um novo treino para um aluno em um determinado dia da semana
     * @param aluno Aluno que receberá o treino
     * @param dia Dia da semana para o treino
     * @param treino Treino diário a ser atribuído
     * @return true se o treino foi criado com sucesso
     */
    boolean criarTreino(Aluno aluno, DayOfWeek dia, TreinoDiario treino);

    /**
     * Atualiza um treino existente para um aluno
     * @param matriculaAluno Matrícula do aluno
     * @param dia Dia da semana do treino a atualizar
     * @param treinoAtualizado Novo treino com as atualizações
     * @return true se o treino foi atualizado com sucesso
     */
    boolean atualizarTreino(String matriculaAluno, DayOfWeek dia, TreinoDiario treinoAtualizado);

    /**
     * Avalia o desempenho de um aluno em um treino específico
     * @param matriculaAluno Matrícula do aluno
     * @param dia Dia da semana do treino
     * @param exerciciosCompletados Número de exercícios completados
     * @param cargas Cargas utilizadas em cada exercício
     * @return uma String com a avaliação de desempenho
     */
    String avaliarDesempenho(String matriculaAluno, DayOfWeek dia, int exerciciosCompletados, int[] cargas);
}
