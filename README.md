# Sistema de Controle de Academia

Este projeto é desenvolvido para a disciplina de **Sistemas Distribuídos**. O objetivo é criar uma plataforma para a gestão de alunos, funcionários e rotinas de treino, utilizando uma arquitetura distribuída.

**Autores**: Murilo Fragoso ([murfra](https://github.com/murfra)) e Pedro Erykles  ([pedroErykles](https://github.com/pedroErykles))

## 🚀 O Projeto

O sistema gerencia o ecossistema de uma academia, permitindo o controle de acesso, gestão de pessoal e acompanhamento de evolução física através de interfaces específicas.

### Estrutura de Classes

O backend foi modelado seguindo os princípios de Programação Orientada a Objetos:

- **Superclasse:** `Pessoa`
- **Subclasses:** `Aluno`, `Instrutor`, `Funcionario` e `Visitante`.
- **Agregação:** A classe `Academia` centraliza a lógica, contendo os conjuntos de matriculados, instrutores, planos de treino e aulas agendadas.
- **Interface de Negócio:** `GestaoTreino` (Responsável por: `criarTreino`, `atualizarTreino` e `avaliarDesempenho`).

## 🛠️ Tecnologias

*   **Backend:** Java
*   **Frontend:** A definir
<!-- *   **Comunicação:** REST API / gRPC (planejado para Sistemas Distribuídos) -->

## 📁 Estrutura de Pastas

```text
├── backend/                # Código fonte em Java
│   ├── src/main/java/com/gym/system
│   │   ├── core/           # Academia (Agregação)
│   │   ├── io/             # IOStreams
│   │   ├── models/         # Pessoa, Aluno, etc.
│   │   ├── multicast/      # Avisos
│   │   ├── network/        # AcademiaServer, etc.
│   │   ├── interfaces/     # GestaoTreino
│   │   ├── services/       # Lógica de negócio
│   │   ├── test/           # IOStreams testes
│   │   ├── ClientMain.java # Cliente (Inicial)
│   │   └── ServerMain.java # Servidor (Inicial)
└── frontend/               # (A definir)
