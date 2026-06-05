const axios = require('axios');

/**
 * Cliente REST para consumir a API de Treinos (TreinoController).
 * Implementado utilizando a biblioteca Axios.
 */
class GymClient {
  constructor(baseUrl = "http://localhost:8080/api/treinos") {
    // Criamos uma instância do Axios configurada para a API
    this.client = axios.create({
      baseURL: baseUrl,
      headers: {
        "Content-Type": "application/json"
      }
    });
  }

  /**
   * Método auxiliar interno para padronizar as chamadas e capturar erros HTTP.
   */
  async _request(method, endpoint, params = null, data = null) {
    try {
      const response = await this.client.request({
        method: method,
        url: endpoint,
        params: params, // Axios anexa automaticamente na Query String
        data: data      // Axios converte automaticamente para JSON
      });

      // Retorna o status HTTP e os dados da resposta com sucesso (2xx)
      return { status: response.status, data: response.data };
    } catch (error) {
      // Se o backend respondeu com um erro (ex: 404, 400)
      if (error.response) {
        return { status: error.response.status, data: error.response.data };
      }
      // Se a requisição foi feita mas o servidor não respondeu (ex: fora do ar)
      else if (error.request) {
        return { status: 503, data: { erroLocal: "Servidor indisponível ou fora do ar." } };
      }
      // Outros erros de configuração local
      else {
        return { status: 500, data: { erroLocal: error.message } };
      }
    }
  }

  // ==========================================
  // GERENCIAMENTO DE ALUNOS
  // ==========================================

  async cadastrarAluno(alunoData) {
    return this._request("POST", "/aluno/cadastrar", null, alunoData);
  }

  async obterAluno(matricula) {
    return this._request("GET", `/aluno/${matricula}`);
  }

  async verificarAlunoExiste(matricula) {
    return this._request("GET", `/aluno/${matricula}/existe`);
  }

  async listarTodosAlunos() {
    return this._request("GET", "/alunos/lista");
  }

  async buscarAlunoCpf(cpf) {
    return this._request("GET", "/buscar/cpf", { cpf });
  }

  async buscarAlunoEmail(email) {
    return this._request("GET", "/buscar/email", { email });
  }

  async removerAluno(matricula) {
    return this._request("DELETE", `/aluno/${matricula}`);
  }

  // ==========================================
  // GERENCIAMENTO DE TREINOS
  // ==========================================

  async criarTreinoNovoAluno(requestData) {
    return this._request("POST", "/criar", null, requestData);
  }

  async criarTreinoAlunoExistente(matricula, dia, treinoData) {
    return this._request("POST", `/aluno/${matricula}/treino`, { dia }, treinoData);
  }

  async atualizarTreino(matricula, dia, treinoData) {
    return this._request("PUT", `/atualizar/${matricula}`, { dia }, treinoData);
  }

  async obterTreinoDia(matricula, dia) {
    return this._request("GET", `/${matricula}/${dia}`);
  }

  async obterCronograma(matricula) {
    return this._request("GET", `/aluno/${matricula}/cronograma`);
  }

  async obterTotalTreinos() {
    return this._request("GET", "/total");
  }

  async removerTreinoDia(matricula, dia) {
    return this._request("DELETE", `/aluno/${matricula}/treino/${dia}`);
  }

  async limparCronograma(matricula) {
    return this._request("DELETE", `/aluno/${matricula}/cronograma`);
  }

  // ==========================================
  // GERENCIAMENTO DE EXERCÍCIOS E AVALIAÇÃO
  // ==========================================

  async listarExercicios(matricula, dia) {
    return this._request("GET", `/aluno/${matricula}/treino/${dia}/exercicios`);
  }

  async adicionarExercicio(matricula, dia, exercicioData) {
    return this._request("POST", `/${matricula}/exercicios/adicionar`, { dia }, exercicioData);
  }

  async removerExercicio(matricula, dia, nomeExercicio) {
    return this._request("DELETE", `/${matricula}/exercicios/${nomeExercicio}`, { dia });
  }

  async avaliarDesempenho(matricula, dia, avaliacaoData) {
    return this._request("POST", `/avaliar/${matricula}`, { dia }, avaliacaoData);
  }
}

// ==========================================
// FUNÇÕES DE IMPRESSÃO (APRESENTAÇÃO)
// ==========================================

