package com.paulojj.pontointligente.services.impl

import com.paulojj.pontointligente.documents.Empresa
import com.paulojj.pontointligente.repositories.EmpresaRepository
import com.paulojj.pontointligente.services.EmpresaService
import org.springframework.stereotype.Service

@Service
class EmpresaServiceImpl(val empresaRepository: EmpresaRepository) : EmpresaService {

    override fun buscarPorCnpj(cnpj: String): Empresa? = empresaRepository.findByCnpj(cnpj)

    override fun persistir(empresa: Empresa): Empresa = empresaRepository.save(empresa)
}