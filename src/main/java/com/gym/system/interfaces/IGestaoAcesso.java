package com.gym.system.interfaces;

import com.gym.system.models.Pessoa;
import com.gym.system.models.Visitante;

public interface IGestaoAcesso {
    public boolean validarEntrada(Pessoa p);
    public void registrarVisita(Visitante v);
}
