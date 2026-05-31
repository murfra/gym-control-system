#!/usr/bin/env python3
"""
Exemplos de uso da API de Treinos com Redis
Requisições HTTP para o sistema de gestão de treinos
"""

import requests
import json
from datetime import datetime

# URL base da API
BASE_URL = "http://localhost:8080/api/treinos"

class TreinoAPIClient:
    def __init__(self, base_url=BASE_URL):
        self.base_url = base_url
        self.session = requests.Session()
        self.session.headers.update({"Content-Type": "application/json"})

    def criar_treino(self, aluno, dia, treino):
        """Cria um novo treino para um aluno"""
        payload = {
            "aluno": aluno,
            "dia": dia,
            "treino": treino
        }
        response = self.session.post(f"{self.base_url}/criar", json=payload)
        return response.json()

    def atualizar_treino(self, matricula, dia, treino):
        """Atualiza um treino existente"""
        response = self.session.put(
            f"{self.base_url}/atualizar/{matricula}",
            params={"dia": dia},
            json=treino
        )
        return response.json()

    def avaliar_desempenho(self, matricula, dia, exercicios_completados, cargas):
        """Avalia o desempenho de um aluno"""
        payload = {
            "exerciciosCompletados": exercicios_completados,
            "cargas": cargas
        }
        response = self.session.post(
            f"{self.base_url}/avaliar/{matricula}",
            params={"dia": dia},
            json=payload
        )
        return response.json()

    def obter_treino_dia(self, matricula, dia):
        """Obtém treino de um dia específico"""
        response = self.session.get(f"{self.base_url}/{matricula}/{dia}")
        return response.json() if response.status_code == 200 else None

    def obter_aluno(self, matricula):
        """Obtém informações de um aluno"""
        response = self.session.get(f"{self.base_url}/aluno/{matricula}")
        return response.json() if response.status_code == 200 else None

    def listar_alunos(self):
        """Lista todos os alunos"""
        response = self.session.get(f"{self.base_url}/alunos/lista")
        return response.json()

    def adicionar_exercicio(self, matricula, dia, exercicio):
        """Adiciona um exercício a um treino"""
        response = self.session.post(
            f"{self.base_url}/{matricula}/exercicios/adicionar",
            params={"dia": dia},
            json=exercicio
        )
        return response.json()

    def remover_exercicio(self, matricula, dia, nome_exercicio):
        """Remove um exercício de um treino"""
        response = self.session.delete(
            f"{self.base_url}/{matricula}/exercicios/{nome_exercicio}",
            params={"dia": dia}
        )
        return response.json()

    def total_treinos(self):
        """Retorna total de treinos cadastrados"""
        response = self.session.get(f"{self.base_url}/total")
        return response.json()

    def remover_aluno(self, matricula):
        """Remove um aluno do sistema"""
        response = self.session.delete(f"{self.base_url}/aluno/{matricula}")
        return response.json()


