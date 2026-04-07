package com.gym.system.model;

import java.io.Serializable;

public class Endereco implements Serializable {
    private int numero;
    private String rua;
    private String bairro;
    private String estado;

    public Endereco(String rua, int numero, String bairro, String estado) {
        this.numero = numero;
        this.rua = rua;
        this.bairro = bairro;
        this.estado = estado;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
