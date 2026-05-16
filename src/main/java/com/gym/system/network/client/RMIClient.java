package com.gym.system.network.client;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.system.interfaces.IGestaoTreino;
import com.gym.system.models.Aluno;
import com.gym.system.models.TreinoDiario;
import com.gym.system.models.enums.Experiencia;

import java.nio.charset.StandardCharsets;
import java.rmi.Naming;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class RMIClient {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);

    public static void main(String[] args) {
        try {
            String url = "rmi://localhost/gestaoTreino";
            IGestaoTreino servico = (IGestaoTreino) Naming.lookup(url);

            // 1. Cria objeto Aluno e Treino
            Aluno aluno = new Aluno("123.456.789-00", "João Silva", LocalDate.of(1998, 3, 15), "99999-0000", "joao@email.com", null, Experiencia.INICIANTE);
            // Fixa matrícula para teste determinístico
            aluno.setMatricula("ALU-TEST-01");

            TreinoDiario treino = new TreinoDiario();
            treino.setDiaDaSemana(DayOfWeek.MONDAY);
            treino.setGrupoMuscular("Peito & Tríceps");

            // 2. Monta JSON da requisição
            ObjectNode reqCriar = MAPPER.createObjectNode();
            reqCriar.set("aluno", MAPPER.valueToTree(aluno));
            reqCriar.put("dia", "MONDAY");
            reqCriar.set("treino", MAPPER.valueToTree(treino));

            // 3. Invoca criarTreino
            byte[] respCriar = servico.doOperation("gestaoTreino", "criarTreino", MAPPER.writeValueAsBytes(reqCriar));
            System.out.println("Criar: " + new String(respCriar, StandardCharsets.UTF_8));

            // 4. Invoca buscarTreino (usa a mesma matrícula para garantir persistência)
            ObjectNode reqBuscar = MAPPER.createObjectNode();
            reqBuscar.put("matricula", "ALU-TEST-01");
            reqBuscar.put("dia", "MONDAY");

            byte[] respBuscar = servico.doOperation("gestaoTreino", "buscarTreino", MAPPER.writeValueAsBytes(reqBuscar));
            System.out.println("Buscar: " + new String(respBuscar, StandardCharsets.UTF_8));

        } catch (Exception e) {
            System.err.println("Erro no Cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}