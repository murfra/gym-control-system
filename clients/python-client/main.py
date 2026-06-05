import requests
import json
from typing import Dict, Any, Tuple

class GymClient:
    """
    Cliente REST para consumir a API de Treinos (TreinoController).
    Organizado para facilitar apresentações e testes de integração.
    """
    
    def __init__(self, base_url: str = "http://localhost:8080/api/treinos"):
        self.base_url = base_url
        self.headers = {"Content-Type": "application/json"}

    def _request(self, method: str, endpoint: str, **kwargs) -> Tuple[int, Any]:
        """Método auxiliar interno para padronizar as chamadas e retornos."""
        url = f"{self.base_url}{endpoint}"
        try:
            response = requests.request(method, url, headers=self.headers, **kwargs)
            # Tenta converter para JSON, se falhar ou estiver vazio, retorna texto puro
            try:
                data = response.json()
            except ValueError:
                data = response.text
            return response.status_code, data
        except Exception as e:
            return 500, {"erro_local": str(e)}

    # ==========================================
    # GERENCIAMENTO DE ALUNOS
    # ==========================================

    def cadastrar_aluno(self, aluno_data: Dict) -> Tuple[int, Any]:
        """POST /aluno/cadastrar"""
        return self._request("POST", "/aluno/cadastrar", json=aluno_data)

    def obter_aluno(self, matricula: str) -> Tuple[int, Any]:
        """GET /aluno/{matricula}"""
        return self._request("GET", f"/aluno/{matricula}")

    def verificar_aluno_existe(self, matricula: str) -> Tuple[int, Any]:
        """GET /aluno/{matricula}/existe"""
        return self._request("GET", f"/aluno/{matricula}/existe")

    def listar_todos_alunos(self) -> Tuple[int, Any]:
        """GET /alunos/lista"""
        return self._request("GET", "/alunos/lista")

    def buscar_aluno_cpf(self, cpf: str) -> Tuple[int, Any]:
        """GET /buscar/cpf"""
        return self._request("GET", "/buscar/cpf", params={"cpf": cpf})

    def buscar_aluno_email(self, email: str) -> Tuple[int, Any]:
        """GET /buscar/email"""
        return self._request("GET", "/buscar/email", params={"email": email})

    def remover_aluno(self, matricula: str) -> Tuple[int, Any]:
        """DELETE /aluno/{matricula}"""
        return self._request("DELETE", f"/aluno/{matricula}")

    # ==========================================
    # GERENCIAMENTO DE TREINOS
    # ==========================================

    def criar_treino_novo_aluno(self, request_data: Dict) -> Tuple[int, Any]:
        """POST /criar"""
        return self._request("POST", "/criar", json=request_data)

    def criar_treino_aluno_existente(self, matricula: str, dia: str, treino_data: Dict) -> Tuple[int, Any]:
        """POST /aluno/{matricula}/treino"""
        return self._request("POST", f"/aluno/{matricula}/treino", params={"dia": dia}, json=treino_data)

    def atualizar_treino(self, matricula: str, dia: str, treino_data: Dict) -> Tuple[int, Any]:
        """PUT /atualizar/{matricula}"""
        return self._request("PUT", f"/atualizar/{matricula}", params={"dia": dia}, json=treino_data)

    def obter_treino_dia(self, matricula: str, dia: str) -> Tuple[int, Any]:
        """GET /{matricula}/{dia}"""
        return self._request("GET", f"/{matricula}/{dia}")

    def obter_cronograma(self, matricula: str) -> Tuple[int, Any]:
        """GET /aluno/{matricula}/cronograma"""
        return self._request("GET", f"/aluno/{matricula}/cronograma")

    def obter_total_treinos(self) -> Tuple[int, Any]:
        """GET /total"""
        return self._request("GET", "/total")

    def remover_treino_dia(self, matricula: str, dia: str) -> Tuple[int, Any]:
        """DELETE /aluno/{matricula}/treino/{dia}"""
        return self._request("DELETE", f"/aluno/{matricula}/treino/{dia}")

    def limpar_cronograma(self, matricula: str) -> Tuple[int, Any]:
        """DELETE /aluno/{matricula}/cronograma"""
        return self._request("DELETE", f"/aluno/{matricula}/cronograma")

    # ==========================================
    # GERENCIAMENTO DE EXERCÍCIOS E AVALIAÇÃO
    # ==========================================

    def listar_exercicios(self, matricula: str, dia: str) -> Tuple[int, Any]:
        """GET /aluno/{matricula}/treino/{dia}/exercicios"""
        return self._request("GET", f"/aluno/{matricula}/treino/{dia}/exercicios")

    def adicionar_exercicio(self, matricula: str, dia: str, exercicio_data: Dict) -> Tuple[int, Any]:
        """POST /{matricula}/exercicios/adicionar"""
        return self._request("POST", f"/{matricula}/exercicios/adicionar", params={"dia": dia}, json=exercicio_data)

    def remover_exercicio(self, matricula: str, dia: str, nome_exercicio: str) -> Tuple[int, Any]:
        """DELETE /{matricula}/exercicios/{nomeExercicio}"""
        return self._request("DELETE", f"/{matricula}/exercicios/{nome_exercicio}", params={"dia": dia})

    def avaliar_desempenho(self, matricula: str, dia: str, avaliacao_data: Dict) -> Tuple[int, Any]:
        """POST /avaliar/{matricula}"""
        return self._request("POST", f"/avaliar/{matricula}", params={"dia": dia}, json=avaliacao_data)


