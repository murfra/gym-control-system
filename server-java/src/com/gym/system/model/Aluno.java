package com.gym.system.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Aluno extends Pessoa {
    // AtomicInteger é utilizado para caso haja concorrência (multithreading)
    private static final AtomicInteger contadorMatricula = new AtomicInteger(1);

    private int numeroMatricula;
    private LocalDate dataMatricula;
    private Experiencia nivelExperiencia;
    private LocalDate dataUltimoPagamento;
    // O Map garante que cada dia tenha apenas um treino
    private Map<DayOfWeek, TreinoDiario> cronograma = new HashMap<>();

    public Aluno(int cpf, int telefone, String nome, LocalDate dataNascimento, String email, Endereco endereco) {
        this.numeroMatricula = contadorMatricula.getAndIncrement();
        this.dataMatricula = LocalDate.now();
        this.nivelExperiencia = Experiencia.INICIANTE;
        super(cpf, telefone, nome, dataNascimento, email, endereco);
    }

    public Aluno(int cpf, int telefone, String nome, LocalDate dataNascimento, String email, Endereco endereco, Experiencia nivelExperiencia) {
        this(cpf, telefone, nome, dataNascimento, email, endereco);
        this.nivelExperiencia = nivelExperiencia;
    }

    public int getNumeroMatricula() {
        return numeroMatricula;
    }

    public LocalDate getDataMatricula() {
        return dataMatricula;
    }

    public Experiencia getNivelExperiencia() {
        return nivelExperiencia;
    }

    public void setNivelExperiencia(Experiencia nivelExperiencia) {
        this.nivelExperiencia = nivelExperiencia;
    }

    public LocalDate getDataUltimoPagamento() {
        return dataUltimoPagamento;
    }

    public void setDataUltimoPagamento(LocalDate dataUltimoPagamento) {
        this.dataUltimoPagamento = dataUltimoPagamento;
    }

    // Utilizado pelo serviço GestaoTreino
    public Map<DayOfWeek, TreinoDiario> getCronograma() {
        return cronograma;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
