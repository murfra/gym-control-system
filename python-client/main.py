import time
import json
from urllib.parse import quote

import requests


BASE_URL = "http://localhost:8080"
API = f"{BASE_URL}/api/treinos"

session = requests.Session()
session.headers.update({
    "Content-Type": "application/json",
    "Accept": "application/json"
})


def imprimir_resposta(titulo, response):
    print("\n" + "=" * 80)
    print(titulo)
    print("URL:", response.url)
    print("Status:", response.status_code)

    try:
        print(json.dumps(response.json(), indent=4, ensure_ascii=False))
    except ValueError:
        print(response.text)


def extrair_matricula(dados):
    """
    Tenta encontrar a matrícula em respostas diferentes da API.
    Exemplo esperado pelo Swagger:
    {
        "sucesso": true,
        "mensagem": "...",
        "matriculaAluno": "ALU-..."
    }
    """
    if isinstance(dados, dict):
        for chave in ["matriculaAluno", "matricula", "id"]:
            if chave in dados and dados[chave]:
                return dados[chave]

        for valor in dados.values():
            matricula = extrair_matricula(valor)
            if matricula:
                return matricula

    if isinstance(dados, list):
        for item in dados:
            matricula = extrair_matricula(item)
            if matricula:
                return matricula

    return None


def gerar_aluno(nome="João Silva"):
    timestamp = int(time.time())

    return {
        "cpf": f"123.456.{timestamp % 1000:03d}-{timestamp % 100:02d}",
        "nome": nome,
        "dataNascimento": "1995-05-15",
        "telefone": "11999999999",
        "email": f"joao{timestamp}@example.com",
        "nivelExperiencia": "INICIANTE"
    }


def gerar_exercicio(nome="Supino"):
    return {
        "nome": nome,
        "descricao": f"{nome} executado com técnica controlada",
        "series": 3,
        "repeticoes": 10,
        "descanso": 60,
        "carga": 80
    }


def gerar_treino(grupo_muscular="Peito"):
    return {
        "grupoMuscular": grupo_muscular,
        "exercicios": [
            gerar_exercicio("Supino"),
            gerar_exercicio("Crucifixo")
        ]
    }


def cadastrar_aluno(aluno):
    response = session.post(
        f"{API}/aluno/cadastrar",
        json=aluno
    )

    imprimir_resposta("POST - Cadastrar aluno", response)
    return response


def criar_treino_novo_aluno(aluno, dia, treino):
    dados = {
        "aluno": aluno,
        "dia": dia,
        "treino": treino
    }

    response = session.post(
        f"{API}/criar",
        json=dados
    )

    imprimir_resposta("POST - Criar treino para novo aluno", response)
    return response


def criar_treino_para_aluno_existente(matricula, dia, treino):
    response = session.post(
        f"{API}/aluno/{matricula}/treino",
        params={"dia": dia},
        json=treino
    )

    imprimir_resposta("POST - Criar treino para aluno existente", response)
    return response


def adicionar_exercicio(matricula, dia, exercicio):
    response = session.post(
        f"{API}/{matricula}/exercicios/adicionar",
        params={"dia": dia},
        json=exercicio
    )

    imprimir_resposta("POST - Adicionar exercício ao treino", response)
    return response


def avaliar_desempenho(matricula, dia):
    dados = {
        "exerciciosCompletados": 2,
        "cargas": [80, 70]
    }

    response = session.post(
        f"{API}/avaliar/{matricula}",
        params={"dia": dia},
        json=dados
    )

    imprimir_resposta("POST - Avaliar desempenho", response)
    return response


def obter_treino_por_dia(matricula, dia):
    response = session.get(
        f"{API}/{matricula}/{dia}"
    )

    imprimir_resposta("GET - Obter treino por dia", response)
    return response


def obter_total_treinos():
    response = session.get(
        f"{API}/total"
    )

    imprimir_resposta("GET - Total de treinos cadastrados", response)
    return response


def buscar_aluno_por_email(email):
    response = session.get(
        f"{API}/buscar/email",
        params={"email": email}
    )

    imprimir_resposta("GET - Buscar aluno por e-mail", response)
    return response


def buscar_aluno_por_cpf(cpf):
    response = session.get(
        f"{API}/buscar/cpf",
        params={"cpf": cpf}
    )

    imprimir_resposta("GET - Buscar aluno por CPF", response)
    return response


def listar_todos_alunos():
    response = session.get(
        f"{API}/alunos/lista"
    )

    imprimir_resposta("GET - Listar todos os alunos", response)
    return response


def obter_aluno(matricula):
    response = session.get(
        f"{API}/aluno/{matricula}"
    )

    imprimir_resposta("GET - Obter aluno por matrícula", response)
    return response


