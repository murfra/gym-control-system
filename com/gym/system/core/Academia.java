package com.gym.system.core;

import com.gym.system.model.Aluno;
import com.gym.system.model.Funcionario;
import com.gym.system.model.Instrutor;
import com.gym.system.model.Visitante;

import java.util.ArrayList;
import java.util.List;

public class Academia {
    private List<Visitante> visitantes;
    private List<Instrutor> instrutores;
    private List<Aluno> alunos;
    private List<Funcionario> funcionarios;

    public Academia() {
        this.visitantes = new ArrayList<>();
        this.instrutores = new ArrayList<>();
        this.alunos = new ArrayList<>();
        this.funcionarios = new ArrayList<>();
    }

    // =========================
    // VISITANTES
    // =========================
    public void adicionarVisitante(Visitante visitante) {
        if (visitante != null) {
            visitantes.add(visitante);
        }
    }

    public List<Visitante> getVisitantes() {
        return visitantes;
    }

    public void setVisitantes(List<Visitante> visitantes) {
        this.visitantes = visitantes;
    }

    // =========================
    // INSTRUTORES
    // =========================
    public void adicionarInstrutor(Instrutor instrutor) {
        if (instrutor != null) {
            instrutores.add(instrutor);
        }
    }

    public List<Instrutor> getInstrutores() {
        return instrutores;
    }

    public void setInstrutores(List<Instrutor> instrutores) {
        this.instrutores = instrutores;
    }

    // =========================
    // ALUNOS
    // =========================
    public void adicionarAluno(Aluno aluno) {
        if (aluno != null) {
            alunos.add(aluno);
        }
    }

    public List<Aluno> getAlunos() {
        return alunos;
    }

    public void setAlunos(List<Aluno> alunos) {
        this.alunos = alunos;
    }

    // =========================
    // FUNCIONÁRIOS
    // =========================
    public void adicionarFuncionario(Funcionario funcionario) {
        if (funcionario != null) {
            funcionarios.add(funcionario);
        }
    }

    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }

    public void setFuncionarios(List<Funcionario> funcionarios) {
        this.funcionarios = funcionarios;
    }
}