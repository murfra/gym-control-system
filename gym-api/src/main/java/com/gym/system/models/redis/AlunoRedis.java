package com.gym.system.models.redis;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gym.system.models.Aluno;
import com.gym.system.models.Endereco;
import com.gym.system.models.enums.Experiencia;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe serializável para persistência de Aluno no Redis
 */
@RedisHash("Aluno")
public class AlunoRedis extends Aluno implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    public AlunoRedis() {
        super("", "", LocalDate.now(), "", "", null, Experiencia.INICIANTE);
        this.id = null;
    }

    public AlunoRedis(String cpf, String nome, LocalDate dataNascimento, String telefone,
                      String email, Endereco endereco, Experiencia nivelExperiencia) {
        super(cpf, nome, dataNascimento, telefone, email, endereco, nivelExperiencia);
        this.id = this.getMatricula();
    }

    public AlunoRedis(Aluno aluno) {
        super(aluno.getCpf(), aluno.getNome(), aluno.getDataNascimento(), aluno.getTelefone(),
                aluno.getEmail(), aluno.getEndereco(), aluno.getNivelExperiencia());
        this.setMatricula(aluno.getMatricula());
        this.id = aluno.getMatricula();
        this.setCronograma(new HashMap<>(aluno.getCronograma()));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