def print_step(title, status, data):
    """Formata o output para a apresentação."""
    print(f"\n--- {title} ---")
    print(f"Status: {status}")
    print(f"Resposta: {json.dumps(data, indent=2, ensure_ascii=False)}")


def print_relatorio_avaliacao(title:str, status: int, data: dict):
    """
    Imprime o relatório de avaliação de desempenho de forma limpa,
    interpretando as quebras de linha do StringBuilder do Java.
    """
    print(f"\n--- {title} ---")
    print(f"Status: {status}")
    
    # Verifica se a resposta contém a chave esperada
    if isinstance(data, dict) and "avaliacao" in data:
        # Imprime o texto puro enviado pelo Java, que já possui a formatação
        print(f"Resposta:\n{data["avaliacao"]}")
    else:
        # Fallback caso dê algum erro no backend
        print("Não foi possível recuperar o relatório.")
        print(f"Resposta bruta: {data}")


# ==========================================
# SCRIPT PARA EXECUÇÃO (DEMO)
# ==========================================
if __name__ == "__main__":
    # Instancia o cliente apontando para o servidor local
    api = GymClient(base_url="http://localhost:8080/api/treinos")

    # Dados de Exemplo
    aluno_mock = {
        "cpf": "123.456.789-00",
        "nome": "João Pereira",
        "dataNascimento": "1995-05-15",
        "telefone": "11999999999",
        "email": "joao@example.com",
        "nivelExperiencia": "INICIANTE"
    }

    treino_mock = {
        "grupoMuscular": "Peito",
        "exercicios": [
            {
                "nome": "Supino",
                "descricao": "Supino reto com barra",
                "series": 3,
                "repeticoes": 10,
                "descanso": 60,
                "carga": 80
            }
        ]
    }

    novo_exercicio_mock = {
        "nome": "Crucifixo",
        "descricao": "Crucifixo no banco reto",
        "series": 3,
        "repeticoes": 12,
        "descanso": 45,
        "carga": 15
    }

    avaliacao_mock = {
        "exerciciosCompletados": 2,
        "cargas": [80, 15]
    }

    print("INICIANDO DEMONSTRAÇÃO DA API GYM-SYSTEM")
    
    # 1. Cadastrar Aluno (Esperado: 200)
    status, res = api.cadastrar_aluno(aluno_mock)
    print_step("1. Cadastrar Novo Aluno", status, res)
    matricula = res.get("matriculaAluno", "MATRICULA_FALSA_PARA_TESTE")

    # 2. Verificar se Aluno Existe (Esperado: 200)
    status, res = api.verificar_aluno_existe(matricula)
    print_step("2. Verificar se Aluno Existe", status, res)

    # 3. Buscar Aluno Inexistente - Testando Erro (Esperado: 404)
    status, res = api.obter_aluno("MATRICULA-INEXISTENTE-999")
    print_step("3. Buscar Aluno Inexistente (Teste de Erro)", status, res)

    # 4. Criar Treino para o Aluno (Esperado: 200)
    status, res = api.criar_treino_aluno_existente(matricula, "MONDAY", treino_mock)
    print_step("4. Criar Treino para Segunda-Feira", status, res)

    # 5. Adicionar Exercício Extra ao Treino (Esperado: 200)
    status, res = api.adicionar_exercicio(matricula, "MONDAY", novo_exercicio_mock)
    print_step("5. Adicionar Exercício (Crucifixo)", status, res)

    # 6. Listar Exercícios de Segunda-feira (Esperado: 200)
    status, res = api.listar_exercicios(matricula, "MONDAY")
    print_step("6. Listar Exercícios da Segunda-feira", status, res)

    # 7. Avaliar Desempenho do Aluno (Esperado: 200)
    status, res = api.avaliar_desempenho(matricula, "MONDAY", avaliacao_mock)
    print_relatorio_avaliacao("7. Avaliar Desempenho", status, res)

    # 8. Obter Cronograma Completo (Esperado: 200)
    status, res = api.obter_cronograma(matricula)
    print_step("8. Obter Cronograma do Aluno", status, res)

    # 9. Listar Todos os Alunos (Esperado: 200)
    status, res = api.listar_todos_alunos()
    print_step("9. Listar Todos os Alunos no Sistema", status, res)

    # 10. Remover Exercício (Esperado: 200)
    status, res = api.remover_exercicio(matricula, "MONDAY", "Supino")
    print_step("10. Remover Exercício 'Supino'", status, res)

    # 11. Limpar Cronograma do Aluno (Esperado: 200)
    status, res = api.limpar_cronograma(matricula)
    print_step("11. Limpar Cronograma do Aluno", status, res)

    # 12. Remover Aluno do Sistema (Esperado: 200)
    status, res = api.remover_aluno(matricula)
    print_step("12. Excluir Aluno do Sistema", status, res)

