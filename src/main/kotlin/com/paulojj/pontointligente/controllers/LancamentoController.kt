package com.paulojj.pontointligente.controllers

import com.paulojj.pontointligente.documents.Funcionario
import com.paulojj.pontointligente.documents.Lancamento
import com.paulojj.pontointligente.dtos.LancamentoDto
import com.paulojj.pontointligente.enums.TipoEnum
import com.paulojj.pontointligente.response.Response
import com.paulojj.pontointligente.services.FuncionarioService
import com.paulojj.pontointligente.services.LancamentoService
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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

        var lancamento: Lancamento = converterDtoParaLancamento(lancamentoDto, result)
        lancamento = lancamentoService.persistir(lancamento)
        response.data = converterLancamentoDto(lancamento)
        return ResponseEntity.ok(response)

    }

    @GetMapping(value = ["/{id}"])
    fun listaPorId(@PathVariable("id") id: String): ResponseEntity<Response<LancamentoDto>> {
        val response: Response<LancamentoDto> = Response<LancamentoDto>()
        val lancamento: Optional<Lancamento> = lancamentoService.buscarPorId(id)
        if (lancamento.isEmpty) {
            response.errors.add("Lançamento não encontrado para o id $id")
            return ResponseEntity.badRequest().body(response)
        }

        response.data = converterLancamentoDto(lancamento.get())
        return ResponseEntity.ok(response)
    }

    @GetMapping(value = ["/funcionario/{funcionarioId}"])
    fun listarPorFuncionarioId(@PathVariable funcionarioId: String,
                               @RequestParam(value = "pag", defaultValue = "0") pag: Int,
                               @RequestParam(value = "ord", defaultValue = "id") ord: String,
                               @RequestParam(value = "dir", defaultValue = "DESC") dir: String):
            ResponseEntity<Response<Page<LancamentoDto>>> {
        val response: Response<Page<LancamentoDto>> = Response<Page<LancamentoDto>>()
        val pageRequest: PageRequest = PageRequest.of(pag, qtdPorPagina, Sort.Direction.valueOf(dir), ord)
        val lancamentos: Page<Lancamento> = lancamentoService.buscarPorFuncionarioId(funcionarioId, pageRequest)
        val lancamentosDto: Page<LancamentoDto> = lancamentos.map { lancamento -> converterLancamentoDto(lancamento) }
        response.data = lancamentosDto
        return ResponseEntity.ok(response)
    }

    @PutMapping(value = ["/{id}"])
    fun atualizar(@PathVariable id: String, @Valid @RequestBody lancamentoDto: LancamentoDto, result: BindingResult):
            ResponseEntity<Response<LancamentoDto>> {
        val response: Response<LancamentoDto> = Response<LancamentoDto>()
        validarFuncionario(lancamentoDto, result)
        lancamentoDto.id = id
        val lancamento: Lancamento = converterDtoParaLancamento(lancamentoDto, result)

        if (result.hasErrors()) {
            for (erro in result.allErrors) response.errors.add(erro.defaultMessage)
            return ResponseEntity.badRequest().body(response)
        }

        lancamentoService.persistir(lancamento)
        response.data = converterLancamentoDto(lancamento)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping(value = ["/{id}"])
    fun remover(@PathVariable id: String): ResponseEntity<Response<String>> {
        val response: Response<String> = Response<String>()
        val lancamento: Optional<Lancamento> = lancamentoService.buscarPorId(id)

        if (lancamento.isEmpty) {
            response.errors.add("Erro ao remover lançamento. Registro não encontrado para o id $id")
            return ResponseEntity.badRequest().body(response)
        }

        lancamentoService.remover(id)
        return ResponseEntity.ok(Response<String>())
    }

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
