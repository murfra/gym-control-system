const axios = require('axios');

const api = axios.create({
  baseURL: 'http://localhost:8080/api/treinos',
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json'
  },

  // Assim o Axios não lança erro automático em 400/404.
  // A gente mesmo imprime o status e entende o que aconteceu.
  validateStatus: () => true
});

function linha() {
  console.log('\n' + '='.repeat(90));
}

function mostrarJson(dados) {
  if (dados === null || dados === undefined || dados === '') {
    console.log('Sem corpo de resposta.');
    return;
  }

  const texto = JSON.stringify(dados, null, 2);

  if (texto.length > 3000) {
    console.log(texto.substring(0, 3000));
    console.log('\n... resposta muito grande, exibindo apenas os primeiros 3000 caracteres.');
  } else {
    console.log(texto);
  }
}

async function executarPasso(numero, descricao, metodo, rota, config = {}) {
  linha();
  console.log(`PASSO ${numero} - ${descricao}`);
  console.log(`${metodo.toUpperCase()} ${rota}`);

  try {
    const resposta = await api.request({
      method: metodo,
      url: rota,
      ...config
    });

    const funcionou = resposta.status >= 200 && resposta.status < 300;

    console.log(`Status: ${resposta.status}`);

    if (funcionou) {
      console.log('Resultado: FUNCIONOU');
    } else {
      console.log('Resultado: A API respondeu, mas com status de erro.');
    }

    console.log('Resposta:');
    mostrarJson(resposta.data);

    return resposta.data;
  } catch (error) {
    console.log('Resultado: FALHA NA COMUNICAÇÃO COM A API');
    console.log('Erro:', error.message);
    return null;
  }
}

function extrairMatricula(dados) {
  if (!dados) return null;

  if (Array.isArray(dados)) {
    for (const item of dados) {
      const matricula = extrairMatricula(item);
      if (matricula) return matricula;
    }
  }

  if (typeof dados === 'object') {
    const possiveisCampos = [
      'matriculaAluno',
      'matricula',
      'id'
    ];

    for (const campo of possiveisCampos) {
      if (dados[campo]) {
        return dados[campo];
      }
    }

    for (const valor of Object.values(dados)) {
      const matricula = extrairMatricula(valor);
      if (matricula) return matricula;
    }
  }

  return null;
}

function gerarCpfSimples() {
  const timestamp = Date.now().toString();
  const parte1 = timestamp.slice(-9, -6).padStart(3, '1');
  const parte2 = timestamp.slice(-6, -3).padStart(3, '2');
  const parte3 = timestamp.slice(-3).padStart(3, '3');
  const digitos = timestamp.slice(-2).padStart(2, '0');

  return `${parte1}.${parte2}.${parte3}-${digitos}`;
}

function gerarAluno(nome) {
  const timestamp = Date.now();

  return {
    cpf: gerarCpfSimples(),
    nome,
    dataNascimento: '1995-05-15',
    telefone: '11999999999',
    email: `${nome.toLowerCase().replaceAll(' ', '.')}.${timestamp}@example.com`,
    nivelExperiencia: 'INICIANTE'
  };
}

function gerarExercicio(nome, carga = 80) {
  return {
    nome,
    descricao: `${nome} executado com técnica controlada`,
    series: 3,
    repeticoes: 10,
    descanso: 60,
    carga
  };
}

function gerarTreino(grupoMuscular) {
  return {
    grupoMuscular,
    exercicios: [
      gerarExercicio('Supino', 80),
      gerarExercicio('Crucifixo', 40)
    ]
  };
}

async function buscarMatriculaPorEmail(email) {
  const dados = await executarPasso(
    'BUSCA EXTRA',
    'Buscando aluno por e-mail para tentar recuperar a matrícula',
    'get',
    '/buscar/email',
    {
      params: { email }
    }
  );

  return extrairMatricula(dados);
}