function printStep(title, status, data) {
  console.log(`\n--- ${title} ---`);
  console.log(`Status HTTP: ${status}`);
  console.log(`Resposta: ${JSON.stringify(data, null, 2)}`);
}

function printRelatorioAvaliacao(title, status, data) {
  console.log(`\n--- ${title} ---`);
  console.log(`Status HTTP: ${status}`);

  if (data && data.avaliacao) {
    // O console.log interpreta perfeitamente o formato textual do StringBuilder vindo no JSON
    console.log(`Resposta:\n${data.avaliacao}`);
  } else {
    console.log("Não foi possível recuperar o relatório.");
    console.log(`Resposta bruta: ${JSON.stringify(data)}`);
  }
}

// ==========================================
// SCRIPT PARA EXECUÇÃO (DEMO)
// ==========================================
async function runDemo() {
  const api = new GymClient("http://localhost:8080/api/treinos");

  const alunoMock = {
    cpf: "123.456.789-00",
    nome: "João Pereira",
    dataNascimento: "1995-05-15",
    telefone: "11999999999",
    email: "joao.js@example.com",
    nivelExperiencia: "INICIANTE"
  };

  const treinoMock = {
    grupoMuscular: "Peito",
    exercicios: [
      {
        nome: "Supino",
        descricao: "Supino reto com barra",
        series: 3,
        repeticoes: 10,
        descanso: 60,
        carga: 80
      }
    ]
  };

  const novoExercicioMock = {
    nome: "Crucifixo",
    descricao: "Crucifixo no banco reto",
    series: 3,
    repeticoes: 12,
    descanso: 45,
    carga: 15
  };

  const avaliacaoMock = {
    exerciciosCompletados: 2,
    cargas: [80, 15]
  };

  console.log("INICIANDO DEMONSTRAÇÃO DA API GYM-SYSTEM (JAVASCRIPT)");

  try {
    // 1. Cadastrar Aluno
    let { status, data } = await api.cadastrarAluno(alunoMock);
    printStep("1. Cadastrar Novo Aluno", status, data);
    const matricula = data.matriculaAluno || "MATRICULA_FALSA_PARA_TESTE";

    // 2. Verificar se Aluno Existe
    ({ status, data } = await api.verificarAlunoExiste(matricula));
    printStep("2. Verificar se Aluno Existe", status, data);

    // 3. Buscar Aluno Inexistente
    ({ status, data } = await api.obterAluno("MATRICULA-INEXISTENTE-999"));
    printStep("3. Buscar Aluno Inexistente (Teste de Erro)", status, data);

    // 4. Criar Treino
    ({ status, data } = await api.criarTreinoAlunoExistente(matricula, "MONDAY", treinoMock));
    printStep("4. Criar Treino para Segunda-Feira", status, data);

    // 5. Adicionar Exercício
    ({ status, data } = await api.adicionarExercicio(matricula, "MONDAY", novoExercicioMock));
    printStep("5. Adicionar Exercício (Crucifixo)", status, data);

    // 6. Listar Exercícios
    ({ status, data } = await api.listarExercicios(matricula, "MONDAY"));
    printStep("6. Listar Exercícios da Segunda-feira", status, data);

    // 7. Avaliar Desempenho (Usando a impressão customizada)
    ({ status, data } = await api.avaliarDesempenho(matricula, "MONDAY", avaliacaoMock));
    printRelatorioAvaliacao("7. Avaliar Desempenho", status, data);

    // 8. Obter Cronograma Completo
    ({ status, data } = await api.obterCronograma(matricula));
    printStep("8. Obter Cronograma do Aluno", status, data);

    // 9. Listar Todos os Alunos
    ({ status, data } = await api.listarTodosAlunos());
    printStep("9. Listar Todos os Alunos no Sistema", status, data);

    // 10. Remover Exercício
    ({ status, data } = await api.removerExercicio(matricula, "MONDAY", "Supino"));
    printStep("10. Remover Exercício 'Supino'", status, data);

    // 11. Limpar Cronograma
    ({ status, data } = await api.limparCronograma(matricula));
    printStep("11. Limpar Cronograma do Aluno", status, data);

    // 12. Remover Aluno
    ({ status, data } = await api.removerAluno(matricula));
    printStep("12. Excluir Aluno do Sistema", status, data);

  } catch (error) {
    console.error("Erro inesperado na demonstração:", error);
  }
}

runDemo();

