package com.gym.system;

import com.gym.system.core.Academia;
import com.gym.system.network.server.AcademiaServer;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        Academia academia = new Academia();
        AcademiaServer server = new AcademiaServer(12345, academia, 60000);
        server.iniciar();
    }
}