def listar_exercicios(matricula, dia):
    response = session.get(
        f"{API}/aluno/{matricula}/treino/{dia}/exercicios"
    )

    imprimir_resposta("GET - Listar exercícios de um treino", response)
    return response


def verificar_se_aluno_existe(matricula):
    response = session.get(
        f"{API}/aluno/{matricula}/existe"
    )

    imprimir_resposta("GET - Verificar se aluno existe", response)
    return response


def obter_cronograma(matricula):
    response = session.get(
        f"{API}/aluno/{matricula}/cronograma"
    )

    imprimir_resposta("GET - Obter cronograma completo", response)
    return response


def atualizar_treino(matricula, dia, treino):
    response = session.put(
        f"{API}/atualizar/{matricula}",
        params={"dia": dia},
        json=treino
    )

    imprimir_resposta("PUT - Atualizar treino existente", response)
    return response


def remover_exercicio(matricula, dia, nome_exercicio):
    nome_codificado = quote(nome_exercicio)

    response = session.delete(
        f"{API}/{matricula}/exercicios/{nome_codificado}",
        params={"dia": dia}
    )

    imprimir_resposta("DELETE - Remover exercício de um treino", response)
    return response


def remover_treino_do_dia(matricula, dia):
    response = session.delete(
        f"{API}/aluno/{matricula}/treino/{dia}"
    )

    imprimir_resposta("DELETE - Remover treino de um dia", response)
    return response


def limpar_cronograma(matricula):
    response = session.delete(
        f"{API}/aluno/{matricula}/cronograma"
    )

    imprimir_resposta("DELETE - Limpar cronograma do aluno", response)
    return response


def remover_aluno(matricula):
    response = session.delete(
        f"{API}/aluno/{matricula}"
    )

    imprimir_resposta("DELETE - Remover aluno", response)
    return response


