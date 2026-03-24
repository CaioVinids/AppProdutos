package br.com.indra.caio_vinicius.controller;

import br.com.indra.caio_vinicius.model.Produtos;
import br.com.indra.caio_vinicius.repository.ProdutosRepository;
import br.com.indra.caio_vinicius.service.ProdutosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos")
@RequestMapping("/produtos")
public class ProdutosController {

    private final ProdutosService produtosService;

    @PostMapping
    @Operation(description = "Endpoint para criar um novo produto",
            summary = "Criação de produto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<Produtos> criarProduto(@RequestBody Produtos produto){
        return ResponseEntity.status(201).body(produtosService.createdProduto(produto));
    }

    @GetMapping
    @Operation(summary = "Lista produtos ativos", description = "Retorna todos os produtos que não sofreram Delete")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<Produtos>> getAll(){
        return ResponseEntity.ok(produtosService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca por ID", description = "Retorna um único produto baseado no ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado no banco")
    })
    public ResponseEntity<Produtos> getById(@PathVariable Long id){
        // Se o service retornar null, tratar para devolver 404
        try {
            Produtos produto = produtosService.getById(id);
            return (produto != null) ? ResponseEntity.ok(produto) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/atualiza/{id}")
    @Operation(summary = "Atualização completa", description = "Substitui todos os dados de um produto existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "ID informado não existe")
    })
    public ResponseEntity<Produtos> atualizarProduto(@PathVariable Long id, @RequestBody Produtos produto){
        produto.setId(id);
        return ResponseEntity.ok(produtosService.atualiza(produto));
    }

    @PatchMapping("/atualiza-preco/{id}")
    @Operation(summary = "Atualiza apenas o preço", description = "Endpoint específico para reajuste de valores")
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
}