package com.paulojj.pontointligente.controllers

import com.paulojj.pontointligente.documents.Funcionario
import com.paulojj.pontointligente.documents.Lancamento
import com.paulojj.pontointligente.dtos.LancamentoDto
import com.paulojj.pontointligente.enums.TipoEnum
import com.paulojj.pontointligente.response.Response
import com.paulojj.pontointligente.services.FuncionarioService
import com.paulojj.pontointligente.services.LancamentoService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/lancamentos")
class LancamentoController(val lancamentoService: LancamentoService,
                           val funcionarioService: FuncionarioService) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    @Value("\${paginacao.qtd_por_pagina}")
    val qtdPorPagina: Int = 15

    @PostMapping
    fun adicionar(@Valid @RequestBody lancamentoDto: LancamentoDto,
                  result: BindingResult): ResponseEntity<Response<LancamentoDto>> {
        val response: Response<LancamentoDto> = Response<LancamentoDto>()
        validarFuncionario(lancamentoDto, result)

        if (result.hasErrors()) {
            for (erro in result.allErrors) response.errors.add(erro.defaultMessage.toString())
            return ResponseEntity.badRequest().body(response)
        }

        val lancamento: Lancamento = converterDtoParaLancamento(lancamentoDto, result)
        lancamentoService.persistir(lancamento)
        response.data = converterLancamentoDto(lancamento)
        return ResponseEntity.ok(response)

    }

    @GetMapping(value = ["/{id}"])
    fun listaPorId(@PathVariable("id") id: String): ResponseEntity<Response<LancamentoDto>> {
        val response: Response<LancamentoDto> = Response<LancamentoDto>()
        val lancamento: Optional<Lancamento> = lancamentoService.budcarPorId(id)
        if (lancamento.isEmpty) {
            response.errors.add("Lançamento não encontrado para o id $id")
            return ResponseEntity.badRequest().body(response)
        }

        response.data = converterLancamentoDto(lancamento.get())
        return ResponseEntity.ok(response)
    }

    @GetMapping(value = ["/funcionario/{funcionarioId}"])
    fun lis

    private fun converterLancamentoDto(lancamento: Lancamento): LancamentoDto = LancamentoDto(
            dateFormat.format(lancamento.data),
            lancamento.tipo.toString(), lancamento.descricao, lancamento.localizacao, lancamento.funcionarioId,
            lancamento.id)

    private fun converterDtoParaLancamento(lancamentoDto: LancamentoDto, result: BindingResult): Lancamento {
        return Lancamento(dateFormat.parse(lancamentoDto.data), TipoEnum.valueOf(lancamentoDto.tipo!!),
                lancamentoDto.funcionarioId!!, lancamentoDto.descricao, lancamentoDto.localizacao, lancamentoDto.id)
    }

    private fun validarFuncionario(lancamentoDto: LancamentoDto, result: BindingResult) {
        if (lancamentoDto.funcionarioId == null) {
            result.addError(ObjectError("funcionario", "Funcionário não informado."))
            return
        }

        val funcionario: Optional<Funcionario> = funcionarioService.buscaPorId(lancamentoDto.funcionarioId)
        if (funcionario.isEmpty)
            result.addError(ObjectError("funcionario", "Funcionário não encontrado. ID inexistente."))
    }
}
