package com.gym.system.io;

import com.gym.system.model.Funcionario;
import com.gym.system.model.enums.Turno;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioInputStream extends InputStream {
    private InputStream origem;
    private int bytesPorAtributo;

    public FuncionarioInputStream(InputStream origem, int bytesPorAtributo) {
        this.origem = origem;
        this.bytesPorAtributo = bytesPorAtributo;
    }

    // Lê uma quantidade N de funcionarios do stream
    public List<Funcionario> lerFuncionarios(int quantidade) throws IOException {
        List<Funcionario> lista = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            // Ordem: CPF -> Nome -> Salário
            int cpf = lerIntComoBytes();
            String nome = lerStringLimitada();
            float salario = lerFloatComoBytes();

            // Criando um objeto parcial para representar o que chegou.
            Funcionario f = new Funcionario(cpf, 0, nome, null, null, null, null, salario, Turno.MANHA);
            f.setSalario(salario);

            lista.add(f);
        }
        return lista;
    }

    private String lerStringLimitada() throws IOException {
        byte[] buffer = new byte[bytesPorAtributo];
        int lidos = origem.read(buffer);
        if (lidos == -1) throw new IOException("Fim do stream inesperado");

        // Converte para String e remove os bytes zero (padding) do final
        return new String(buffer, StandardCharsets.UTF_8).trim();
    }

    private int lerIntComoBytes() throws IOException {
        int resultado = 0;
        // Lê os 4 bytes do int (ou a quantidade definida em bytesPorAtributo)
        for (int i = 0; i < 4; i++) {
            int b = origem.read();
            if (b == -1) throw new IOException("Erro ao ler int");
            resultado = (resultado << 8) | (b & 0xFF);
        }

        // Se bytesPorAtributo for > 4, "pula" os bytes de preenchimento restantes
        for (int i = 4; i < bytesPorAtributo; i++) {
            origem.read();
        }
        return resultado;
    }

    private float lerFloatComoBytes() throws IOException {
        int bits = 0;
        // Lê os 4 bytes e monta o inteiro original
        for (int i = 0; i < 4; i++) {
            int b = origem.read();
            if (b == -1) throw new IOException("Erro ao ler float");
            bits = (bits << 8) | (b & 0xFF);
        }

        // Pula o preenchimento (padding) se houver
        for (int i = 4; i < bytesPorAtributo; i++) {
            origem.read();
        }

        // Converte os bits de volta para o número decimal (float)
        return Float.intBitsToFloat(bits);
    }

    @Override
    public int read() throws IOException {
        return origem.read();
    }
}
