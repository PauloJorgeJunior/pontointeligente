package com.paulojj.pontointligente.services

import com.paulojj.pontointligente.documents.Funcionario
import java.util.*

interface FuncionarioService {
    fun persistir(funcionario: Funcionario): Funcionario
    fun buscaPorCpf(cpf: String): Funcionario?
    fun buscaPorEmail(email: String): Funcionario?
    fun buscaPorId(id: String): Optional<Funcionario>
}