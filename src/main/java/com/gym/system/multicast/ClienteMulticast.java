package com.gym.system.multicast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class ClienteMulticast implements Runnable {
    private static final String GRUPO_MULTICAST = "239.255.0.1";
    private static final int PORTA = 5000;

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final String nomeUsuario;
    private volatile boolean rodando = true;

    public ClienteMulticast(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    @Override
    public void run() {
        try (MulticastSocket socket = new MulticastSocket(PORTA)) {
            InetAddress grupo = InetAddress.getByName(GRUPO_MULTICAST);
            socket.joinGroup(grupo);
            System.out.println("[" + nomeUsuario + "] Conectado ao canal de avisos...");

            byte[] buffer = new byte[2048];
            while (rodando) {
                DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);
                socket.receive(pacote);

                // Converte apenas os bytes válidos recebidos, evitando lixo no final do buffer
                String jsonRecebido = new String(pacote.getData(), 0, pacote.getLength(), StandardCharsets.UTF_8);
                Aviso aviso = mapper.readValue(jsonRecebido, Aviso.class);

                System.out.println("\nNOVO AVISO para " + nomeUsuario + ":");
                System.out.println("   " + aviso);
            }
        } catch (IOException e) {
            if (rodando) {
                System.err.println("Erro no cliente: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void parar() {
        rodando = false;
    }

    public static void main(String[] args) {
        String nome = args.length > 0 ? args[0] : "Usuário Teste";
        new Thread(new ClienteMulticast(nome)).start();
    }
}