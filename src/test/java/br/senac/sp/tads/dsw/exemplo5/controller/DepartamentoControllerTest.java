package br.senac.sp.tads.dsw.exemplo5.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import br.senac.sp.tads.dsw.exemplo5.model.Departamento;
import br.senac.sp.tads.dsw.exemplo5.repository.DepartamentoRepository;
// Para injeção de dependência via @Autowired:
import tools.jackson.databind.ObjectMapper;
// Para injeção de dependência direta:
// import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional 
public class DepartamentoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DepartamentoRepository repository;

    // Para injeção de dependência via @Autowired:
    @Autowired
    private ObjectMapper objectMapper;
    
    // Para injeção de dependência direta:
    // private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deveCriarDepartamentoComSucesso() throws Exception {
        // 1 - Criaro objeto que queremos enviar
        Departamento departamento = new Departamento();
        departamento.setNome("Tecnologia da Informação");
        departamento.setOrcamento(150000.00);

        // Converter o objeto JAVA em para uma String (texto JSON)
        String jsonRequisicao = objectMapper.writeValueAsString(departamento);

        // 2 - Enviar o POST para a url da API
        mockMvc.perform(post("/api/departamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequisicao))
        // 3 - Verificar se a resposta é o esperado        
                .andExpect(status().isCreated()) // HTTP 201
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Tecnologia da Informação"));
    }

    @Test
    void deveListarTodosOsDepartamentos() throws Exception {
        Departamento departamento = new Departamento();
        departamento.setNome("RH");
        departamento.setOrcamento(50000.00);
        repository.save(departamento); // coloca 1 registro no banco de dados

        mockMvc.perform(get("/api/departamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("RH")); // Posição zero do array
    }

    @Test
    void deveBuscarDepartamentoPorId() throws Exception {
        Departamento departamento = new Departamento();
        departamento.setNome("Marketing");
        departamento.setOrcamento(80000.00);
        Departamento departamentoSalvo = repository.save(departamento);

        mockMvc.perform(get("/api/departamentos/" + departamentoSalvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Marketing"));
    }

    @Test
    void deveAtualizarDepartamento() throws Exception {
        // 1 - Prepara o registro antigo no banco de dados
        Departamento departamentoSalvo = new Departamento();
        departamentoSalvo.setNome("Financeiro");
        departamentoSalvo.setOrcamento(10000.00);
        repository.save(departamentoSalvo); // coloca 1 registro no banco de dados

        // 2 - Cria os novos dados para o PUT
        departamentoSalvo.setOrcamento(50000.00);
        String jsonAtualizado = objectMapper.writeValueAsString(departamentoSalvo);

        // 3 - Executa e verifica se atualizou
        mockMvc.perform(put("/api/departamentos/" + departamentoSalvo.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonAtualizado))       
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(jsonPath("$.orcamento").value(50000.00));
    }

    @Test
    void deveApagarDepartamento() throws Exception {
        Departamento departamento = new Departamento();
        departamento.setNome("Limpeza");
        departamento.setOrcamento(1000.00);
        Departamento departamentoSalvo = repository.save(departamento);

        // Deleta e espera 204 No Content
        mockMvc.perform(delete("/api/departamentos/" + departamentoSalvo.getId()))
                .andExpect(status().isNoContent());

        // Confirma que apagou tentando buscar (espera 404 Not Found)
        mockMvc.perform(get("/api/departamentos/" + departamentoSalvo.getId()))
                .andExpect(status().isNotFound());
    }
}
