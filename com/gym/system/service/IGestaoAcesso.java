package com.gym.system.service;

import com.gym.system.model.Pessoa;
import com.gym.system.model.Visitante;

public interface IGestaoAcesso {
    public boolean validarEntrada(Pessoa p);
    public void registrarVisita(Visitante v);
}
