package com.paulojj.pontointligente

import com.paulojj.pontointligente.documents.Empresa
import com.paulojj.pontointligente.documents.Funcionario
import com.paulojj.pontointligente.enums.PerfilEnum
import com.paulojj.pontointligente.repositories.EmpresaRepository
import com.paulojj.pontointligente.repositories.FuncionarioRepository
import com.paulojj.pontointligente.repositories.LancamentoRepository
import com.paulojj.pontointligente.utils.SenhaUtils
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = arrayOf(SecurityAutoConfiguration::class))
class PontointligenteApplication(val empresaRepository: EmpresaRepository,
                                 val funcionarioRepository: FuncionarioRepository,
                                 val lancamentoRepository: LancamentoRepository) : CommandLineRunner {

    override fun run(vararg args: String?) {
        empresaRepository.deleteAll()
        funcionarioRepository.deleteAll()
        lancamentoRepository.deleteAll()

        var empresa: Empresa = Empresa("Empresa", "10443887000146")
        empresa = empresaRepository.save(empresa)

        var admin: Funcionario = Funcionario("Admin", "admin@empresa.com", SenhaUtils().gerarBcrypt("123456"), "25708317000", PerfilEnum.ROLE_ADMIN, empresa.id!!)
        admin = funcionarioRepository.save(admin)

        var funcionario: Funcionario = Funcionario("Funcionario", "funcionario@empresa.com", SenhaUtils().gerarBcrypt("123456"), "44325441557", PerfilEnum.ROLE_USUARIO, empresa.id!!)
        funcionario = funcionarioRepository.save(funcionario)

        println("Empresa ID: " + empresa.id)
        println("Admin ID: " + admin.id)
        println("Funcionário ID: " + funcionario.id)
    }


}

fun main(args: Array<String>) {
    runApplication<PontointligenteApplication>(*args)
}