def main():
    print("\nINICIANDO SEQUÊNCIA COMPLETA DA API GYM SYSTEM")

    dia_principal = "MONDAY"
    dia_secundario = "TUESDAY"

    # ============================================================
    # 1. VERIFICAR ESTADO INICIAL DO SISTEMA
    # ============================================================

    obter_total_treinos()
    listar_todos_alunos()

    # ============================================================
    # 2. FLUXO 1: CADASTRAR ALUNO PRIMEIRO
    # ============================================================

    aluno_1 = gerar_aluno("João Silva")

    response_cadastro = cadastrar_aluno(aluno_1)

    try:
        dados_cadastro = response_cadastro.json()
    except ValueError:
        dados_cadastro = {}

    matricula_1 = extrair_matricula(dados_cadastro)

    # Caso a API não retorne a matrícula diretamente,
    # buscamos pelo e-mail cadastrado.
    if not matricula_1:
        response_busca_email = buscar_aluno_por_email(aluno_1["email"])

        try:
            dados_busca_email = response_busca_email.json()
        except ValueError:
            dados_busca_email = {}

        matricula_1 = extrair_matricula(dados_busca_email)

    if not matricula_1:
        print("\nNão foi possível recuperar a matrícula do aluno 1.")
        print("A sequência será interrompida.")
        return

    print("\nMATRÍCULA DO ALUNO 1:", matricula_1)

    # ============================================================
    # 3. CONSULTAR DADOS DO ALUNO CADASTRADO
    # ============================================================

    buscar_aluno_por_email(aluno_1["email"])
    buscar_aluno_por_cpf(aluno_1["cpf"])
    obter_aluno(matricula_1)
    verificar_se_aluno_existe(matricula_1)
    listar_todos_alunos()

    # ============================================================
    # 4. CRIAR TREINO PARA ALUNO EXISTENTE
    # ============================================================

    treino_1 = gerar_treino("Peito")

    criar_treino_para_aluno_existente(
        matricula=matricula_1,
        dia=dia_principal,
        treino=treino_1
    )

    # ============================================================
    # 5. CONSULTAR TREINO CRIADO
    # ============================================================

    obter_treino_por_dia(matricula_1, dia_principal)
    listar_exercicios(matricula_1, dia_principal)
    obter_cronograma(matricula_1)
    obter_total_treinos()

    # ============================================================
    # 6. ADICIONAR EXERCÍCIO AO TREINO
    # ============================================================

    adicionar_exercicio(
        matricula=matricula_1,
        dia=dia_principal,
        exercicio=gerar_exercicio("Desenvolvimento")
    )

    listar_exercicios(matricula_1, dia_principal)

    # ============================================================
    # 7. ATUALIZAR TREINO EXISTENTE
    # ============================================================

    treino_atualizado = {
        "grupoMuscular": "Peito e Ombro",
        "exercicios": [
            {
                "nome": "Supino Inclinado",
                "descricao": "Supino inclinado com halteres",
                "series": 4,
                "repeticoes": 10,
                "descanso": 60,
                "carga": 70
            },
            {
                "nome": "Desenvolvimento",
                "descricao": "Desenvolvimento de ombro com halteres",
                "series": 3,
                "repeticoes": 12,
                "descanso": 60,
                "carga": 50
            },
            {
                "nome": "Elevação Lateral",
                "descricao": "Elevação lateral para ombros",
                "series": 3,
                "repeticoes": 15,
                "descanso": 45,
                "carga": 12
            }
        ]
    }

    atualizar_treino(
        matricula=matricula_1,
        dia=dia_principal,
        treino=treino_atualizado
    )

    obter_treino_por_dia(matricula_1, dia_principal)
    listar_exercicios(matricula_1, dia_principal)
    obter_cronograma(matricula_1)

    # ============================================================
    # 8. AVALIAR DESEMPENHO DO ALUNO
    # ============================================================

    avaliar_desempenho(matricula_1, dia_principal)

    # ============================================================
    # 9. FLUXO 2: CRIAR ALUNO E TREINO AO MESMO TEMPO
    # ============================================================

    aluno_2 = gerar_aluno("Maria Oliveira")

    treino_2 = {
        "grupoMuscular": "Costas",
        "exercicios": [
            {
                "nome": "Puxada Frontal",
                "descricao": "Puxada frontal na polia",
                "series": 3,
                "repeticoes": 12,
                "descanso": 60,
                "carga": 55
            },
            {
                "nome": "Remada Baixa",
                "descricao": "Remada baixa sentada",
                "series": 3,
                "repeticoes": 10,
                "descanso": 60,
                "carga": 60
            }
        ]
    }

    response_criar_completo = criar_treino_novo_aluno(
        aluno=aluno_2,
        dia=dia_secundario,
        treino=treino_2
    )

    try:
        dados_criar_completo = response_criar_completo.json()
    except ValueError:
        dados_criar_completo = {}

    matricula_2 = extrair_matricula(dados_criar_completo)

    if not matricula_2:
        response_busca_email_2 = buscar_aluno_por_email(aluno_2["email"])

        try:
            dados_busca_email_2 = response_busca_email_2.json()
        except ValueError:
            dados_busca_email_2 = {}

        matricula_2 = extrair_matricula(dados_busca_email_2)

    if matricula_2:
        print("\nMATRÍCULA DO ALUNO 2:", matricula_2)

        buscar_aluno_por_email(aluno_2["email"])
        buscar_aluno_por_cpf(aluno_2["cpf"])
        obter_aluno(matricula_2)
        verificar_se_aluno_existe(matricula_2)

        obter_treino_por_dia(matricula_2, dia_secundario)
        listar_exercicios(matricula_2, dia_secundario)
        obter_cronograma(matricula_2)

    else:
        print("\nNão foi possível recuperar a matrícula do aluno 2.")
        print("Continuando a sequência com o aluno 1.")

    # ============================================================
    # 10. VERIFICAÇÕES GERAIS ANTES DAS REMOÇÕES
    # ============================================================

    obter_total_treinos()
    listar_todos_alunos()

    # ============================================================
    # 11. REMOVER EXERCÍCIO DO TREINO DO ALUNO 1
    # ============================================================

    remover_exercicio(
        matricula=matricula_1,
        dia=dia_principal,
        nome_exercicio="Desenvolvimento"
    )

    listar_exercicios(matricula_1, dia_principal)

    # ============================================================
    # 12. REMOVER TREINO DE UM DIA DO ALUNO 1
    # ============================================================

    remover_treino_do_dia(
        matricula=matricula_1,
        dia=dia_principal
    )

    obter_cronograma(matricula_1)

    # ============================================================
    # 13. LIMPAR CRONOGRAMA DO ALUNO 1
    # ============================================================

    limpar_cronograma(matricula_1)

    obter_cronograma(matricula_1)

    # ============================================================
    # 14. REMOVER ALUNO 1
    # ============================================================

    remover_aluno(matricula_1)

    verificar_se_aluno_existe(matricula_1)

    # ============================================================
    # 15. LIMPEZA DO ALUNO 2, CASO TENHA SIDO CRIADO
    # ============================================================

    if matricula_2:
        limpar_cronograma(matricula_2)
        remover_aluno(matricula_2)
        verificar_se_aluno_existe(matricula_2)

    # ============================================================
    # 16. ESTADO FINAL DO SISTEMA
    # ============================================================

    obter_total_treinos()
    listar_todos_alunos()

    print("\nSEQUÊNCIA FINALIZADA")

if __name__ == "__main__":
    main()