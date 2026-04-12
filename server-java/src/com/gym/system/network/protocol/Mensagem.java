package com.gym.system.network.protocol;

public class Mensagem {
    public enum Tipo {
        CADASTRAR_VISITANTE, LISTAR_VISITANTES,
        CADASTRAR_INSTRUTOR, LISTAR_INSTRUTORES,
        SUCESSO, ERRO
    }

    private final Tipo tipo;
    private final byte[] payload; // Contém os bytes gerados pelos seus Streams

    public Mensagem(Tipo tipo, byte[] payload) {
        this.tipo = tipo;
        this.payload = payload;
    }

    public Tipo getTipo() { return tipo; }
    public byte[] getPayload() { return payload; }
}