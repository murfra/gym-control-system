package com.gym.system;
import com.gym.system.core.Academia;
import com.gym.system.network.server.AcademiaServer;
import com.gym.system.network.client.AcademiaClient;

public class Main {
    public static void main(String[] args) throws Exception {
        Academia sistema = new Academia();

        if (args.length > 0 && args[0].equalsIgnoreCase("server")) {
            new AcademiaServer(8080, sistema).iniciar();
        } else {
            // Modo Cliente (exemplo rápido)
            AcademiaClient client = new AcademiaClient("localhost", 8080);
            client.conectar();

            // Teste rápido
            System.out.println(client.listarVisitantes().length + " visitantes encontrados.");
            client.desconectar();
        }
    }
}