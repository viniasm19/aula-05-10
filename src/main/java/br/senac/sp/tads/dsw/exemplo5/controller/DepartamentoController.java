package br.senac.sp.tads.dsw.exemplo5.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.senac.sp.tads.dsw.exemplo5.dto.DepartamentoRequestDTO;
import br.senac.sp.tads.dsw.exemplo5.dto.DepartamentoResponseDTO;
import br.senac.sp.tads.dsw.exemplo5.service.DepartamentoService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    private final DepartamentoService service;

    public DepartamentoController(DepartamentoService service) {
        this.service = service;
    }

    @GetMapping
    public List<DepartamentoResponseDTO> listarTodos() {
        return service.listarTodos();
    }
    
    @GetMapping("/{id}")
    public DepartamentoResponseDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartamentoResponseDTO criar(@RequestBody @Valid DepartamentoRequestDTO dto) {
        return service.criar(dto);
    }
    
    @PutMapping("/{id}")
    public DepartamentoResponseDTO atualizar(@PathVariable Long id,
                            @RequestBody @Valid DepartamentoRequestDTO dto) {
    return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}
