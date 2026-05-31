package com.gym.system.services;

import com.gym.system.models.Aluno;
import com.gym.system.models.Exercicio;
import com.gym.system.models.TreinoDiario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.util.Map;
import java.util.List;

/**
 * Service para gerenciar treinos de alunos.
 * Utiliza a implementação GestaoTreinoService para as operações de negócio.
 */
@Service
public class TreinoService {
    
    @Autowired
    private GestaoTreinoService gestaoTreinoService;

    /**
     * Cria um novo treino para um aluno
     * @param aluno Aluno que receberá o treino
     * @param dia Dia da semana para o treino
     * @param treino Treino diário
     * @return true se criado com sucesso
     */
    public boolean criarTreino(Aluno aluno, DayOfWeek dia, TreinoDiario treino) {
        return gestaoTreinoService.criarTreino(aluno, dia, treino);
    }

    /**
     * Atualiza um treino existente
     * @param matriculaAluno Matrícula do aluno
     * @param dia Dia da semana
     * @param treinoAtualizado Treino com as atualizações
     * @return true se atualizado com sucesso
     */
    public boolean atualizarTreino(String matriculaAluno, DayOfWeek dia, TreinoDiario treinoAtualizado) {
        return gestaoTreinoService.atualizarTreino(matriculaAluno, dia, treinoAtualizado);
    }

    /**
     * Avalia o desempenho do aluno em um treino
     * @param matriculaAluno Matrícula do aluno
     * @param dia Dia da semana
     * @param exerciciosCompletados Número de exercícios completados
     * @param cargas Array com as cargas utilizadas
     * @return Relatório de avaliação de desempenho
     */
    public String avaliarDesempenho(String matriculaAluno, DayOfWeek dia, int exerciciosCompletados, int[] cargas) {
        return gestaoTreinoService.avaliarDesempenho(matriculaAluno, dia, exerciciosCompletados, cargas);
    }

    /**
     * Obtém o treino de um aluno em um dia específico
     * @param matriculaAluno Matrícula do aluno
     * @param dia Dia da semana
     * @return O TreinoDiario ou null se não encontrado
     */
    public TreinoDiario obterTreinoDia(String matriculaAluno, DayOfWeek dia) {
        return gestaoTreinoService.obterTreinoDia(matriculaAluno, dia);
    }

    /**
     * Obtém um aluno pelo sua matrícula
     * @param matriculaAluno Matrícula do aluno
     * @return O Aluno ou null se não encontrado
     */
    public Aluno obterAluno(String matriculaAluno) {
        return gestaoTreinoService.obterAluno(matriculaAluno);
    }

    /**
     * Obtém todos os alunos cadastrados
     * @return Map com todos os alunos (matrícula -> Aluno)
     */
    public Map<String, Aluno> obterTodosAlunos() {
        return gestaoTreinoService.obterTodosAlunos();
    }

    /**
     * Remove um aluno do sistema
     * @param matriculaAluno Matrícula do aluno
     * @return true se removido com sucesso
     */
    public boolean removerAluno(String matriculaAluno) {
        return gestaoTreinoService.removerAluno(matriculaAluno);
    }

    /**
     * Adiciona um exercício a um treino de um aluno
     * @param matriculaAluno Matrícula do aluno
     * @param dia Dia da semana
     * @param exercicio Exercício a adicionar
     * @return true se o exercício foi adicionado com sucesso
     */
    public boolean adicionarExercicio(String matriculaAluno, DayOfWeek dia, Exercicio exercicio) {
        TreinoDiario treino = gestaoTreinoService.obterTreinoDia(matriculaAluno, dia);
        if (treino != null && exercicio != null) {
            treino.getExercicios().add(exercicio);
            return true;
        }
        return false;
    }

    /**
     * Remove um exercício de um treino
     * @param matriculaAluno Matrícula do aluno
     * @param dia Dia da semana
     * @param nomeExercicio Nome do exercício a remover
     * @return true se removido com sucesso
     */
    public boolean removerExercicio(String matriculaAluno, DayOfWeek dia, String nomeExercicio) {
        TreinoDiario treino = gestaoTreinoService.obterTreinoDia(matriculaAluno, dia);
        if (treino != null) {
            return treino.getExercicios().removeIf(ex -> ex.getNome().equals(nomeExercicio));
        }
        return false;
    }

    /**
     * Retorna a quantidade total de treinos cadastrados
     */
    public int totalTreinosCadastrados() {
        return gestaoTreinoService.obterTodosAlunos().values()
                .stream()
                .mapToInt(aluno -> aluno.getCronograma().size())
                .sum();
    }

    public boolean salvarAluno(Aluno aluno)  {
        return gestaoTreinoService.salvarAluno(aluno);
    }
    /**
     * Verifica se um aluno existe.
     */
    public boolean existeAluno(String matriculaAluno)  {
        return gestaoTreinoService.existeAluno(matriculaAluno);
    }
    /**
     * Busca aluno por CPF.
     */
    public Aluno buscarAlunoPorCpf(String cpf)  {
        return gestaoTreinoService.buscarAlunoPorCpf(cpf);
    }
    /**
     * Busca aluno por e-mail.
     */
    public Aluno buscarAlunoPorEmail(String email)  {
        return gestaoTreinoService.buscarAlunoPorEmail(email);
    }
    /**
     * Obtém o cronograma completo do aluno.
     */
    public Map<DayOfWeek, TreinoDiario> obterCronogramaAluno(String matriculaAluno)  {
        return gestaoTreinoService.obterCronogramaAluno(matriculaAluno);
    }
    /**
     * Lista os exercícios de um treino específico.
     */
    public List<Exercicio> listarExercicios(String matriculaAluno, DayOfWeek dia)  {
        return gestaoTreinoService.listarExercicios(matriculaAluno, dia);
    }
    /**
     * Remove o treino de um dia específico.
     */
    public boolean removerTreino(String matriculaAluno, DayOfWeek dia)  {
        return gestaoTreinoService.removerTreino(matriculaAluno, dia);
    }
    /**
     * Limpa todo o cronograma do aluno.
     */
    public boolean limparCronograma(String matriculaAluno)  {
        return gestaoTreinoService.limparCronograma(matriculaAluno);
    }
}
