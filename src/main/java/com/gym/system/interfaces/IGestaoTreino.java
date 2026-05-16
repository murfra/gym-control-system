package com.gym.system.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IGestaoTreino extends Remote {
    /**
     * Método do protocolo requisição-resposta (adaptado do livro texto).
     * objectRef: nome do serviço remoto
     * methodId: identificador da operação de negócio
     * arguments: payload JSON serializado (passagem por valor)
     */
    byte[] doOperation(String objectRef, String methodId, byte[] arguments) throws RemoteException;
}
