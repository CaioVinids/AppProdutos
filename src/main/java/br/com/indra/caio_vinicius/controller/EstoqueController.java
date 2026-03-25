package br.com.indra.caio_vinicius.controller;

import br.com.indra.caio_vinicius.model.EstoqueTransaction;
import br.com.indra.caio_vinicius.service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Estoque", description = "Endpoints para gerenciamento de estoque")
@RequestMapping("/estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    @PostMapping("/{productId}/adicionar")
    @Operation(summary = "Adicionar estoque", description = "Incrementa a quantidade de um produto e gera log de ENTRADA")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoque atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<String> adicionarEstoque(@PathVariable Long productId, @RequestParam Integer quantitade) {
        estoqueService.adicionarEstoque(productId, quantitade, "Entrada via API");
        return ResponseEntity.ok("Estoque atualizado com sucesso!");
    }

    @PostMapping("/{productId}/remover")
    @Operation(summary = "Remover estoque", description = "Subtrai a quantidade do produto. Lança erro se o saldo for insuficiente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saída registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Saldo insuficiente para a operação"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<String> removerEstoque(@PathVariable Long productId, @RequestParam Integer quantitade) {
        estoqueService.removerEstoque(productId, quantitade, "Saída via API");
        return ResponseEntity.ok("Saída registrada com sucesso!");
    }

    @GetMapping("/{productId}/historico")
    @Operation(summary = "Lista o historico do estoque" ,description = "Retorna lista de entradas, saídas e ajustes de um determinado produto")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<EstoqueTransaction>> listarHistoricoEstoque(@PathVariable Long productId) {
        List<EstoqueTransaction> historico = estoqueService.obterHistorico(productId);
        return ResponseEntity.ok(historico);
    }

    @PostMapping("/{productId}/devolver")
    @Operation(summary = "Devolução de produto", description = "Registra o retorno de itens ao estoque físico (ex: devolução de cliente)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Devolução processada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    public ResponseEntity<String> devolverEstoque(@PathVariable Long productId, @RequestParam Integer quantitade) {
        estoqueService.devolverEstoque(productId, quantitade, "Devolução de cliente");
        return ResponseEntity.ok("Devolução registrada com sucesso!");
    }

    @PostMapping("/{productId}/ajustar")
    @Operation(summary = "Ajuste de estoque", description = "Sobrescreve o saldo atual pelo valor informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ajuste realizado com sucesso")
    })
    public ResponseEntity<String> ajustarEstoque(@PathVariable Long productId, @RequestParam Integer quantitade) {
        estoqueService.ajustarEstoque(productId, quantitade, "Ajuste de estoque físico");
        return ResponseEntity.ok("Estoque ajustado com sucesso!");
    }

}
