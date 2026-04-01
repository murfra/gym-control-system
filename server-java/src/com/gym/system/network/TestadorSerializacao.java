package com.gym.system.network;

import com.gym.system.io.VisitanteOutputStream;
import com.gym.system.io.InstrutorInputStream;
import com.gym.system.io.InstrutorOutputStream;
import com.gym.system.model.Visitante;
import com.gym.system.model.Instrutor;

import java.io.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Classe de teste para demonstrar o funcionamento dos Streams customizados
 * com diferentes tipos de destino/origem:
 * 1. System.out / System.in
 * 2. Arquivos (File)
 * 3. Servidor remoto (Socket TCP)
 */
public class TestadorSerializacao {
    private static final int BYTES_POR_ATRIBUTO = 20;

    public static void main(String[] args) {
        try {
            System.out.println("╔════════════════════════════════════════════════╗");
            System.out.println("║     TESTADOR DE SERIALIZAÇÃO - GYM SYSTEM      ║");
            System.out.println("╚════════════════════════════════════════════════╝\n");

            testeVisitantesConsole();
            testeInstrutoresArquivo();
            
            System.out.println("\n✓ Testes completados com sucesso!");

        } catch (Exception e) {
            System.err.println("✗ Erro durante os testes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Teste 1: Visitantes → Console
     * Demonstra envio de dados para System.out
     */
    static void testeVisitantesConsole() throws IOException {
        System.out.println("┌─ Teste 1: Visitantes para Console ──────────────┐");

        Visitante[] visitantes = {
            new Visitante(12345678, 987654321, "João", LocalDate.of(1990, 5, 15), "joao@email.com", null),
            new Visitante(87654321, 123456789, "Maria", LocalDate.of(1995, 8, 20), "maria@email.com", null),
            new Visitante(55555555, 666666666, "Pedro", LocalDate.of(1988, 3, 10), "pedro@email.com", null)
        };

        System.out.println("Enviando " + visitantes.length + " visitantes...\n");
        
        VisitanteOutputStream vos = new VisitanteOutputStream(visitantes, visitantes.length, BYTES_POR_ATRIBUTO, System.out);
        vos.enviarDados();

        System.out.println("\n└─────────────────────────────────────────────────┘\n");
    }

    /**
     * Teste 2: Instrutores → Arquivo → Ler do Arquivo
     * Demonstra persistência em arquivo e leitura posterior
     */
    static void testeInstrutoresArquivo() throws IOException {
        System.out.println("┌─ Teste 2: Instrutores em Arquivo ──────────────┐");

        Instrutor[] instrutores = {
            new Instrutor(11111111, 111111111, "Carlos", LocalDate.of(1985, 1, 5), "carlos@gym.com", null),
            new Instrutor(22222222, 222222222, "Ana", LocalDate.of(1992, 6, 12), "ana@gym.com", null),
            new Instrutor(33333333, 333333333, "Bruno", LocalDate.of(1989, 9, 25), "bruno@gym.com", null)
        };

        String arquivo = "teste_instrutores.bin";

        // Escrita
        System.out.println("Escrevendo " + instrutores.length + " instrutores em: " + arquivo);
        FileOutputStream fos = new FileOutputStream(arquivo);
        InstrutorOutputStream ios = new InstrutorOutputStream(instrutores, instrutores.length, BYTES_POR_ATRIBUTO, fos);
        ios.writeSystem();
        fos.close();

        File f = new File(arquivo);
        System.out.println("✓ Arquivo criado com sucesso!");
        System.out.println("  Tamanho: " + f.length() + " bytes");

        // Leitura
        System.out.println("\nLendo dados do arquivo...");
        FileInputStream fis = new FileInputStream(arquivo);
        InstrutorInputStream iis = new InstrutorInputStream(fis, BYTES_POR_ATRIBUTO);
        List<Instrutor> instrutoresLidos = iis.lerInstrutores(instrutores.length);
        fis.close();

        System.out.println("✓ " + instrutoresLidos.size() + " instrutor(es) lido(s):");
        for (Instrutor inst : instrutoresLidos) {
            System.out.println("  → " + inst.getNome() + " | CPF: " + inst.getCpf() + " | Email: " + inst.getEmail());
        }

        // Limpeza
        f.delete();
        System.out.println("\nArquivo deletado.");
        System.out.println("└─────────────────────────────────────────────────┘\n");
    }
}
