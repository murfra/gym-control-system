package com.gym.system;

import com.gym.system.core.Academia;
//import com.gym.system.network.server.AcademiaServer;
//import com.gym.system.network.client.AcademiaClient;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java Main [server | client]");
            return;
        }

        String modo = args[0].toLowerCase();
        Academia sistema = new Academia(); // Instância única do contexto

        /*switch (modo) {
            case "server" -> new AcademiaServer(sistema).iniciar();
            case "client" -> new AcademiaClient(sistema).executar();
            default -> System.err.println("Modo desconhecido. Use 'server' ou 'client'.");
        }*/
    }
}