package com.paulojj.pontointligente.services

import com.paulojj.pontointligente.documents.Funcionario
import com.paulojj.pontointligente.enums.PerfilEnum
import com.paulojj.pontointligente.repositories.FuncionarioRepository
import com.paulojj.pontointligente.utils.SenhaUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class FuncionarioServiceTest {
    @MockBean
    private val funcionarioRepository: FuncionarioRepository? = null

    @Autowired
    private val funcionarioService: FuncionarioService? = null

    private val EMAIL: String = "email@email.com"
    private val CPF: String = "76291798754"
    private val ID: String = "1"

    @BeforeEach
    @Throws(Exception::class)
    fun setUp() {
        given(funcionarioRepository?.save(any(Funcionario::class.java))).willReturn(funcionario())
//        given(funcionarioRepository?.find ById(ID)).willReturn(funcionario())
        given(funcionarioRepository?.findByEmail(EMAIL)).willReturn(funcionario())
        given(funcionarioRepository?.findByCpf(CPF)).willReturn(funcionario())
    }

    private fun funcionario(): Funcionario = Funcionario("Nome", EMAIL, SenhaUtils().gerarBcrypt("123456"),
            CPF, PerfilEnum.ROLE_USUARIO, ID)

}