def exemplo_1_criar_treino():
    """Exemplo 1: Criar um treino completo para um aluno"""
    print("=== EXEMPLO 1: Criar Treino ===")
    
    client = TreinoAPIClient()
    
    aluno = {
        "cpf": "123.456.789-00",
        "nome": "João Silva",
        "dataNascimento": "1995-05-15",
        "telefone": "11999999999",
        "email": "joao@example.com",
        "nivelExperiencia": "INICIANTE"
    }
    
    treino = {
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
    
    resultado = client.criar_treino(aluno, "MONDAY", treino)
    print(json.dumps(resultado, indent=2))
    return resultado.get("matriculaAluno")


def exemplo_2_atualizar_treino(matricula):
    """Exemplo 2: Atualizar um treino existente"""
    print("\n=== EXEMPLO 2: Atualizar Treino ===")
    
    client = TreinoAPIClient()
    
    treino_atualizado = {
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
    
    resultado = client.atualizar_treino(matricula, "TUESDAY", treino_atualizado)
    print(json.dumps(resultado, indent=2))


def exemplo_3_adicionar_exercicio(matricula):
    """Exemplo 3: Adicionar um novo exercício a um treino"""
    print("\n=== EXEMPLO 3: Adicionar Exercício ===")
    
    client = TreinoAPIClient()
    
    exercicio = {
        "nome": "Supino Declinado",
        "descricao": "Supino com banco declinado",
        "series": 3,
        "repeticoes": 8,
        "descanso": 90,
        "carga": 100
    }
    
    resultado = client.adicionar_exercicio(matricula, "MONDAY", exercicio)
    print(json.dumps(resultado, indent=2))


def exemplo_4_avaliar_desempenho(matricula):
    """Exemplo 4: Avaliar desempenho de um aluno em um treino"""
    print("\n=== EXEMPLO 4: Avaliar Desempenho ===")
    
    client = TreinoAPIClient()
    
    # Aluno completou 2 exercícios com cargas de 80 e 85 kg
    resultado = client.avaliar_desempenho(
        matricula,
        "MONDAY",
        exercicios_completados=2,
        cargas=[80, 85]
    )
    print(resultado["avaliacao"])


def exemplo_5_obter_informacoes(matricula):
    """Exemplo 5: Obter informações do aluno e seu treino"""
    print("\n=== EXEMPLO 5: Obter Informações ===")
    
    client = TreinoAPIClient()
    
    # Obter aluno
    aluno = client.obter_aluno(matricula)
    if aluno:
        print(f"Aluno: {aluno['nome']}")
        print(f"Matrícula: {aluno['matricula']}")
        print(f"Nível: {aluno['nivelExperiencia']}")
    
    # Obter treino de segunda-feira
    treino = client.obter_treino_dia(matricula, "MONDAY")
    if treino:
        print(f"\nTreino de segunda: {treino['grupoMuscular']}")
        print(f"Exercícios: {len(treino['exercicios'])}")


def exemplo_6_listar_alunos():
    """Exemplo 6: Listar todos os alunos cadastrados"""
    print("\n=== EXEMPLO 6: Listar Alunos ===")
    
    client = TreinoAPIClient()
    
    alunos = client.listar_alunos()
    print(f"Total de alunos: {len(alunos)}")
    for matricula, aluno in alunos.items():
        print(f"- {aluno['nome']} ({matricula})")


def exemplo_7_total_treinos():
    """Exemplo 7: Obter total de treinos cadastrados"""
    print("\n=== EXEMPLO 7: Total de Treinos ===")
    
    client = TreinoAPIClient()
    
    total = client.total_treinos()
    print(json.dumps(total, indent=2))


def exemplo_8_remover_exercicio(matricula):
    """Exemplo 8: Remover um exercício de um treino"""
    print("\n=== EXEMPLO 8: Remover Exercício ===")
    
    client = TreinoAPIClient()
    
    resultado = client.remover_exercicio(matricula, "MONDAY", "Rosca Direta")
    print(json.dumps(resultado, indent=2))


def main():
    """Executa todos os exemplos"""
    try:
        # Exemplo 1: Criar treino
        matricula = exemplo_1_criar_treino()
        
        if matricula:
            # Exemplo 2: Atualizar treino
            exemplo_2_atualizar_treino(matricula)
            
            # Exemplo 3: Adicionar exercício
            exemplo_3_adicionar_exercicio(matricula)
            
            # Exemplo 4: Avaliar desempenho
            exemplo_4_avaliar_desempenho(matricula)
            
            # Exemplo 5: Obter informações
            exemplo_5_obter_informacoes(matricula)
            
            # Exemplo 6: Listar alunos
            exemplo_6_listar_alunos()
            
            # Exemplo 7: Total de treinos
            exemplo_7_total_treinos()
            
            # Exemplo 8: Remover exercício
            exemplo_8_remover_exercicio(matricula)
    
    except requests.exceptions.ConnectionError:
        print("Erro: Não foi possível conectar à API em http://localhost:8080")
        print("Certifique-se de que a aplicação Spring Boot está rodando.")
    except Exception as e:
        print(f"Erro: {e}")


if __name__ == "__main__":
    main()
