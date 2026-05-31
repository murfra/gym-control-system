# API de Treinos com Redis

## Visão Geral

Sistema de gestão de treinos para alunos de academia que persiste dados em Redis usando Spring Data Redis.

## Arquitetura

```
Controller (TreinoController)
    ↓
Service (TreinoService)
    ↓
Business (GestaoTreinoService)
    ↓
Repository (TreinoRepository)
    ↓
Redis
```

## Classes Principais

### Models

- **Aluno**: Classe base para alunos
- **AlunoRedis**: Extensão de Aluno com anotações Redis (@RedisHash)
- **TreinoDiario**: Estrutura de um treino diário
- **TreinoDiarioRedis**: Extensão serializável de TreinoDiario
- **Exercicio**: Estrutura de um exercício individual

### Services

- **GestaoTreinoService**: Implementa ITreinoBusiness com persistência no Redis
- **TreinoService**: Service com métodos adicionais para gerenciamento

### Repository

- **TreinoRepository**: Interface para persistência no Redis

## Configuração

### application.properties

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=2000
spring.data.redis.database=0
```

### Redis Config

RedisConfig.java configura:

- Conexão com Jedis
- Serialização JSON para objetos
- RedisTemplate beans

## Endpoints API

### 1. Criar Treino

**POST** `/api/treinos/criar`

Request Body:

```json
{
  "aluno": {
    "cpf": "123.456.789-00",
    "nome": "João Silva",
    "dataNascimento": "1995-05-15",
    "telefone": "11999999999",
    "email": "joao@example.com",
    "nivelExperiencia": "INICIANTE"
  },
  "dia": "MONDAY",
  "treino": {
    "grupoMuscular": "Peito",
    "exercicios": [
      {
        "nome": "Supino",
        "descricao": "Supino inclinado",
        "series": 3,
        "repeticoes": 10,
        "descanso": 60,
        "carga": 80
      },
      {
        "nome": "Rosca Direta",
        "descricao": "Rosca direta com halter",
        "series": 3,
        "repeticoes": 12,
        "descanso": 45,
        "carga": 25
      }
    ]
  }
}
```

Response:

```json
{
  "sucesso": true,
  "mensagem": "Treino criado com sucesso",
  "matriculaAluno": "ALU-1717145600000"
}
```

### 2. Atualizar Treino

**PUT** `/api/treinos/atualizar/{matricula}?dia=MONDAY`

Request Body:

```json
{
  "grupoMuscular": "Costas",
  "exercicios": [
    {
      "nome": "Puxada na Barra",
      "descricao": "Puxada na barra alta",
      "series": 4,
      "repeticoes": 8,
      "descanso": 90,
      "carga": 100
    }
  ]
}
```

Response:

```json
{
  "sucesso": true,
  "mensagem": "Treino atualizado com sucesso"
}
```

### 3. Avaliar Desempenho

**POST** `/api/treinos/avaliar/{matricula}?dia=MONDAY`

Request Body:

```json
{
  "exerciciosCompletados": 3,
  "cargas": [80, 85, 80]
}
```

Response:

```json
{
  "avaliacao": "=== AVALIAÇÃO DE DESEMPENHO ===\nAluno: João Silva\nMatrícula: ALU-1717145600000\nDia: MONDAY\nGrupo Muscular: Peito\nExercícios Completados: 3/3\nTaxa de Conclusão: 100.0%\nCarga Média: 81.7 kg\nStatus: Excelente! Treino completado com sucesso.\n"
}
```

### 4. Obter Treino de um Dia

**GET** `/api/treinos/{matricula}/{dia}`

Response:

```json
{
  "diaDaSemana": "MONDAY",
  "grupoMuscular": "Peito",
  "exercicios": [
    {
      "nome": "Supino",
      "descricao": "Supino inclinado",
      "series": 3,
      "repeticoes": 10,
      "descanso": 60,
      "carga": 80
    }
  ]
}
```

### 5. Obter Aluno

**GET** `/api/treinos/aluno/{matricula}`

Response:

```json
{
  "cpf": "123.456.789-00",
  "nome": "João Silva",
  "dataNascimento": "1995-05-15",
  "telefone": "11999999999",
  "email": "joao@example.com",
  "matricula": "ALU-1717145600000",
  "nivelExperiencia": "INICIANTE",
  "cronograma": {
    "MONDAY": {
      "diaDaSemana": "MONDAY",
      "grupoMuscular": "Peito",
      "exercicios": [...]
    }
  }
}
```

### 6. Listar Todos Alunos

**GET** `/api/treinos/alunos/lista`

Response:

```json
{
  "ALU-1717145600000": {
    "cpf": "123.456.789-00",
    "nome": "João Silva",
    ...
  }
}
```

### 7. Adicionar Exercício

**POST** `/api/treinos/{matricula}/exercicios/adicionar?dia=MONDAY`

Request Body:

```json
{
  "nome": "Rosca Direta",
  "descricao": "Rosca direta com halter",
  "series": 3,
  "repeticoes": 12,
  "descanso": 45,
  "carga": 25
}
```

### 8. Remover Exercício

**DELETE** `/api/treinos/{matricula}/exercicios/{nomeExercicio}?dia=MONDAY`

### 9. Total de Treinos

**GET** `/api/treinos/total`

Response:

```json
{
  "totalTreinos": 15
}
```

### 10. Remover Aluno

**DELETE** `/api/treinos/aluno/{matricula}`

## Persistência no Redis

Os dados são armazenados no Redis com a seguinte estrutura:

```
Aluno:ALU-1717145600000 -> {
  "cpf": "123.456.789-00",
  "nome": "João Silva",
  "cronograma": {
    "MONDAY": {
      "grupoMuscular": "Peito",
      "exercicios": [...]
    }
  }
}
```

## Serialização

- **AlunoRedis** estende **Aluno** com @RedisHash("Aluno")
- **TreinoDiarioRedis** estende **TreinoDiario** implementando Serializable
- **Exercicio** implementa Serializable
- Jackson2JsonRedisSerializer para serialização JSON

## Como Usar

1. **Iniciar Redis**:

```bash
redis-server
```

2. **Iniciar a Aplicação Spring Boot**:

```bash
mvn spring-boot:run
```

3. **Fazer requisições**:

```bash
curl -X POST http://localhost:8080/api/treinos/criar \
  -H "Content-Type: application/json" \
  -d @request.json
```

## Tratamento de Erros

Todos os endpoints retornam um objeto de erro em caso de falha:

```json
{
  "erro": "Descrição do erro"
}
```

## Métodos ITreinoBusiness

```java
// Criar novo treino
boolean criarTreino(Aluno aluno, DayOfWeek dia, TreinoDiario treino)

// Atualizar treino existente
boolean atualizarTreino(String matriculaAluno, DayOfWeek dia, TreinoDiario treinoAtualizado)

// Avaliar desempenho com relatório detalhado
String avaliarDesempenho(String matriculaAluno, DayOfWeek dia, int exerciciosCompletados, int[] cargas)
```
