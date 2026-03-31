package com.gym.system.io;

import com.gym.system.model.Visitante;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VisitanteInputStream extends InputStream {
    private InputStream origem;
    private int bytesPorAtributo;

    public VisitanteInputStream(InputStream origem, int bytesPorAtributo) {
        this.origem = origem;
        this.bytesPorAtributo = bytesPorAtributo;
    }

    // Lê uma quantidade N de visitantes do stream
    public List<Visitante> lerVisitantes(int quantidade) throws IOException {
        List<Visitante> lista = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            // Ordem: CPF -> Nome -> Email -> Data de Visita
            int cpf = lerIntComoBytes();
            String nome = lerStringLimitada();
            String email = lerStringLimitada();
            String dataVisitaStr = lerStringLimitada();
            
            // Conversão de String para LocalDate
            LocalDate dataVisita = null;
            try {
                dataVisita = LocalDate.parse(dataVisitaStr, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception e) {
                dataVisita = LocalDate.now();
            }

            // Criando um objeto Visitante com os dados recebidos
            Visitante v = new Visitante(cpf, 0, nome, null, email, null);
            
            lista.add(v);
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
