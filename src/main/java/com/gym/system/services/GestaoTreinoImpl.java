package com.gym.system.services;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.system.interfaces.IGestaoTreino;
import com.gym.system.models.Aluno;
import com.gym.system.models.TreinoDiario;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

public class GestaoTreinoImpl extends UnicastRemoteObject implements IGestaoTreino {
    // Mesma configuração dos seus AlunoInputStream/OutputStream
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);

    // Banco em memória para persistência entre chamadas RMI
    private final Map<String, Aluno> bancoAlunos = new HashMap<>();

    public GestaoTreinoImpl() throws RemoteException { super(); }

    @Override
    public byte[] doOperation(String objectRef, String methodId, byte[] arguments) throws RemoteException {
        try {
            JsonNode req = MAPPER.readTree(arguments);
            JsonNode resp;

            switch (methodId) {
                case "criarTreino" -> {
                    Aluno a = MAPPER.treeToValue(req.get("aluno"), Aluno.class);
                    DayOfWeek d = DayOfWeek.valueOf(req.get("dia").asText().toUpperCase());
                    TreinoDiario t = MAPPER.treeToValue(req.get("treino"), TreinoDiario.class);

                    // Persiste no servidor
                    bancoAlunos.putIfAbsent(a.getMatricula(), a);
                    Aluno stored = bancoAlunos.get(a.getMatricula());
                    stored.getCronograma().put(d, t);

                    resp = MAPPER.createObjectNode().put("status", "created").put("matricula", a.getMatricula());
                }
                case "buscarTreino" -> {
                    String mat = req.get("matricula").asText();
                    DayOfWeek d = DayOfWeek.valueOf(req.get("dia").asText().toUpperCase());
                    Aluno a = bancoAlunos.get(mat);
                    TreinoDiario t = (a != null) ? a.getCronograma().get(d) : null;
                    resp = t != null ? MAPPER.valueToTree(t) : MAPPER.createObjectNode().put("status", "not_found");
                }
                case "atualizarTreino" -> {
                    String mat = req.get("matricula").asText();
                    DayOfWeek d = DayOfWeek.valueOf(req.get("dia").asText().toUpperCase());
                    TreinoDiario t = MAPPER.treeToValue(req.get("treino"), TreinoDiario.class);
                    Aluno a = bancoAlunos.get(mat);
                    if (a != null) a.getCronograma().put(d, t);
                    resp = MAPPER.createObjectNode().put("status", "updated");
                }
                case "excluirTreino" -> {
                    String mat = req.get("matricula").asText();
                    DayOfWeek d = DayOfWeek.valueOf(req.get("dia").asText().toUpperCase());
                    Aluno a = bancoAlunos.get(mat);
                    if (a != null) a.getCronograma().remove(d);
                    resp = MAPPER.createObjectNode().put("status", "deleted");
                }
                default -> resp = MAPPER.createObjectNode().put("error", "Método inválido: " + methodId);
            }
            return MAPPER.writeValueAsBytes(resp);

        } catch (IOException e) {
            throw new RemoteException("Erro de processamento JSON", e);
        } catch (Exception e) {
            throw new RemoteException("Erro interno no servidor", e);
        }
    }
}