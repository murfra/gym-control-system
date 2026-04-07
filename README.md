# Sistema de Controle de Academia

## Histórico de Alterações

### Dia 07/04/26
- Enums movidos para `model/enums/`;
- Classe de agregação movida para `core/`;
- Novo pacote `multicast` (onde será utilizado UDP para avisos);
- Alterações nos IOStream:
  - OutputStream: `writeFile` e `writeTCP` enviam no formato JSON;
  - InputStream: `readFile` e `readTCP` recebem no formato JSON;
  - _Nota_: `writeSystem` e `readSystem` ainda imprimem e recebem do terminal, respectivamente;
- Classe Pessoa alterada:
  - Idade não é mais um atributo é calculado por um método;

### Dia 01/04/26
- Implementação da Classe `Instrutor`
- `Instrutor` agora é subclasse de `Funcionario`
- Novas classes para testar as IOStreams:
  - `AlunoStreamClient`;
  - `FuncionarioStreamClient`;
  - `InstrutorStreamClient`;
  - `VisitanteStreamClient`;
- O atributo **CPF** agora é do tipo **String**;
- Implementação dos IOStreams:
  - `InstrutorInputStream` e `InstrutorOutputStream`;
  - `VisitanteInputStream` e `VisitanteOutputStream`;
- Novos atributos para Visitante e Instrutor:
  - Visitante: `nivelExperiencia`;
  - Instrutor: `cref` e `alunos` (lista de alunos do instrutor).

### Dia 30/03/26
- Classe **Pessoa** implementa `Serializable`;
- Novo Enum: `Turno`;
- Nova interface: `IGestaoAcesso`;
- Novo serviço: `GestaoAcessoImpl`;
- Novo pacote: **io**;
  - Classes adicionadas:
    - `AlunoOutputStream` e `AlunoInputStream`;
    - `FuncionarioOutputStream` e `FuncionarioInputStream`;
- Pacote **server** foi renomeado para **network**, a classe `GymServer` foi apagada e agora possui cliente e servidor
juntos na pasta:
  - Foram adicionados: `AcademiaClient` e `AcademiaServer` (Implementados para testar `AlunoOutputStream` e `AlunoInputStream`);
- Na classe `AcademiaClient` foram feitos os testes para os seguintes OutputStreams de out:
  - Saída padrão (`System.out`)
  - Um arquivo (`FileOutputStream`)
  - Servidor Remoto (**TCP**)
- Além disso, ainda na classe `AcademiaClient` há uma separação de quais testes serão feitos, se o dos alunos ou dos
funcionários, para isso, bastar enviar um byte para que o servidor defina qual `tipo` de dado receberá.

### Dia 28/03/26
- Novas classes:
  - **Exercicio**:
    - nome, descricao, series, repeticoes, descanso (em segundos) e carga;
    - Construtor com e sem parâmetro de descrição;
    - getters e setters para cada atributo.
  - **TreinoDiario**:
    - diaDaSemana, grupoMuscular e exercicios;
    - Construtor _vazio_;
    - getters e setters para cada atributo.
- Enum adicionado: **Experiencia** (Um aluno possui um nível de experiência, iniciante, intermediário ou avançado);
- Novos: atributos | métodos (_getters e setters_):
  - **Pessoa**: Idade | getIdade (calculado com base na data de nascimento)
  - **Aluno**: Experiência e Cronograma (de treino) | getCronograma
- Novo construtor para Aluno, sem necessidade de especificar experiência
- Criação da _Interface_: **GestaoTreino**
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
    - Pacote adicionado: **com.academia**;
    - Pasta model para as entidades.
- Novas classes criadas: **Academia, Aluno, Funcionario, Instrutor e Visitante**;
- Classe **Pessoa** definida como **abstrata**;
- Classe que servirá como serviço para o cliente: **Academia**;
- Criação de atributos e rápida implementação dos métodos presentes em cada classe.

### Dia 24/03/26
- Criação das classes: **Pessoa** e **Endereço**;
- Definição de atributos e métodos para ambas as classes;
- Implementação dos métodos.
