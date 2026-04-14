package com.gym.system;

import com.gym.system.core.Academia;
import com.gym.system.network.server.AcademiaServer;

public class ServerMain {
    public static void main(String[] args) {
        Academia academia = new Academia();
        AcademiaServer server = new AcademiaServer(12345, academia);
        server.iniciar();
    }
}
