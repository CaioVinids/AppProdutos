package br.com.indra.caio_vinicius.controller;

import br.com.indra.caio_vinicius.model.EstoqueTransaction;
import br.com.indra.caio_vinicius.model.Produtos;
import br.com.indra.caio_vinicius.repository.EstoqueTransactionRepository;
import br.com.indra.caio_vinicius.service.ProdutosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos")
@RequestMapping("/produtos")
public class ProdutosController {

    private final ProdutosService produtosService;
    private final EstoqueTransactionRepository estoqueTransactionRepository;

    @PostMapping
    @Operation(summary = "Criação de produto", description = "Endpoint para criar um novo produto vinculado a uma categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou Categoria não encontrada")
    })
    public ResponseEntity<Produtos> criarProduto(
            @RequestBody Produtos produto,
            @Parameter(description = "ID da categoria obrigatória") @RequestParam Long categoriaId) {
        return ResponseEntity.status(201).body(produtosService.createdProduto(produto, categoriaId));
    }

    @GetMapping
    @Operation(summary = "Lista produtos com filtros", description = "Retorna produtos ativos, permitindo filtrar por nome do produto ou da categoria")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<Produtos>> getAll(
            @Parameter(description = "Parte do nome do produto") @RequestParam(required = false) String nome,
            @Parameter(description = "Parte do nome da categoria") @RequestParam(required = false) String nomeCategoria) {
        return ResponseEntity.ok(produtosService.buscarPorFiltros(nome, nomeCategoria));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca por ID", description = "Retorna um único produto baseado no ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<Produtos> getById(@PathVariable Long id) {
        try {
            Produtos produto = produtosService.getById(id);
            return ResponseEntity.ok(produto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/atualiza/{id}")
    @Operation(summary = "Atualização completa", description = "Substitui os dados de um produto e permite alterar sua categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "ID ou Categoria não encontrada")
    })
    public ResponseEntity<Produtos> atualizarProduto(
            @PathVariable Long id,
            @RequestBody Produtos produto,
            @Parameter(description = "Novo ID da categoria") @RequestParam Long categoriaId) {
        return ResponseEntity.ok(produtosService.atualiza(id, produto, categoriaId));
    }

    @PatchMapping("/atualiza-preco/{id}")
    @Operation(summary = "Atualiza apenas o preço", description = "Endpoint específico para reajuste de valores com registro em histórico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preço atualizado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<Produtos> atualizarProdutoParcial(@PathVariable Long id, @RequestBody Map<String, Object> campos) {
        return ResponseEntity.ok(produtosService.atualizaPreco(id, campos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclusão lógica (Soft Delete)", description = "Altera o status 'ativo' para 0 sem apagar o registro")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "ID não encontrado")
    })
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        produtosService.deletarProduto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{produtoId}/historico")
    @Operation(summary = "Histórico de movimentação", description = "Retorna todas as entradas e saídas do produto.")
    public ResponseEntity<List<EstoqueTransaction>> buscarHistorico(@PathVariable Long produtoId) {
        return ResponseEntity.ok(estoqueTransactionRepository.findByProduto_IdOrderByDataTransacaoDesc(produtoId));
    }

}