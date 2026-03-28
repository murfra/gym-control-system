# Sistema de Gerenciamento de Academia

## Histórico de Alterações

### Dia 28/03/26
- Novas classes:
  - **Exercicio**:
    - nome, descricao, series, repeticoes, descanso (em segundos) e carga;
    - Construtor com e sem parâmetro de descrição;
    - getters e setters para cada atributo.
  - **TreinoDiario**:
    - diaDaSemana, grupoMuscular e exercicios;
    - Construtor _vazio_;f
    - getters e setters para cada atributo.
- Enum adicionado: **Experiencia** (Um aluno possui um nível de experiência, iniciante, intermediário ou avançado);
- Novos: atributos | métodos (_getters e setters_):
  - **Pessoa**: Idade | getIdade (calculado com base na data de nascimento)
  - **Aluno**: Experiência e Cronograma (de treino) | getCronograma
- Novo construtor para Aluno, sem necessidade de especificar experiência
- Criação da Interface: **GestaoTreino**
  - Métodos: **criarTreino**, **buscarTreino**, **atualizarTreino**, **excluirTreino** e **avaliarDesempenho**
- Serviço Implementado: **Gestão de Treino**. Exemplo de utilização:
  ```Java
  public class App {
      public static void main(String[] args) {
        // 1. Criamos o Serviço e o Aluno
        IGestaoTreino gestao = new GestaoTreinoImpl();
        Aluno aluno = new Aluno("João Silva");

        // 2. Criamos o treino
        TreinoDiario treinoSegunda = new TreinoDiario();
        treinoSegunda.setGrupoMuscular("Peito e Tríceps");
        
        // Criamos e adicionamos os exercícios ao treino
        Exercicio supino = new Exercicio("Supino Reto", 4, 12, "60s");
        treinoSegunda.getExercicios().add(supino);

        // 3. Enviamos para o serviço guardar
        // O serviço recebe o aluno, o dia (chave) e o treino que acabamos de criar
        gestao.criarTreino(aluno, DayOfWeek.MONDAY, treinoSegunda);

        // Pronto! Agora o objeto 'aluno' tem o treino proposto.
      }
  }```


### Dia 27/03/26
- Reformulação da estrutura de pastas
- Nova classe: **GymServer** (_socket_)

### Dia 25/03/26
- Nova estrutura de pastas:
    - Package adicionado: **com.academia**;
    - Pasta model para as entidades.
- Novas classes criadas: **Academia, Aluno, Funcionario, Instrutor e Visitante**;
- Classe **Pessoa** definida como **abstrata**;
- Classe que servirá como serviço para o cliente: **Academia**;
- Criação de atributos e rápida implementação dos métodos presentes em cada classe.

### Dia 24/03/26
- Criação das classes: **Pessoa** e **Endereço**;
- Definição de atributos e métodos para ambas as classes;
- Implementação dos métodos.
