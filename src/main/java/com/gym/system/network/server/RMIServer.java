package com.gym.system.network.server;

import com.gym.system.interfaces.IGestaoTreino;
import com.gym.system.services.GestaoTreinoImpl;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RMIServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);

            IGestaoTreino gestaoTreino = new GestaoTreinoImpl();
            String gestaoTreinoName = "rmi://localhost/gestaoTreino";

            Naming.rebind(gestaoTreinoName, gestaoTreino);
            System.out.println("Aguardando clientes!");
        } catch (RemoteException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
