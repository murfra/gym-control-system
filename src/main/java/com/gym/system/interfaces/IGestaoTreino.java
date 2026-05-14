package com.gym.system.interfaces;

import com.gym.system.models.Aluno;
import com.gym.system.models.TreinoDiario;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.DayOfWeek;

public interface IGestaoTreino extends Remote {
    public TreinoDiario criarTreino(Aluno aluno, DayOfWeek dia, TreinoDiario treino) throws RemoteException;
    public TreinoDiario buscarTreino(Aluno aluno, DayOfWeek dia) throws RemoteException;
    public TreinoDiario atualizarTreino(Aluno aluno, DayOfWeek dia, TreinoDiario treinoDiario) throws RemoteException;
    public void excluirTreino(Aluno aluno, DayOfWeek dia) throws RemoteException;
    public String avaliarDesempenho(Aluno aluno) throws RemoteException;
}
