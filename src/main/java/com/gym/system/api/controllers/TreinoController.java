package com.gym.system.api.controllers;

import com.gym.system.models.Aluno;
import com.gym.system.models.Exercicio;
import com.gym.system.models.TreinoDiario;
import com.gym.system.models.enums.Experiencia;
import com.gym.system.services.TreinoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Controller REST para gerenciar treinos.
 */
@RestController
@RequestMapping("/api/treinos")
@CrossOrigin(origins = "*")
@Tag( name = "Treinos", description = "Endpoints para criar, atualizar, consultar e avaliar treinos dos alunos" ) public class TreinoController  {

    @Autowired private TreinoService treinoService;

    @Operation( summary = "Criar treino para um novo aluno", description = "Cria um aluno, gera sua matrícula e associa um treino a um dia da semana." )
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Treino criado com sucesso", content =
            @Content(schema =
            @Schema(implementation = RespostaPadrao.class))),
            @ApiResponse(responseCode = "400", description = "Erro nos dados enviados", content =
            @Content(schema =
            @Schema(implementation = ErroResponse.class)))
    }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody( description = "Dados do aluno, dia da semana e treino diário", required = true, content =
    @Content( schema =
    @Schema(implementation = CriarTreinoRequest.class), examples =
    @ExampleObject( name = "Exemplo de criação de treino", value = """
            { "aluno": { "cpf": "123.456.789-00", "nome": "João Silva", "dataNascimento": "1995-05-15", "telefone": "11999999999", "email": "joao@example.com", "nivelExperiencia": "INICIANTE" }, "dia": "MONDAY", "treino": { "grupoMuscular": "Peito", "exercicios": [ { "nome": "Supino", "descricao": "Supino reto com barra", "series": 3, "repeticoes": 10, "descanso": 60, "carga": 80 } ] } }
            """ ) ) )
    @PostMapping("/criar") public ResponseEntity<?> criarTreino(@RequestBody CriarTreinoRequest request)  {
        try  {
            Aluno aluno = montarAluno(request.aluno);
            TreinoDiario treino = montarTreino(request.treino);
            boolean sucesso = treinoService.criarTreino( aluno, converterDia(request.dia), treino );
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", sucesso);
            response.put("mensagem", sucesso ? "Treino criado com sucesso" : "Falha ao criar treino");
            response.put("matriculaAluno", aluno.getMatricula());
            return sucesso ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
        }
        catch (Exception e)  {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @Operation( summary = "Cadastrar aluno", description = "Cadastra um aluno sem precisar criar treino no mesmo momento." )
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Aluno cadastrado com sucesso", content =
            @Content(schema =
            @Schema(implementation = RespostaPadrao.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao cadastrar aluno", content =
            @Content(schema =
            @Schema(implementation = ErroResponse.class)))
    }
    )
    @PostMapping("/aluno/cadastrar") public ResponseEntity<?> cadastrarAluno(@RequestBody AlunoRequest alunoData)  {
        try  {
            Aluno aluno = montarAluno(alunoData);
            boolean sucesso = treinoService.salvarAluno(aluno);
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", sucesso);
            response.put("mensagem", sucesso ? "Aluno cadastrado com sucesso" : "Falha ao cadastrar aluno");
            response.put("matriculaAluno", aluno.getMatricula());
            return sucesso ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
        }
        catch (Exception e)  {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @Operation( summary = "Criar treino para aluno existente", description = "Cria um treino para um aluno já cadastrado no sistema." )
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Treino criado com sucesso", content =
            @Content(schema =
            @Schema(implementation = RespostaPadrao.class))),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado"),
            @ApiResponse(responseCode = "400", description = "Erro ao criar treino", content =
            @Content(schema =
            @Schema(implementation = ErroResponse.class)))
    }
    )
    @PostMapping("/aluno/{matricula}/treino") public ResponseEntity<?> criarTreinoParaAlunoExistente(
            @Parameter(description = "Matrícula do aluno", example = "ALU-1717182829292")
            @PathVariable String matricula,
            @Parameter(description = "Dia da semana", example = "MONDAY")
            @RequestParam String dia,
            @RequestBody TreinoRequest treinoData)  {
        try  {
            Aluno aluno = treinoService.obterAluno(matricula);
            if (aluno == null)  {
                return ResponseEntity.notFound().build();
            }
            TreinoDiario treino = montarTreino(treinoData);
            boolean sucesso = treinoService.criarTreino( aluno, converterDia(dia), treino );
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", sucesso);
            response.put("mensagem", sucesso ? "Treino criado para aluno existente" : "Falha ao criar treino");
            return sucesso ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
        }
        catch (Exception e)  {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }


    @Operation( summary = "Atualizar treino existente", description = "Atualiza o treino de um aluno em um dia específico da semana." )
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Treino atualizado com sucesso", content =
            @Content(schema =
            @Schema(implementation = RespostaPadrao.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar treino", content =
            @Content(schema =
            @Schema(implementation = ErroResponse.class)))
    }
    )
    @PutMapping("/atualizar/{matricula}") public ResponseEntity<?> atualizarTreino(
            @Parameter(description = "Matrícula do aluno", example = "ALU-1717182829292")
            @PathVariable String matricula,
            @Parameter(description = "Dia da semana", example = "MONDAY")
            @RequestParam String dia,
            @RequestBody TreinoRequest treinoData)  {
        try  {
            TreinoDiario treinoAtualizado = montarTreino(treinoData);
            boolean sucesso = treinoService.atualizarTreino( matricula, converterDia(dia), treinoAtualizado );
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", sucesso);
            response.put("mensagem", sucesso ? "Treino atualizado com sucesso" : "Falha ao atualizar treino");
            return sucesso ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
        }
        catch (Exception e)  {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }



    @Operation( summary = "Avaliar desempenho do aluno", description = "Avalia o desempenho do aluno em um treino, considerando exercícios completados e cargas utilizadas." )
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Avaliação gerada com sucesso", content =
            @Content(schema =
            @Schema(implementation = AvaliacaoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao avaliar desempenho", content =
            @Content(schema =
            @Schema(implementation = ErroResponse.class)))
    }
    )
    @PostMapping("/avaliar/{matricula}") public ResponseEntity<?> avaliarDesempenho(
            @Parameter(description = "Matrícula do aluno", example = "ALU-1717182829292")
            @PathVariable String matricula,
            @Parameter(description = "Dia da semana", example = "MONDAY")
            @RequestParam String dia,
            @RequestBody AvaliacaoRequest avaliacaoData)  {
        try  {
            int exerciciosCompletados = avaliacaoData.exerciciosCompletados != null ? avaliacaoData.exerciciosCompletados : 0;
            int[] cargas = avaliacaoData.cargas != null ? avaliacaoData.cargas.stream().mapToInt(Integer::intValue).toArray() : new int[0];
            String avaliacao = treinoService.avaliarDesempenho( matricula, converterDia(dia), exerciciosCompletados, cargas );
            return ResponseEntity.ok(Map.of("avaliacao", avaliacao));
        }
        catch (Exception e)  {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }


    @Operation( summary = "Obter treino de um aluno por dia", description = "Busca o treino diário de um aluno usando matrícula e dia da semana." )
    @GetMapping("/{matricula}/{dia}") public ResponseEntity<?> obterTreinoDia(
            @Parameter(description = "Matrícula do aluno", example = "ALU-1717182829292")
            @PathVariable String matricula,
            @Parameter(description = "Dia da semana", example = "MONDAY")
            @PathVariable String dia)  {
        try  {
            TreinoDiario treino = treinoService.obterTreinoDia(matricula, converterDia(dia));
            if (treino == null)  {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(treino);
        }
        catch (Exception e)  {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }


    @Operation( summary = "Obter aluno por matrícula", description = "Retorna os dados de um aluno cadastrado pela matrícula." )
    @GetMapping("/aluno/{matricula}") public ResponseEntity<?> obterAluno(
            @Parameter(description = "Matrícula do aluno", example = "ALU-1717182829292")
            @PathVariable String matricula)  {
        try  {
            Aluno aluno = treinoService.obterAluno(matricula);
            if (aluno == null)  {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(aluno);
        }
        catch (Exception e)  {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }


    @Operation( summary = "Verificar se aluno existe", description = "Verifica se existe um aluno cadastrado com a matrícula informada." )
    @GetMapping("/aluno/{matricula}/existe") public ResponseEntity<?> existeAluno(
            @Parameter(description = "Matrícula do aluno", example = "ALU-1717182829292")
            @PathVariable String matricula)  {
        try  {
            boolean existe = treinoService.existeAluno(matricula);
            return ResponseEntity.ok(Map.of( "matricula", matricula, "existe", existe ));
        }
        catch (Exception e)  {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }


    @Operation( summary = "Obter cronograma completo", description = "Retorna o cronograma completo de treinos do aluno." )
    @GetMapping("/aluno/{matricula}/cronograma") public ResponseEntity<?> obterCronogramaAluno(
            @Parameter(description = "Matrícula do aluno", example = "ALU-1717182829292")
            @PathVariable String matricula)  {
        try  {
            Aluno aluno = treinoService.obterAluno(matricula);
            if (aluno == null)  {
                return ResponseEntity.notFound().build();
            }
            Map<DayOfWeek, TreinoDiario> cronograma = treinoService.obterCronogramaAluno(matricula);
            return ResponseEntity.ok(cronograma);
        }
        catch (Exception e)  {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }


    @Operation( summary = "Listar exercícios de um treino", description = "Lista os exercícios cadastrados no treino de um aluno em um dia específico." )
    @GetMapping("/aluno/{matricula}/treino/{dia}/exercicios") public ResponseEntity<?> listarExercicios(
            @Parameter(description = "Matrícula do aluno", example = "ALU-1717182829292")
            @PathVariable String matricula,
            @Parameter(description = "Dia da semana", example = "MONDAY")
            @PathVariable String dia)  {
        try  {
            Aluno aluno = treinoService.obterAluno(matricula);
            if (aluno == null)  {
                return ResponseEntity.notFound().build();
            }
            List<Exercicio> exercicios = treinoService.listarExercicios( matricula, converterDia(dia) );
            return ResponseEntity.ok(exercicios);
        }
        catch (Exception e)  {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }


    @Operation( summary = "Listar todos os alunos", description = "Retorna todos os alunos cadastrados, organizados por matrícula." )
    @GetMapping("/alunos/lista") public ResponseEntity<?> listarTodosAlunos()  {
        try  {
            Map<String, Aluno> alunos = treinoService.obterTodosAlunos();
            return ResponseEntity.ok(alunos);
        }
        catch (Exception e)  {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }
    @Operation( summary = "Adicionar exercício a um treino", description = "Adiciona um exercício ao treino de um aluno em um dia específico." )
    @PostMapping("/{matricula}/exercicios/adicionar") public ResponseEntity<?> adicionarExercicio(
            @Parameter(description = "Matrícula do aluno", example = "ALU-1717182829292")
            @PathVariable String matricula,
            @Parameter(description = "Dia da semana", example = "MONDAY")
            @RequestParam String dia,
            @RequestBody ExercicioRequest exercicioData)  {
        try  {
            Exercicio exercicio = montarExercicio(exercicioData);
            boolean sucesso = treinoService.adicionarExercicio( matricula, converterDia(dia), exercicio );
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", sucesso);
            response.put("mensagem", sucesso ? "Exercício adicionado com sucesso" : "Falha ao adicionar exercício");
            return sucesso ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
        }
        catch (Exception e)  {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }


    @Operation( summary = "Remover exercício de um treino", description = "Remove um exercício pelo nome dentro do treino de um aluno em um dia específico." )
    @DeleteMapping("/{matricula}/exercicios/{nomeExercicio}") public ResponseEntity<?> removerExercicio(
            @Parameter(description = "Matrícula do aluno", example = "ALU-1717182829292")
            @PathVariable String matricula,
            @Parameter(description = "Nome do exercício", example = "Supino")
            @PathVariable String nomeExercicio,
            @Parameter(description = "Dia da semana", example = "MONDAY")
            @RequestParam String dia)  {
        try  {
            boolean sucesso = treinoService.removerExercicio( matricula, converterDia(dia), nomeExercicio );
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", sucesso);
            response.put("mensagem", sucesso ? "Exercício removido com sucesso" : "Falha ao remover exercício");
            return sucesso ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
        }
        catch (Exception e)  {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }


    @Operation( summary = "Remover treino de um dia", description = "Remove o treino de um aluno em um dia específico." )
    @DeleteMapping("/aluno/{matricula}/treino/{dia}") public ResponseEntity<?> removerTreino(
            @Parameter(description = "Matrícula do aluno", example = "ALU-1717182829292")
            @PathVariable String matricula,
            @Parameter(description = "Dia da semana", example = "MONDAY")
            @PathVariable String dia)  {
        try  {
            boolean sucesso = treinoService.removerTreino( matricula, converterDia(dia) );
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", sucesso);
            response.put("mensagem", sucesso ? "Treino removido com sucesso" : "Falha ao remover treino");
            return sucesso ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
        }
        catch (Exception e)  {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }


    @Operation( summary = "Limpar cronograma", description = "Remove todos os treinos do cronograma de um aluno." )
    @DeleteMapping("/aluno/{matricula}/cronograma") public ResponseEntity<?> limparCronograma(
            @Parameter(description = "Matrícula do aluno", example = "ALU-1717182829292")
            @PathVariable String matricula)  {
        try  {
            boolean sucesso = treinoService.limparCronograma(matricula);
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", sucesso);
            response.put("mensagem", sucesso ? "Cronograma limpo com sucesso" : "Falha ao limpar cronograma");
            return sucesso ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
        }
        catch (Exception e)  {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }


    @Operation( summary = "Buscar aluno por CPF", description = "Busca um aluno cadastrado pelo CPF." )
    @GetMapping("/buscar/cpf") public ResponseEntity<?> buscarAlunoPorCpf(
            @Parameter(description = "CPF do aluno", example = "123.456.789-00")
            @RequestParam String cpf)  {
        try  {
            Aluno aluno = treinoService.buscarAlunoPorCpf(cpf);
            if (aluno == null)  {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(aluno);
        }
        catch (Exception e)  {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }


    @Operation( summary = "Buscar aluno por e-mail", description = "Busca um aluno cadastrado pelo e-mail." )
    @GetMapping("/buscar/email") public ResponseEntity<?> buscarAlunoPorEmail(
            @Parameter(description = "E-mail do aluno", example = "joao@example.com")
            @RequestParam String email)  {
        try  {
            Aluno aluno = treinoService.buscarAlunoPorEmail(email);
            if (aluno == null)  {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(aluno);
        }
        catch (Exception e)  {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }


    @Operation( summary = "Obter total de treinos cadastrados", description = "Retorna a quantidade total de treinos cadastrados no sistema." )
    @GetMapping("/total") public ResponseEntity<?> totalTreinosCadastrados()  {
        try  {
            int total = treinoService.totalTreinosCadastrados();
            return ResponseEntity.ok(Map.of("totalTreinos", total));
        }
        catch (Exception e)  {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }


    @Operation( summary = "Remover aluno", description = "Remove um aluno do sistema usando sua matrícula." )
    @DeleteMapping("/aluno/{matricula}") public ResponseEntity<?> removerAluno(
            @Parameter(description = "Matrícula do aluno", example = "ALU-1717182829292")
            @PathVariable String matricula)  {
        try  {
            boolean sucesso = treinoService.removerAluno(matricula);
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", sucesso);
            response.put("mensagem", sucesso ? "Aluno removido com sucesso" : "Falha ao remover aluno");
            return sucesso ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
        }
        catch (Exception e)  {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    private Aluno montarAluno(AlunoRequest alunoData)  {
        String nivelExperienciaStr = alunoData.nivelExperiencia != null ? alunoData.nivelExperiencia : "INICIANTE";
        LocalDate dataNascimento = LocalDate.parse(alunoData.dataNascimento);
        Aluno aluno = new Aluno( alunoData.cpf, alunoData.nome, dataNascimento, alunoData.telefone, alunoData.email, null, Experiencia.valueOf(nivelExperienciaStr) );
        if (alunoData.matricula != null && !alunoData.matricula.isBlank())  {
            aluno.setMatricula(alunoData.matricula);
        }
        return aluno;
    }

    private TreinoDiario montarTreino(TreinoRequest treinoData)  {
        TreinoDiario treino = new TreinoDiario();
        treino.setGrupoMuscular(treinoData.grupoMuscular);
        if (treinoData.exercicios != null)  {
            for (ExercicioRequest exData : treinoData.exercicios)  {
                treino.getExercicios().add(montarExercicio(exData));
            }
        }
        return treino;
    }

    private Exercicio montarExercicio(ExercicioRequest exData)  {
        return new Exercicio( exData.nome, exData.descricao, exData.series != null ? exData.series : 0, exData.repeticoes != null ? exData.repeticoes : 0, exData.descanso != null ? exData.descanso : 0, exData.carga != null ? exData.carga : 0 );
    }

    private DayOfWeek converterDia(String dia)  {
        String valor = dia.trim().toUpperCase();
        return switch (valor)  {
            case "SEGUNDA", "SEGUNDA_FEIRA", "SEGUNDA-FEIRA" -> DayOfWeek.MONDAY;
            case "TERCA", "TERÇA", "TERCA_FEIRA", "TERÇA_FEIRA", "TERCA-FEIRA", "TERÇA-FEIRA" -> DayOfWeek.TUESDAY;
            case "QUARTA", "QUARTA_FEIRA", "QUARTA-FEIRA" -> DayOfWeek.WEDNESDAY;
            case "QUINTA", "QUINTA_FEIRA", "QUINTA-FEIRA" -> DayOfWeek.THURSDAY;
            case "SEXTA", "SEXTA_FEIRA", "SEXTA-FEIRA" -> DayOfWeek.FRIDAY;
            case "SABADO", "SÁBADO" -> DayOfWeek.SATURDAY;
            case "DOMINGO" -> DayOfWeek.SUNDAY;
            default -> DayOfWeek.valueOf(valor);
        }
                ;
    }

    @Schema(description = "Requisição para criar um treino para um aluno") public static class CriarTreinoRequest  {
        @Schema(description = "Dados do aluno", requiredMode = Schema.RequiredMode.REQUIRED) public AlunoRequest aluno;
        @Schema(description = "Dia da semana do treino", example = "MONDAY", requiredMode = Schema.RequiredMode.REQUIRED) public String dia;
        @Schema(description = "Dados do treino diário", requiredMode = Schema.RequiredMode.REQUIRED) public TreinoRequest treino;
    }

    @Schema(description = "Dados de entrada de um aluno") public static class AlunoRequest  {
        @Schema(description = "CPF do aluno", example = "123.456.789-00", requiredMode = Schema.RequiredMode.REQUIRED) public String cpf;
        @Schema(description = "Nome completo do aluno", example = "João Silva", requiredMode = Schema.RequiredMode.REQUIRED) public String nome;
        @Schema(description = "Data de nascimento no formato ISO", example = "1995-05-15", requiredMode = Schema.RequiredMode.REQUIRED) public String dataNascimento;
        @Schema(description = "Telefone do aluno", example = "11999999999") public String telefone;
        @Schema(description = "E-mail do aluno", example = "joao@example.com") public String email;
        @Schema(description = "Matrícula do aluno. Opcional, pois o sistema pode gerar automaticamente.", example = "ALU-1717182829292") public String matricula;
        @Schema(description = "Nível de experiência do aluno", example = "INICIANTE") public String nivelExperiencia;
    }

    @Schema(description = "Dados de um treino diário") public static class TreinoRequest  {
        @Schema(description = "Grupo muscular trabalhado no treino", example = "Peito", requiredMode = Schema.RequiredMode.REQUIRED) public String grupoMuscular;
        @ArraySchema( schema =
        @Schema(implementation = ExercicioRequest.class), arraySchema =
        @Schema(description = "Lista de exercícios do treino") ) public List<ExercicioRequest> exercicios;
    }

    @Schema(description = "Dados de um exercício") public static class ExercicioRequest  {
        @Schema(description = "Nome do exercício", example = "Supino", requiredMode = Schema.RequiredMode.REQUIRED) public String nome;
        @Schema(description = "Descrição do exercício", example = "Supino reto com barra") public String descricao;
        @Schema(description = "Quantidade de séries", example = "3") public Integer series;
        @Schema(description = "Quantidade de repetições", example = "10") public Integer repeticoes;
        @Schema(description = "Tempo de descanso em segundos", example = "60") public Integer descanso;
        @Schema(description = "Carga utilizada em kg", example = "80") public Integer carga;
    }

    @Schema(description = "Dados para avaliação de desempenho") public static class AvaliacaoRequest  {
        @Schema(description = "Quantidade de exercícios completados pelo aluno", example = "3") public Integer exerciciosCompletados;
        @ArraySchema( schema =
        @Schema(description = "Carga usada em cada exercício", example = "80"), arraySchema =
        @Schema(description = "Lista de cargas utilizadas") ) public List<Integer> cargas;
    }

    @Schema(description = "Resposta padrão das operações") public static class RespostaPadrao  {
        @Schema(description = "Indica se a operação foi concluída com sucesso", example = "true") public boolean sucesso;
        @Schema(description = "Mensagem da operação", example = "Treino criado com sucesso") public String mensagem;
        @Schema(description = "Matrícula do aluno", example = "ALU-1717182829292") public String matriculaAluno;
    }

    @Schema(description = "Resposta de erro") public static class ErroResponse  {
        @Schema(description = "Mensagem de erro", example = "Dia da semana inválido") public String erro;
    }

    @Schema(description = "Resposta da avaliação de desempenho") public static class AvaliacaoResponse  {
        @Schema(description = "Relatório textual de avaliação do desempenho") public String avaliacao;
    }

}