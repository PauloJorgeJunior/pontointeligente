package com.paulojj.pontointligente.services.impl

import com.paulojj.pontointligente.documents.Funcionario
import com.paulojj.pontointligente.repositories.FuncionarioRepository
import com.paulojj.pontointligente.services.FuncionarioService
import org.springframework.stereotype.Service
import java.util.*

@Service
class FuncionarioServiceImpl(val funcionarioRepository: FuncionarioRepository) : FuncionarioService {
    override fun persistir(funcionario: Funcionario): Funcionario =
            funcionarioRepository.save(funcionario)

    override fun buscaPorCpf(cpf: String): Funcionario? = funcionarioRepository.findByCpf(cpf)

    override fun buscaPorEmail(email: String): Funcionario? = funcionarioRepository.findByEmail(email)

    override fun buscaPorId(id: String): Optional<Funcionario> = funcionarioRepository.findById(id)
}