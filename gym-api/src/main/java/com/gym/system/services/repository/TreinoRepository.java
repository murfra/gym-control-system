package com.gym.system.services.repository;
import com.gym.system.models.redis.AlunoRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
/**
* Repository para persistência de Alunos no Redis.
*/
@Repository public interface TreinoRepository extends CrudRepository<AlunoRedis, String> {

    /**
    * Encontra um aluno pela matrícula.
    */
    default AlunoRedis findByMatricula(String matricula)  {
        if (matricula == null || matricula.isBlank())  {
            return null;
        }
        return findById(matricula).orElse(null);
    }

    /**
    * Verifica se existe aluno com a matrícula informada.
    */
    default boolean existsByMatricula(String matricula)  {
        if (matricula == null || matricula.isBlank())  {
            return false;
        }
        return existsById(matricula);
    }

    /**
    * Remove um aluno pela matrícula.
    */
    default void deleteByMatricula(String matricula)  {
        if (matricula != null && !matricula.isBlank())  {
            deleteById(matricula);
        }
    }

    /**
    * Busca aluno por CPF.
    */
    default AlunoRedis findByCpf(String cpf)  {
        if (cpf == null || cpf.isBlank())  {
            return null;
        }
        for (AlunoRedis aluno : findAll())  {
            if (cpf.equals(aluno.getCpf()))  {
                return aluno;
            }
        }
        return null;
    }

    /**
    * Busca aluno por e-mail.
    */
    default AlunoRedis findByEmail(String email)  {
        if (email == null || email.isBlank())  {
            return null;
        }
        for (AlunoRedis aluno : findAll())  {
            if (email.equalsIgnoreCase(aluno.getEmail()))  {
                return aluno;
            }
        }
        return null;
    }
}