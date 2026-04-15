# 🏋️‍♂️ Gym Control System - Sistema Distribuído de Controle de Academia

Projeto acadêmico desenvolvido para a disciplina **Sistemas Distribuídos** (UFC Campus Quixadá). Implementa um servidor remoto de gestão de academia com comunicação cliente-servidor via **TCP/UDP**.

---

## ✨ Funcionalidades Implementadas

| Questão | Requisito | Implementação |
|:---:|:---|:---|
| **1** | Serviço remoto + POJOs | `Aluno`, `Instrutor`, `Funcionario`, `Visitante` + `Academia` (core) |
| **2** | `*OutputStream` customizado | Serialização manual de arrays de POJOs para `byte[]` |
| **3** | `*InputStream` customizado | Desserialização de fluxos binários recebidos via TCP |
| **4** | Cliente-Servidor TCP | Empacotamento/desempacotamento via `Mensagem.java` |
| **5** | Sistema de Votação | Login, lista de candidatos, voto com prazo, multicast de avisos, Protobuf, servidor multithreaded |

---

## 🏗️ Arquitetura & Tecnologias

- **Linguagem:** Java 17+
- **Build Tool:** Apache Maven
- **Comunicação:**
    - `TCP Unicast` → CRUD e Votação
    - `UDP Multicast` → Avisos administrativos em tempo real
- **Serialização:**
    - Custom Streams (`*InputStream`/`*OutputStream`) para CRUD
    - Google Protocol Buffers (`*.proto`) para módulo de votação
- **Concorrência:** Servidor multithreaded (`new Thread()` por cliente) + `ScheduledExecutorService` para deadline

---

## 📂 Estrutura do Projeto

```
.
├── pom.xml                      # Dependências + Plugin Protobuf
├── src/main/
│   ├── java/com/gym/system/
│   │   ├── core/                # Regra de negócio (Academia)
│   │   ├── models/              # POJOs + Enums
│   │   ├── io/                  # Streams customizados (Q2/Q3)
│   │   ├── network/
│   │   │   ├── client/          # AcademiaClient.java
│   │   │   ├── server/          # AcademiaServer.java
│   │   │   └── protocol/        # Mensagem.java (Q4)
│   │   └── proto/               # gym_voting.proto (Q5)
│   └── resources/
└── src/test/
```

---

## ⚙️ Pré-requisitos

- [JDK 17 ou superior](https://openjdk.org/)
- [Apache Maven 3.8+](https://maven.apache.org/)
- IDE recomendada: IntelliJ IDEA ou VS Code (com extensões Java/Maven)

---

## 🚀 Como Compilar e Executar

### 1. Compilar o Projeto
```bash
mvn clean compile
```
> 💡 O plugin `protobuf-maven-plugin` gera automaticamente as classes Java a partir de `src/main/proto/gym_voting.proto` durante a compilação.

### 2. Iniciar o Servidor
```bash
# Terminal 1
mvn exec:java -Dexec.mainClass="com.gym.system.ServerMain"
# Ou execute ServerMain.java diretamente pela sua IDE
```
**Saída esperada:**
```
🟢 Servidor Academia online na porta 12345
⏳ Prazo de votação: 60s
📡 Multicast: 📢 Manutenção na piscina das 14h às 16h
```

### 3. Executar o Cliente
```bash
# Terminal 2
mvn exec:java -Dexec.mainClass="com.gym.system.ClientMain"
# Ou execute ClientMain.java diretamente pela sua IDE
```
O cliente executará automaticamente os testes CRUD, solicitará login para votação, permitirá o voto e aguardará o prazo expirar para exibir os resultados.

---

## 📖 Fluxo de Uso

1. **Testes CRUD (Automáticos):** Cadastro e listagem de `Visitante`, `Instrutor`, `Aluno` e `Funcionario`.
2. **Login para Votação:**
    - Usuário: `aluno`
    - Senha: `123`
3. **Listagem de Candidatos:** Exibe modalidades disponíveis e tempo restante.
4. **Votação:** Digite o ID da modalidade desejada.
5. **Aguardar Prazo:** O servidor rejeita `resultados` até o timer expirar. Enquanto isso, avisos multicast aparecem no console.
6. **Resultados:** Após o deadline, digite `resultados` para ver o placar com percentuais e vencedor.

---

## 🌐 Protocolos & Portas

| Tipo | Endereço/Porta | Uso |
|:---|:---|:---|
| TCP Unicast | `localhost:12345` | CRUD, Login, Lista, Voto, Resultados |
| UDP Multicast | `224.0.1.10:55555` | Avisos administrativos (broadcast contínuo) |

---

## 📝 Notas Técnicas

- ⚠️ **Warning `sun.misc.Unsafe`**: Ao rodar com Java 17+, o Protobuf 4.x emite um aviso de depreciação. **Não impacta a execução** e é seguro ignorar para fins acadêmicos.
- 🔒 **Validação de Login**: Implementada como `aluno`/`123` para simplicidade da demonstração. Pode ser facilmente adaptada para consultar a lista de `Academia.getAlunos()` ou `Academia.getFuncionarios()`.
- 🧵 **Multithreading**: Cada conexão TCP recebe uma thread dedicada (`ClienteHandler`). O multicast e o timer de deadline rodam em threads isoladas.
- 📦 **Dual Protocol**: O servidor roteia automaticamente entre `Mensagem.java` (CRUD) e `GymProto.*` (Votação) com base no primeiro byte lido (`tipoStr`).

---

## 🎓 Créditos

- **Disciplina:** Sistemas Distribuídos
- **Instituição:** Universidade Federal do Ceará (UFC) – Campus de Quixadá
- **Professor:** Rafael Braga
- **Desenvolvido por:** Murilo Fragoso ([murfra](https://github.com/murfra)) e Pedro Erykles ([pedroErykles](https://github.com/pedroErykles)).
- **Semestre:** 2026.1

---
💡 *Dica: Para testar rapidamente o deadline, altere o terceiro parâmetro em `ServerMain.java` de `60000` para `10000` (10 segundos) e recompile.*