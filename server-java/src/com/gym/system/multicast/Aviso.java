package com.gym.system.multicast;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Aviso implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Tipo { FECHAMENTO, PROMOCAO, MANUTENCAO, OUTRO }

    private Tipo tipo;
    private String mensagem;
    private LocalDateTime dataEnvio;

    // Construtor vazio exigido pelo Jackson
    public Aviso() {}

    public Aviso(Tipo tipo, String mensagem) {
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.dataEnvio = LocalDateTime.now();
    }

    // Getters
    public Tipo getTipo() { return tipo; }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public String getMensagem() { return mensagem; }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDateTime getDataEnvio() { return dataEnvio; }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    @Override
    public String toString() {
        return "[" + dataEnvio + "] " + tipo + ": " + mensagem;
    }
}