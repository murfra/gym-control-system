package com.gym.system.io;

import com.gym.system.model.Aluno;
import com.gym.system.model.enums.Experiencia;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AlunoInputStream extends InputStream {
    private InputStream origem;
    private int bytesPorAtributo;

    public AlunoInputStream(InputStream origem, int bytesPorAtributo) {
        this.origem = origem;
        this.bytesPorAtributo = bytesPorAtributo;
    }

    // Lê uma quantidade N de alunos do stream
    public List<Aluno> lerAlunos(int quantidade) throws IOException {
        List<Aluno> lista = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            // Ordem: CPF -> Nome -> Email -> Nível de Experiencia
            int cpf = lerIntComoBytes();
            String nome = lerStringLimitada();
            String email = lerStringLimitada();
            String nivelExperiencia = lerStringLimitada(); // Aqui é carregado o nome do Enum
            Experiencia exp = Experiencia.valueOf(nivelExperiencia); // Convertendo o nome para o tipo Experiencia

            /* Criando um objeto parcial para representar o que chegou
            * Ao criar um novo aluno, o construtor dele automaticamente cria uma nova matrícula, nesse caso, o que pode
            * ser feito seria, após instanciar aqui o novo Aluno, chamar o método de setar sua matrícula.
            * Esse método não foi implementado pois pode ocorrer conflito de matrícula.
            */
            Aluno a = new Aluno(cpf, 0, nome, null, email, null, exp);

            lista.add(a);
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

    @Override
    public int read() throws IOException {
        return origem.read();
    }
}
