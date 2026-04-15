package com.gym.system.multicast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ServidorMulticast {
    private static final String GRUPO_MULTICAST = "239.255.0.1";
    private static final int PORTA = 5000;
    private static final int TTL = 1;

    // ObjectMapper thread-safe e configurado para datas legíveis (ISO-8601)
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static void main(String[] args) {
        try (MulticastSocket socket = new MulticastSocket();
             Scanner scanner = new Scanner(System.in)) {

            InetAddress grupo = InetAddress.getByName(GRUPO_MULTICAST);
            socket.setTimeToLive(TTL);

            System.out.println("Servidor Multicast de Avisos (JSON) iniciado.");
            System.out.println("Tipos válidos: FECHAMENTO, PROMOCAO, MANUTENCAO, OUTRO\n");

            while (true) {
                System.out.print("Tipo do aviso: ");
                String tipoStr = scanner.nextLine().trim().toUpperCase();
                Aviso.Tipo tipo = Aviso.Tipo.valueOf(tipoStr);

                System.out.print("Mensagem: ");
                String msg = scanner.nextLine();

                Aviso aviso = new Aviso(tipo, msg);
                byte[] dados = mapper.writeValueAsString(aviso).getBytes(StandardCharsets.UTF_8);

                DatagramPacket pacote = new DatagramPacket(dados, dados.length, grupo, PORTA);
                socket.send(pacote);
                System.out.println("Aviso enviado com sucesso!\n");
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}