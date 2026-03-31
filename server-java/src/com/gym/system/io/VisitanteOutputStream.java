package com.gym.system.io;

import com.gym.system.model.Visitante;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VisitanteOutputStream extends OutputStream {
    private Visitante[] visitantes;
    private int quantidade;
    private int bytesPorAtributo;
    private OutputStream destino;

    public VisitanteOutputStream(Visitante[] visitantes, int quantidade, int bytesPorAtributo, OutputStream destino) {
        this.visitantes = visitantes;
        this.quantidade = quantidade;
        this.bytesPorAtributo = bytesPorAtributo;
        this.destino = destino;
    }

    // Método que percorre o array e envia os dados
    public void enviarDados() throws IOException {
        for (int i = 0; i < quantidade; i++) {
            Visitante v = visitantes[i];

            // Atributos: CPF (int) -> Nome (String) -> Email (String) -> Data de Visita (String/Date)
            escreverIntComoBytes(v.getCpf());
            escreverStringLimitada(v.getNome());
            escreverStringLimitada(v.getEmail());

            // Data de visita (convertendo LocalDate para String no formato yyyy-MM-dd)
            String dataVisita = (v.getDataVisita() != null) ? 
                v.getDataVisita().format(DateTimeFormatter.ISO_LOCAL_DATE) : "1970-01-01";
            escreverStringLimitada(dataVisita);
        }
        destino.flush();
    }

    // Auxiliar para gravar Strings respeitando o limite de bytes do enunciado
    private void escreverStringLimitada(String texto) throws IOException {
        byte[] b = texto.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < bytesPorAtributo; i++) {
            if (i < b.length) {
                destino.write(b[i]);
            } else {
                destino.write(0); // Preenche com byte zero se a string for menor que o limite
            }
        }
    }

    // Auxiliar para gravar o Int (4 bytes) - ajustando para caber no limite definido
    private void escreverIntComoBytes(int valor) throws IOException {
        // Envia o int byte a byte
        destino.write((valor >>> 24) & 0xFF);
        destino.write((valor >>> 16) & 0xFF);
        destino.write((valor >>> 8) & 0xFF);
        destino.write(valor & 0xFF);

        // Se o bytesPorAtributo for maior que 4, preenche o resto
        for(int i = 4; i < bytesPorAtributo; i++) {
            destino.write(0);
        }
    }

    @Override
    public void write(int b) throws IOException {
        destino.write(b);
    }
}
