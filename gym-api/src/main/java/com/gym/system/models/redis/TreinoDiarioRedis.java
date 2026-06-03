package com.gym.system.models.redis;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gym.system.models.TreinoDiario;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe serializável para persistência de TreinoDiario no Redis
 */
public class TreinoDiarioRedis extends TreinoDiario implements Serializable {
    private static final long serialVersionUID = 1L;

    public TreinoDiarioRedis() {
        super();
    }

    public TreinoDiarioRedis(TreinoDiario treino) {
        if (treino != null) {
            this.setDiaDaSemana(treino.getDiaDaSemana());
            this.setGrupoMuscular(treino.getGrupoMuscular());
            this.setExercicios(new ArrayList<>(treino.getExercicios()));
        }
    }
}
