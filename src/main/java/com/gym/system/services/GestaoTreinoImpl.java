package com.gym.system.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.gym.system.interfaces.IGestaoTreino;
import com.gym.system.models.Aluno;
import com.gym.system.models.TreinoDiario;
import com.gym.system.util.JsonIOUtil;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

public class GestaoTreinoImpl extends UnicastRemoteObject implements IGestaoTreino {
    private final Map<String, Aluno> bancoAlunos = new HashMap<>();

    public GestaoTreinoImpl() throws RemoteException { super(); }

    @Override
    public byte[] doOperation(String objectRef, String methodId, byte[] arguments) throws RemoteException {
        JsonNode request = getRequest(arguments);
        JsonNode response = processMethod(methodId, request);
        return sendReply(response);
    }

    private JsonNode getRequest(byte[] rawRequest) throws RemoteException {
        try {
            return JsonIOUtil.getMapper().readTree(rawRequest);
        } catch (Exception e) {
            throw new RemoteException("Falha ao obter requisição (getRequest)", e);
        }
    }

    private byte[] sendReply(JsonNode response) throws RemoteException {
        try {
            return JsonIOUtil.getMapper().writeValueAsBytes(response);
        } catch (Exception e) {
            throw new RemoteException("Falha ao enviar resposta (sendReply)", e);
        }
    }

    private JsonNode processMethod(String methodId, JsonNode req) {
        try {
            return switch (methodId) {
                case "criarTreino" -> {
                    Aluno a = JsonIOUtil.getMapper().treeToValue(req.get("aluno"), Aluno.class);
                    DayOfWeek d = DayOfWeek.valueOf(req.get("dia").asText().toUpperCase());
                    TreinoDiario t = JsonIOUtil.getMapper().treeToValue(req.get("treino"), TreinoDiario.class);
                    bancoAlunos.putIfAbsent(a.getMatricula(), a);
                    bancoAlunos.get(a.getMatricula()).getCronograma().put(d, t);
                    yield JsonIOUtil.getMapper().createObjectNode().put("status", "created");
                }
                case "buscarTreino" -> {
                    String mat = req.get("matricula").asText();
                    DayOfWeek d = DayOfWeek.valueOf(req.get("dia").asText().toUpperCase());
                    Aluno a = bancoAlunos.get(mat);
                    if (a != null && a.getCronograma().containsKey(d)) {
                        yield JsonIOUtil.getMapper().valueToTree(a.getCronograma().get(d));
                    } else {
                        yield JsonIOUtil.getMapper().createObjectNode().put("status", "not_found");
                    }
                }
                case "atualizarTreino" -> {
                    String mat = req.get("matricula").asText();
                    DayOfWeek d = DayOfWeek.valueOf(req.get("dia").asText().toUpperCase());
                    TreinoDiario t = JsonIOUtil.getMapper().treeToValue(req.get("treino"), TreinoDiario.class);
                    Aluno a = bancoAlunos.get(mat);
                    if (a != null) a.getCronograma().put(d, t);
                    yield JsonIOUtil.getMapper().createObjectNode().put("status", "updated");
                }
                case "excluirTreino" -> {
                    String mat = req.get("matricula").asText();
                    DayOfWeek d = DayOfWeek.valueOf(req.get("dia").asText().toUpperCase());
                    Aluno a = bancoAlunos.get(mat);
                    if (a != null) a.getCronograma().remove(d);
                    yield JsonIOUtil.getMapper().createObjectNode().put("status", "deleted");
                }
                case "avaliarDesempenho" -> {
                    String mat = req.get("matricula").asText();
                    Aluno a = bancoAlunos.get(mat);
                    if (a == null) throw new RuntimeException("Aluno não encontrado");
                    int total = a.getCronograma().size();
                    yield JsonIOUtil.getMapper().createObjectNode()
                            .put("aluno", a.getNome())
                            .put("treinos_semanais", total);
                }
                default -> JsonIOUtil.getMapper().createObjectNode().put("error", "Método inválido: " + methodId);
            };
        } catch (Exception e) {
            return JsonIOUtil.getMapper().createObjectNode().put("error", e.getMessage());
        }
    }
}