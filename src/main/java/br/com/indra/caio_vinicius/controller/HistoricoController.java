package br.com.indra.caio_vinicius.controller;

import br.com.indra.caio_vinicius.service.HistoricoService;
import br.com.indra.caio_vinicius.service.dto.HistoricoProdutoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Historico", description = "Endpoints para consulta de preços")
@RequestMapping("/historico")
public class HistoricoController {

    private final HistoricoService historicoService;

    @GetMapping
    @Operation(summary = "Listar todo o histórico",
            description = "Retorna todas as alterações de preços registradas no sistema para todos os produtos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista geral de histórico retornada com sucesso")
    })

    public ResponseEntity<List<HistoricoProdutoDTO>> getAll() {
        List<HistoricoProdutoDTO> historicoCompleto = historicoService.getAllHistorico();
        return ResponseEntity.ok(historicoCompleto);
    }

    @GetMapping("/produto/{produtoId}")
    @Operation(summary = "Consultar histórico por produto",
            description = "Retorna todas as alterações de preço de um produto específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não possui histórico ou não existe")
    })

    public ResponseEntity<List<HistoricoProdutoDTO>> getByProduto(@PathVariable Long produtoId) {
        List<HistoricoProdutoDTO> historico = historicoService.getHistoricoByProdutoId(produtoId);
        return ResponseEntity.ok(historico);
    }
}