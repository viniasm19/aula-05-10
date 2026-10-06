package br.senac.sp.tads.dsw.exemplo5.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import br.senac.sp.tads.dsw.exemplo5.model.Departamento;
import br.senac.sp.tads.dsw.exemplo5.model.Funcionario;
import br.senac.sp.tads.dsw.exemplo5.repository.DepartamentoRepository;
import br.senac.sp.tads.dsw.exemplo5.repository.FuncionarioRepository;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional 
public class FuncionarioControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Departamento departamentoPadrao;

    @BeforeEach
    void setUp() {
        Departamento departamento = new Departamento();
        departamento.setNome("Desenvolvimento");
        departamento.setOrcamento(200000.00);
        departamentoPadrao = departamentoRepository.save(departamento);
    }

    private Funcionario salvarFuncionario(String nome) {
    Funcionario funcionario = new Funcionario();
    funcionario.setNome(nome);
    funcionario.setDataContratacao(LocalDate.of(2024, 3, 15));
    funcionario.setTrabalhoRemoto(true);
    funcionario.setDepartamento(departamentoPadrao);
    return funcionarioRepository.save(funcionario);
    }

    @Test
    void deveCriarFuncionarioComSucesso() throws Exception {
         Funcionario funcionario = new Funcionario();
    funcionario.setNome("Maria Souza");
    funcionario.setDataContratacao(LocalDate.of(2024, 3, 15));
    funcionario.setTrabalhoRemoto(true);
    funcionario.setDepartamento(departamentoPadrao);

    String json = objectMapper.writeValueAsString(funcionario);

    mockMvc.perform(post("/api/funcionarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.nome").value("Maria Souza"))
            .andExpect(jsonPath("$.departamento.nome").value("Desenvolvimento"));
}

    @Test 
    void deveRetornarErro400AoCriarFuncionarioComDataFutura() throws Exception {
        Funcionario funcionario = new Funcionario();
    funcionario.setNome("João Futuro");
    funcionario.setDataContratacao(LocalDate.now().plusDays(1)); // amanhã -> viola @PastOrPresent
    funcionario.setTrabalhoRemoto(false);
    funcionario.setDepartamento(departamentoPadrao);

    String json = objectMapper.writeValueAsString(funcionario);

    mockMvc.perform(post("/api/funcionarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isBadRequest()); // HTTP 400
    }

    @Test 
    void deveListarTodosOsFuncionarios() throws Exception {
         salvarFuncionario("Ana Lima");
         mockMvc.perform(get("/api/funcionarios"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nome").value("Ana Lima"));
    }

    @Test 
    void deveBuscarFuncionarioPorId() throws Exception {
        Funcionario salvo = salvarFuncionario("Carlos Dias");

    mockMvc.perform(get("/api/funcionarios/" + salvo.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("Carlos Dias"))
            .andExpect(jsonPath("$.departamento.id").value(departamentoPadrao.getId()));

    }

    @Test 
    void deveAtualizarFuncionario() throws Exception {
         Funcionario salvo = salvarFuncionario("Paula Reis");

    salvo.setTrabalhoRemoto(false); // novo valor que vai no PUT
    String json = objectMapper.writeValueAsString(salvo);

    mockMvc.perform(put("/api/funcionarios/" + salvo.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.trabalhoRemoto").value(false));
    }

    @Test 
    void deveApagarFuncionario() throws Exception {
         Funcionario salvo = salvarFuncionario("Rafael Costa");

    mockMvc.perform(delete("/api/funcionarios/" + salvo.getId()))
            .andExpect(status().isNoContent()); // HTTP 204

    // confirma que apagou: agora o GET tem que dar 404
    mockMvc.perform(get("/api/funcionarios/" + salvo.getId()))
            .andExpect(status().isNotFound());
    }

}