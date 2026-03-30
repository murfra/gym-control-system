package com.gym.system.io;

import com.gym.system.model.Aluno;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class AlunoOutputStream extends OutputStream {
    private Aluno[] alunos;
    private int quantidade;
    private int bytesPorAtributo;
    private OutputStream destino;

    public AlunoOutputStream(Aluno[] alunos, int quantidade, int bytesPorAtributo, OutputStream destino) {
        this.alunos = alunos;
        this.quantidade = quantidade;
        this.bytesPorAtributo = bytesPorAtributo;
        this.destino = destino;
    }

    // Método que percorre o array e envia os dados
    public void enviarDados() throws IOException {
        for (int i = 0; i < quantidade; i++) {
            Aluno a = alunos[i];

            // Atributos: CPF (int) -> Nome (String) -> Email (String) -> Nível de Experiência (Enum => String)
            escreverIntComoBytes(a.getCpf());
            escreverStringLimitada(a.getNome());
            escreverStringLimitada(a.getEmail());

            // nivelExperiencia (Convertendo o Enum para String)
            String exp = (a.getNivelExperiencia() != null) ? a.getNivelExperiencia().name() : "INICIANTE";
            escreverStringLimitada(exp);
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
        // Envia o int byte a byte (exemplo simplificado para respeitar a lógica de fluxo)
        destino.write((valor >>> 24) & 0xFF);
        destino.write((valor >>> 16) & 0xFF);
        destino.write((valor >>> 8) & 0xFF);
        destino.write(valor & 0xFF);

        // Se o seu 'bytesPorAtributo' for maior que 4, preenche o resto
        for(int i = 4; i < bytesPorAtributo; i++) {
            destino.write(0);
        }
    }

    @Override
    public void write(int b) throws IOException {
        destino.write(b);
    }
}
