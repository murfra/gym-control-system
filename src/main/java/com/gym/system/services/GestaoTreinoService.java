package com.gym.system.services;

import com.gym.system.interfaces.ITreinoBusiness;
import com.gym.system.models.Aluno;
import com.gym.system.models.TreinoDiario;
import com.gym.system.models.redis.AlunoRedis;
import com.gym.system.models.redis.TreinoDiarioRedis;
import com.gym.system.services.repository.TreinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import com.gym.system.models.Exercicio;
import java.util.ArrayList;
import java.util.List;

@Component
public class GestaoTreinoService implements ITreinoBusiness {
    
    @Autowired
    private TreinoRepository treinoRepository;

    @Override
    public boolean criarTreino(Aluno aluno, DayOfWeek dia, TreinoDiario treino) {
        if (aluno == null || dia == null || treino == null) {
            return false;
        }

        try {
            // Busca ou cria um aluno no Redis
            AlunoRedis alunoRedis = treinoRepository.findByMatricula(aluno.getMatricula());
            if (alunoRedis == null) {
                alunoRedis = new AlunoRedis(aluno);
                alunoRedis.setCronograma(new HashMap<>());
            }

            // Adiciona ou atualiza o treino do dia
            TreinoDiarioRedis treinoDiarioRedis = new TreinoDiarioRedis(treino);
            treinoDiarioRedis.setDiaDaSemana(dia);
            alunoRedis.getCronograma().put(dia, treinoDiarioRedis);

            // Persiste no Redis
            treinoRepository.save(alunoRedis);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean atualizarTreino(String matriculaAluno, DayOfWeek dia, TreinoDiario treinoAtualizado) {
        if (matriculaAluno == null || dia == null || treinoAtualizado == null) {
            return false;
        }

        try {
            // Busca o aluno no Redis
            AlunoRedis alunoRedis = treinoRepository.findByMatricula(matriculaAluno);
            if (alunoRedis == null) {
                return false;
            }

            // Verifica se o treino do dia existe
            if (!alunoRedis.getCronograma().containsKey(dia)) {
                return false;
            }

            // Atualiza o treino
            TreinoDiarioRedis treinoDiarioRedis = new TreinoDiarioRedis(treinoAtualizado);
            treinoDiarioRedis.setDiaDaSemana(dia);
            alunoRedis.getCronograma().put(dia, treinoDiarioRedis);

            // Persiste no Redis
            treinoRepository.save(alunoRedis);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String avaliarDesempenho(String matriculaAluno, DayOfWeek dia, int exerciciosCompletados, int[] cargas) {
        if (matriculaAluno == null || dia == null) {
            return "Erro: Parâmetros inválidos";
        }

        try {
            // Busca o aluno no Redis
            AlunoRedis alunoRedis = treinoRepository.findByMatricula(matriculaAluno);
            if (alunoRedis == null) {
                return "Erro: Aluno não encontrado";
            }

            // Verifica se o treino do dia existe
            if (!alunoRedis.getCronograma().containsKey(dia)) {
                return "Erro: Treino do dia " + dia + " não encontrado";
            }

            TreinoDiario treino = alunoRedis.getCronograma().get(dia);
            int totalExercicios = treino.getExercicios().size();

            if (totalExercicios == 0) {
                return "Erro: Treino sem exercícios cadastrados";
            }

            // Calcula a taxa de conclusão
            double taxaConclusao = (double) exerciciosCompletados / totalExercicios * 100;

            // Calcula a média de carga
            double mediaCargas = 0;
            if (cargas != null && cargas.length > 0) {
                for (int carga : cargas) {
                    mediaCargas += carga;
                }
                mediaCargas /= cargas.length;
            }

            // Gera relatório de avaliação
            StringBuilder avaliacao = new StringBuilder();
            avaliacao.append("=== AVALIAÇÃO DE DESEMPENHO ===\n");
            avaliacao.append("Aluno: ").append(alunoRedis.getNome()).append("\n");
            avaliacao.append("Matrícula: ").append(matriculaAluno).append("\n");
            avaliacao.append("Dia: ").append(dia).append("\n");
            avaliacao.append("Grupo Muscular: ").append(treino.getGrupoMuscular()).append("\n");
            avaliacao.append("Exercícios Completados: ").append(exerciciosCompletados).append("/").append(totalExercicios).append("\n");
            avaliacao.append("Taxa de Conclusão: ").append(String.format("%.1f%%", taxaConclusao)).append("\n");
            avaliacao.append("Carga Média: ").append(String.format("%.1f kg", mediaCargas)).append("\n");

            // Adiciona feedback baseado na taxa de conclusão
            if (taxaConclusao == 100) {
                avaliacao.append("Status: Excelente! Treino completado com sucesso.\n");
            } else if (taxaConclusao >= 80) {
                avaliacao.append("Status: Bom! A maioria dos exercícios foi completada.\n");
            } else if (taxaConclusao >= 60) {
                avaliacao.append("Status: Aceitável. Procure completar mais exercícios próxima vez.\n");
            } else {
                avaliacao.append("Status: Insuficiente. Recomenda-se dedicação maior ao treino.\n");
            }

            return avaliacao.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao avaliar desempenho: " + e.getMessage();
        }
    }

    /**
     * Retorna o aluno pela matrícula
     */
    public Aluno obterAluno(String matricula) {
        try {
            return treinoRepository.findByMatricula(matricula);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retorna todos os alunos cadastrados
     */
    public Map<String, Aluno> obterTodosAlunos() {
        Map<String, Aluno> alunos = new HashMap<>();

        try {
            for (AlunoRedis aluno : treinoRepository.findAll()) {
                alunos.put(aluno.getMatricula(), aluno);
            }

            return alunos;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    /**
     * Remove um aluno do Redis
     */
    public boolean removerAluno(String matricula)  {
        try  {
            if (!treinoRepository.existsByMatricula(matricula))  {
                return false;
            }
            treinoRepository.deleteByMatricula(matricula);
            return true;
        }
        catch (Exception e)  {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retorna o treino de um aluno em um dia específico
     */
    public TreinoDiario obterTreinoDia(String matricula, DayOfWeek dia) {
        try {
            AlunoRedis aluno = treinoRepository.findByMatricula(matricula);
            if (aluno == null) {
                return null;
            }
            return aluno.getCronograma().get(dia);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Salva ou atualiza um aluno no Redis.
     */
    public boolean salvarAluno(Aluno aluno)  {
        if (aluno == null || aluno.getMatricula() == null || aluno.getMatricula().isBlank())  {
            return false;
        }
        try  {
            AlunoRedis alunoRedis = new AlunoRedis(aluno);
            if (alunoRedis.getCronograma() == null)  {
                alunoRedis.setCronograma(new HashMap<>());
            }
            treinoRepository.save(alunoRedis);
            return true;
        }
        catch (Exception e)  {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Verifica se existe aluno com a matrícula informada.
     */
    public boolean existeAluno(String matricula)  {
        try  {
            return treinoRepository.existsByMatricula(matricula);
        }
        catch (Exception e)  {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Busca aluno por CPF.
     */
    public Aluno buscarAlunoPorCpf(String cpf)  {
        try  {
            return treinoRepository.findByCpf(cpf);
        }
        catch (Exception e)  {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Busca aluno por e-mail.
     */
    public Aluno buscarAlunoPorEmail(String email)  {
        try  {
            return treinoRepository.findByEmail(email);
        }
        catch (Exception e)  {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Retorna o cronograma completo de um aluno.
     */
    public Map<DayOfWeek, TreinoDiario> obterCronogramaAluno(String matricula)  {
        try  {
            AlunoRedis aluno = treinoRepository.findByMatricula(matricula);
            if (aluno == null || aluno.getCronograma() == null)  {
                return new HashMap<>();
            }
            return aluno.getCronograma();
        }
        catch (Exception e)  {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    /**
     * Lista os exercícios de um treino específico.
     */
    public List<Exercicio> listarExercicios(String matricula, DayOfWeek dia)  {
        try  {
            TreinoDiario treino = obterTreinoDia(matricula, dia);
            if (treino == null || treino.getExercicios() == null)  {
                return new ArrayList<>();
            }
            return new ArrayList<>(treino.getExercicios());
        }
        catch (Exception e)  {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    /**
     * Adiciona um exercício a um treino e persiste no Redis.
     */
    public boolean adicionarExercicio(String matricula, DayOfWeek dia, Exercicio exercicio)  {
        if (matricula == null || dia == null || exercicio == null)  {
            return false;
        }
        try  {
            AlunoRedis aluno = treinoRepository.findByMatricula(matricula);
            if (aluno == null || aluno.getCronograma() == null)  {
                return false;
            }
            TreinoDiario treino = aluno.getCronograma().get(dia);
            if (treino == null)  {
                return false;
            }
            if (treino.getExercicios() == null)  {
                treino.setExercicios(new ArrayList<>());
            }
            treino.getExercicios().add(exercicio);
            aluno.getCronograma().put(dia, treino);
            treinoRepository.save(aluno);
            return true;
        }
        catch (Exception e)  {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Remove um exercício de um treino e persiste no Redis.
     */
    public boolean removerExercicio(String matricula, DayOfWeek dia, String nomeExercicio)  {
        if (matricula == null || dia == null || nomeExercicio == null || nomeExercicio.isBlank())  {
            return false;
        }
        try  {
            AlunoRedis aluno = treinoRepository.findByMatricula(matricula);
            if (aluno == null || aluno.getCronograma() == null)  {
                return false;
            }
            TreinoDiario treino = aluno.getCronograma().get(dia);
            if (treino == null || treino.getExercicios() == null)  {
                return false;
            }
            boolean removeu = treino.getExercicios()
                    .removeIf(exercicio -> exercicio.getNome().equalsIgnoreCase(nomeExercicio));
            if (!removeu)  {
                return false;
            }
            aluno.getCronograma().put(dia, treino);
            treinoRepository.save(aluno);
            return true;
        }
        catch (Exception e)  {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Remove o treino de um dia específico.
     */
    public boolean removerTreino(String matricula, DayOfWeek dia)  {
        if (matricula == null || dia == null)  {
            return false;
        }
        try  {
            AlunoRedis aluno = treinoRepository.findByMatricula(matricula);
            if (aluno == null || aluno.getCronograma() == null)  {
                return false;
            }
            if (!aluno.getCronograma().containsKey(dia))  {
                return false;
            }
            aluno.getCronograma().remove(dia);
            treinoRepository.save(aluno);
            return true;
        }
        catch (Exception e)  {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Remove todos os treinos do cronograma do aluno.
     */
    public boolean limparCronograma(String matricula)  {
        if (matricula == null || matricula.isBlank())  {
            return false;
        }
        try  {
            AlunoRedis aluno = treinoRepository.findByMatricula(matricula);
            if (aluno == null)  {
                return false;
            }
            aluno.setCronograma(new HashMap<>());
            treinoRepository.save(aluno);
            return true;
        }
        catch (Exception e)  {
            e.printStackTrace();
            return false;
        }
    }
}
