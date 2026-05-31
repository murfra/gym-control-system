# Sistema de Gestão de Treinos com Redis

## O que foi implementado

### 1. **Interface de Negócio (ITreinoBusiness)**

Localização: `src/main/java/com/gym/system/interfaces/ITreinoBusiness.java`

Define 3 métodos principais:

- `criarTreino()` - Cria um novo treino para um aluno
- `atualizarTreino()` - Atualiza um treino existente
- `avaliarDesempenho()` - Avalia o desempenho com relatório detalhado

### 2. **Implementação de Negócio com Redis (GestaoTreinoService)**

Localização: `src/main/java/com/gym/system/services/GestaoTreinoService.java`

- Implementa `ITreinoBusiness`
- Usa `TreinoRepository` para persistência em Redis
- Converteu armazenamento de memória para banco de dados Redis
- Todos os dados agora são persistidos

### 3. **Service Layer (TreinoService)**

Localização: `src/main/java/com/gym/system/services/TreinoService.java`

- Utiliza injeção de dependência do Spring
- Wrapper para `GestaoTreinoService`
- Métodos auxiliares para gerenciar exercícios e alunos

### 4. **Controller REST (TreinoController)**

Localização: `src/main/java/com/gym/system/api/controllers/TreinoController.java`

10 endpoints para gerenciar treinos:

- POST `/api/treinos/criar` - Criar treino
- PUT `/api/treinos/atualizar/{matricula}` - Atualizar treino
- POST `/api/treinos/avaliar/{matricula}` - Avaliar desempenho
- GET `/api/treinos/{matricula}/{dia}` - Obter treino do dia
- GET `/api/treinos/aluno/{matricula}` - Obter dados do aluno
- GET `/api/treinos/alunos/lista` - Listar alunos
- POST `/api/treinos/{matricula}/exercicios/adicionar` - Adicionar exercício
- DELETE `/api/treinos/{matricula}/exercicios/{nome}` - Remover exercício
- GET `/api/treinos/total` - Total de treinos
- DELETE `/api/treinos/aluno/{matricula}` - Remover aluno

### 5. **Models Serializáveis para Redis**

Localização: `src/main/java/com/gym/system/models/redis/`

#### AlunoRedis.java

- Estende `Aluno`
- Anotação `@RedisHash("Aluno")` para persistência
- `@Id` anotação para chave primária
- Serializable para persistência

#### TreinoDiarioRedis.java

- Estende `TreinoDiario`
- Implementa `Serializable`
- Serialização automática pelo Jackson

### 6. **Repository Spring Data Redis**

Localização: `src/main/java/com/gym/system/services/repository/TreinoRepository.java`

```java
@Repository
public interface TreinoRepository extends RedisRepository<AlunoRedis, String> {
    AlunoRedis findByMatricula(String matricula);
}
```

- Estende `RedisRepository`
- Métodos CRUD automáticos
- Método customizado para buscar por matrícula

### 7. **Configuração Redis (RedisConfig)**

Localização: `src/main/java/com/gym/system/config/RedisConfig.java`

- `RedisConnectionFactory` com Jedis
- `RedisTemplate` com serialização JSON
- Habilitação de repositórios Redis

### 8. **Propriedades de Configuração**

Localização: `src/main/resources/application.properties`

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.database=0
spring.data.redis.jedis.pool.max-active=8
```

### 9. **Classes Serializáveis**

Atualizadas para implementar `Serializable`:

- `Exercicio` - Implementa Serializable
- `TreinoDiario` - Implementa Serializable
- `Pessoa` - Implementa Serializable (classe pai)

## Fluxo de Dados

```
1. HTTP Request (JSON)
        ↓
2. TreinoController (Parse JSON)
        ↓
3. TreinoService (Business Logic)
        ↓
4. GestaoTreinoService (Implementação com Redis)
        ↓
5. TreinoRepository (Spring Data Redis)
        ↓
6. Redis Database (Persistência)
```

## Exemplo de Uso

### 1. Iniciar Redis

```bash
redis-server
```

### 2. Iniciar Aplicação

```bash
mvn spring-boot:run
```

### 3. Criar Treino

```bash
curl -X POST http://localhost:8080/api/treinos/criar \
  -H "Content-Type: application/json" \
  -d '{
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
        }
      ]
    }
  }'
```

### 4. Ver no Redis

```bash
redis-cli
> KEYS *
> HGETALL Aluno:ALU-1717145600000
```

## Arquivos Criados

```
src/main/java/com/gym/system/
├── interfaces/
│   └── ITreinoBusiness.java (NEW)
├── services/
│   ├── GestaoTreinoService.java (ATUALIZADO)
│   ├── TreinoService.java (ATUALIZADO)
│   └── repository/
│       └── TreinoRepository.java (NEW)
├── api/controllers/
│   └── TreinoController.java (ATUALIZADO)
├── config/
│   └── RedisConfig.java (NEW)
└── models/
    ├── redis/
    │   ├── AlunoRedis.java (NEW)
    │   └── TreinoDiarioRedis.java (NEW)
    ├── Exercicio.java (ATUALIZADO)
    ├── TreinoDiario.java (ATUALIZADO)
    └── Pessoa.java (ATUALIZADO)

src/main/resources/
└── application.properties (ATUALIZADO)

Documentação:
├── API_TREINOS_REDIS.md (NEW)
├── REDIS_IMPLEMENTATION.md (NEW)
└── exemplo_api_treinos.py (NEW)
```

## Dependências

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-redis</artifactId>
    <version>3.2.0</version>
</dependency>

<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>5.1.2</version>
</dependency>
```

## Dados Persistidos no Redis

Estrutura de chaves Redis:

```
Aluno:ALU-1717145600000 = {
  "cpf": "123.456.789-00",
  "nome": "João Silva",
  "matricula": "ALU-1717145600000",
  "cronograma": {
    "MONDAY": {
      "grupoMuscular": "Peito",
      "exercicios": [
        {
          "nome": "Supino",
          "series": 3,
          "repeticoes": 10,
          "carga": 80
        }
      ]
    }
  }
}
```

## Vantagens da Implementação

1. **Persistência**: Dados armazenados no Redis em vez de memória
2. **Escalabilidade**: Redis suporta múltiplas instâncias
3. **Performance**: Redis é muito rápido para leitura/escrita
4. **Serialização Automática**: Jackson converte objetos em JSON
5. **Integração Spring**: Spring Data Redis simplifica o acesso
6. **REST API**: 10 endpoints para gerenciar treinos
7. **Validação de Dados**: Parse correto de JSON com tipos específicos

## Próximos Passos (Opcional)

1. Adicionar índices no Redis para buscas mais rápidas
2. Implementar cache com TTL
3. Adicionar autenticação/autorização
4. Implementar paginação para listar alunos
5. Adicionar relatórios e estatísticas
6. Implementar logs estruturados
7. Testes unitários e integração
