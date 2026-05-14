package com.gym.system.network.client;

import com.gym.system.interfaces.IGestaoTreino;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RMIClient {
    public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException {
        String gestaoTreinoName = "rmi://localhost/gestaoTreino";
        IGestaoTreino gestaoTreino = (IGestaoTreino) Naming.lookup(gestaoTreinoName);
    }
}