async function main() {
  console.log('\nINICIANDO TESTE COMPLETO DA API GYM SYSTEM');
  console.log('Servidor esperado: http://localhost:8080');
  console.log('Base da API: /api/treinos');

  const diaPrincipal = 'MONDAY';
  const diaSecundario = 'TUESDAY';

  // ============================================================
  // 1. ESTADO INICIAL DO SISTEMA
  // ============================================================

  await executarPasso(
    1,
    'Verificando quantos treinos existem no sistema antes dos testes',
    'get',
    '/total'
  );

  await executarPasso(
    2,
    'Listando alunos cadastrados antes dos testes',
    'get',
    '/alunos/lista'
  );

  // ============================================================
  // 2. CADASTRAR UM ALUNO SEM TREINO
  // ============================================================

  const aluno1 = gerarAluno('João Silva');

  const dadosCadastroAluno1 = await executarPasso(
    3,
    'Cadastrando um aluno sem criar treino ainda',
    'post',
    '/aluno/cadastrar',
    {
      data: aluno1
    }
  );

  let matricula1 = extrairMatricula(dadosCadastroAluno1);

  if (!matricula1) {
    matricula1 = await buscarMatriculaPorEmail(aluno1.email);
  }

  if (!matricula1) {
    linha();
    console.log('Não foi possível recuperar a matrícula do aluno 1.');
    console.log('A sequência principal será encerrada porque as próximas rotas dependem da matrícula.');
    return;
  }

  linha();
  console.log(`Matrícula do aluno 1 encontrada: ${matricula1}`);

  // ============================================================
  // 3. CONSULTAR DADOS DO ALUNO
  // ============================================================

  await executarPasso(
    4,
    'Buscando aluno pelo e-mail cadastrado',
    'get',
    '/buscar/email',
    {
      params: {
        email: aluno1.email
      }
    }
  );

  await executarPasso(
    5,
    'Buscando aluno pelo CPF cadastrado',
    'get',
    '/buscar/cpf',
    {
      params: {
        cpf: aluno1.cpf
      }
    }
  );

  await executarPasso(
    6,
    'Obtendo aluno pela matrícula',
    'get',
    `/aluno/${matricula1}`
  );

  await executarPasso(
    7,
    'Verificando se o aluno existe pela matrícula',
    'get',
    `/aluno/${matricula1}/existe`
  );

  // ============================================================
  // 4. CRIAR TREINO PARA ALUNO EXISTENTE
  // ============================================================

  const treinoPeito = gerarTreino('Peito');

  await executarPasso(
    8,
    'Criando treino para o aluno já cadastrado',
    'post',
    `/aluno/${matricula1}/treino`,
    {
      params: {
        dia: diaPrincipal
      },
      data: treinoPeito
    }
  );

  // ============================================================
  // 5. CONSULTAR TREINO, EXERCÍCIOS E CRONOGRAMA
  // ============================================================

  await executarPasso(
    9,
    'Consultando o treino do aluno no dia escolhido',
    'get',
    `/${matricula1}/${diaPrincipal}`
  );

  await executarPasso(
    10,
    'Listando exercícios do treino do aluno',
    'get',
    `/aluno/${matricula1}/treino/${diaPrincipal}/exercicios`
  );

  await executarPasso(
    11,
    'Consultando o cronograma completo do aluno',
    'get',
    `/aluno/${matricula1}/cronograma`
  );

  // ============================================================
  // 6. ADICIONAR EXERCÍCIO AO TREINO
  // ============================================================

  await executarPasso(
    12,
    'Adicionando um novo exercício ao treino',
    'post',
    `/${matricula1}/exercicios/adicionar`,
    {
      params: {
        dia: diaPrincipal
      },
      data: gerarExercicio('Desenvolvimento', 50)
    }
  );

  await executarPasso(
    13,
    'Conferindo se o exercício foi adicionado',
    'get',
    `/aluno/${matricula1}/treino/${diaPrincipal}/exercicios`
  );

  // ============================================================
  // 7. ATUALIZAR TREINO EXISTENTE
  // ============================================================

  const treinoAtualizado = {
    grupoMuscular: 'Peito e Ombro',
    exercicios: [
      {
        nome: 'Supino Inclinado',
        descricao: 'Supino inclinado com halteres',
        series: 4,
        repeticoes: 10,
        descanso: 60,
        carga: 70
      },
      {
        nome: 'Desenvolvimento',
        descricao: 'Desenvolvimento de ombro com halteres',
        series: 3,
        repeticoes: 12,
        descanso: 60,
        carga: 50
      },
      {
        nome: 'Elevação Lateral',
        descricao: 'Elevação lateral para ombros',
        series: 3,
        repeticoes: 15,
        descanso: 45,
        carga: 12
      }
    ]
  };

  await executarPasso(
    14,
    'Atualizando o treino existente do aluno',
    'put',
    `/atualizar/${matricula1}`,
    {
      params: {
        dia: diaPrincipal
      },
      data: treinoAtualizado
    }
  );

  await executarPasso(
    15,
    'Consultando treino atualizado',
    'get',
    `/${matricula1}/${diaPrincipal}`
  );

  // ============================================================
  // 8. AVALIAR DESEMPENHO DO ALUNO
  // ============================================================

  const avaliacao = {
    exerciciosCompletados: 3,
    cargas: [70, 50, 12]
  };

  await executarPasso(
    16,
    'Avaliando desempenho do aluno no treino',
    'post',
    `/avaliar/${matricula1}`,
    {
      params: {
        dia: diaPrincipal
      },
      data: avaliacao
    }
  );

  // ============================================================
  // 9. CRIAR ALUNO E TREINO AO MESMO TEMPO
  // ============================================================

  const aluno2 = gerarAluno('Maria Oliveira');

  const treinoCostas = {
    grupoMuscular: 'Costas',
    exercicios: [
      {
        nome: 'Puxada Frontal',
        descricao: 'Puxada frontal na polia',
        series: 3,
        repeticoes: 12,
        descanso: 60,
        carga: 55
      },
      {
        nome: 'Remada Baixa',
        descricao: 'Remada baixa sentada',
        series: 3,
        repeticoes: 10,
        descanso: 60,
        carga: 60
      }
    ]
  };

  const dadosCriacaoCompleta = await executarPasso(
    17,
    'Criando um novo aluno e já associando um treino a ele',
    'post',
    '/criar',
    {
      data: {
        aluno: aluno2,
        dia: diaSecundario,
        treino: treinoCostas
      }
    }
  );

  let matricula2 = extrairMatricula(dadosCriacaoCompleta);

  if (!matricula2) {
    matricula2 = await buscarMatriculaPorEmail(aluno2.email);
  }

  if (matricula2) {
    linha();
    console.log(`Matrícula do aluno 2 encontrada: ${matricula2}`);

    await executarPasso(
      18,
      'Consultando aluno 2 pela matrícula',
      'get',
      `/aluno/${matricula2}`
    );

    await executarPasso(
      19,
      'Consultando treino do aluno 2',
      'get',
      `/${matricula2}/${diaSecundario}`
    );

    await executarPasso(
      20,
      'Consultando cronograma do aluno 2',
      'get',
      `/aluno/${matricula2}/cronograma`
    );
  } else {
    linha();
    console.log('Não foi possível recuperar a matrícula do aluno 2.');
    console.log('A sequência continuará apenas com o aluno 1.');
  }

  // ============================================================
  // 10. VERIFICAÇÕES GERAIS ANTES DE REMOVER DADOS
  // ============================================================

  await executarPasso(
    21,
    'Verificando total de treinos antes das remoções',
    'get',
    '/total'
  );

  await executarPasso(
    22,
    'Listando alunos antes das remoções',
    'get',
    '/alunos/lista'
  );

  // ============================================================
  // 11. REMOVER EXERCÍCIO DO TREINO
  // ============================================================

  await executarPasso(
    23,
    'Removendo exercício específico do treino do aluno 1',
    'delete',
    `/${matricula1}/exercicios/${encodeURIComponent('Desenvolvimento')}`,
    {
      params: {
        dia: diaPrincipal
      }
    }
  );

  await executarPasso(
    24,
    'Conferindo exercícios após remover um exercício',
    'get',
    `/aluno/${matricula1}/treino/${diaPrincipal}/exercicios`
  );

  // ============================================================
  // 12. REMOVER TREINO DE UM DIA
  // ============================================================

  await executarPasso(
    25,
    'Removendo treino de um dia específico do aluno 1',
    'delete',
    `/aluno/${matricula1}/treino/${diaPrincipal}`
  );

  await executarPasso(
    26,
    'Conferindo cronograma após remover treino do dia',
    'get',
    `/aluno/${matricula1}/cronograma`
  );

  // ============================================================
  // 13. LIMPAR CRONOGRAMA
  // ============================================================

  await executarPasso(
    27,
    'Limpando cronograma completo do aluno 1',
    'delete',
    `/aluno/${matricula1}/cronograma`
  );

  await executarPasso(
    28,
    'Conferindo cronograma após limpeza',
    'get',
    `/aluno/${matricula1}/cronograma`
  );

  // ============================================================
  // 14. REMOVER ALUNO 1
  // ============================================================

  await executarPasso(
    29,
    'Removendo aluno 1 do sistema',
    'delete',
    `/aluno/${matricula1}`
  );

  await executarPasso(
    30,
    'Verificando se aluno 1 ainda existe após remoção',
    'get',
    `/aluno/${matricula1}/existe`
  );

  // ============================================================
  // 15. LIMPAR ALUNO 2, CASO TENHA SIDO CRIADO
  // ============================================================

  if (matricula2) {
    await executarPasso(
      31,
      'Limpando cronograma do aluno 2',
      'delete',
      `/aluno/${matricula2}/cronograma`
    );

    await executarPasso(
      32,
      'Removendo aluno 2 do sistema',
      'delete',
      `/aluno/${matricula2}`
    );

    await executarPasso(
      33,
      'Verificando se aluno 2 ainda existe após remoção',
      'get',
      `/aluno/${matricula2}/existe`
    );
  }

  // ============================================================
  // 16. ESTADO FINAL DO SISTEMA
  // ============================================================

  await executarPasso(
    34,
    'Verificando total final de treinos',
    'get',
    '/total'
  );

  await executarPasso(
    35,
    'Listando alunos no final da sequência',
    'get',
    '/alunos/lista'
  );

  linha();
  console.log('SEQUÊNCIA FINALIZADA');
}

main();