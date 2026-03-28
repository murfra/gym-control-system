package com.gym.system.model;

import java.util.ArrayList;
import java.util.List;

public class Academia {
    private List<Visitante> visitantes;
    private List<Instrutor> instrutores;
    private List<Funcionario> funcionarios;
    private List<Aluno> alunosMatriculados;

    public Academia() {
        this.visitantes = new ArrayList<>();
        this.instrutores = new ArrayList<>();
        this.funcionarios = new ArrayList<>();
        this.alunosMatriculados = new ArrayList<>();
    }

    public void matricularAluno(Aluno aluno) {
        alunosMatriculados.add(aluno);
    }

    public List<Aluno> getAlunosMatriculados() {
        return alunosMatriculados;
    }
}
