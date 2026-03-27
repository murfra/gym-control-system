package com.gym.system.service;

import com.gym.system.model.*;
import com.gym.system.model.Aluno;
import com.gym.system.model.Funcionario;
import com.gym.system.model.Instrutor;
import com.gym.system.model.Visitante;

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
