package com.gym.system.io;

import com.gym.system.model.Funcionario;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class FuncionarioOutputStream extends OutputStream {
    private Funcionario[] funcionarios;
    private int quantidade;
    private int bytesPorAtributo;
    private OutputStream destino;

    public FuncionarioOutputStream(Funcionario[] funcionarios, int quantidade, int bytesPorAtributo, OutputStream destino) {
        this.funcionarios = funcionarios;
        this.quantidade = quantidade;
        this.bytesPorAtributo = bytesPorAtributo;
        this.destino = destino;
    }

    // Método que percorre o array e envia os dados
    public void enviarDados() throws IOException {
        for (int i = 0; i < quantidade; i++) {
            Funcionario f = funcionarios[i];

            // Atributos: CPF (int) -> Nome (String) -> Salário (float)
            escreverIntComoBytes(f.getCpf());
            escreverStringLimitada(f.getNome());
            escreverFloatComoBytes(f.getSalario());
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

        // Se o seu 'bytesPorAtributo' for maior que 4, preenche o resto
        for(int i = 4; i < bytesPorAtributo; i++) {
            destino.write(0);
        }
    }

    private void escreverFloatComoBytes(float valor) throws IOException {
        // Transformando o float em um int de 32 bits mantendo a precisão decimal
        int bits = Float.floatToRawIntBits(valor);

        // Mesma lógica do Int
        destino.write((bits >>> 24) & 0xFF);
        destino.write((bits >>> 16) & 0xFF);
        destino.write((bits >>> 8) & 0xFF);
        destino.write(bits & 0xFF);

        // Preenchimento (padding) se o bytesPorAtributo for maior que 4
        for(int i = 4; i < bytesPorAtributo; i++) {
            destino.write(0);
        }
    }

    @Override
    public void write(int b) throws IOException {
        destino.write(b);
    }
}
