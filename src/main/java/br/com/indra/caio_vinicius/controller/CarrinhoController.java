package br.com.indra.caio_vinicius.controller;

import br.com.indra.caio_vinicius.model.Carrinho;
import br.com.indra.caio_vinicius.service.CarrinhoService;
import br.com.indra.caio_vinicius.service.dto.CarrinhoDTOs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Carrinho", description = "Endpoints para gerenciamento do carrinho de compras")
@RequestMapping("/carrinho")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    @PostMapping("/criar")
    @Operation(summary = "Criar um carrinho", description = "Cria um carrinho com status ATIVO para o usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Carrinho criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    public ResponseEntity<Carrinho> criarCarrinho(@RequestBody CarrinhoDTOs.Criar dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(carrinhoService.criarCarrinho(dto.getUsuarioId()));
    }

    @GetMapping("/visualizar/{usuarioId}")
    @Operation(summary = "Visualizar carrinho ativo", description = "Retorna o carrinho que estiver com status ATIVO para o ID do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrinho encontrado"),
            @ApiResponse(responseCode = "404", description = "Nenhum carrinho ativo encontrado para este usuário")
    })
    public ResponseEntity<Carrinho> buscarCarrinho(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carrinhoService.buscarCarrinho(usuarioId));
    }

    @PostMapping("/item")
    @Operation(summary = "Adicionar item ao carrinho", description = "Insere um novo item no carrinho")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item adicionado e total recalculado"),
            @ApiResponse(responseCode = "400", description = "Erro na validação do item ou estoque")
    })
    public ResponseEntity<Carrinho> adicionarItem(@RequestBody CarrinhoDTOs.AdicionarItem dto) {
        return ResponseEntity.ok(carrinhoService.adicionarItem(dto));
    }

    @PutMapping("/item/atualizar")
    @Operation(summary = "Atualiza a quantidade de um item", description = "Atualiza a quantidade de um item específico no carrinho")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantidade atualizada"),
            @ApiResponse(responseCode = "404", description = "ID do item não encontrado")
    })
    public ResponseEntity<Carrinho> atualizarQuantidade(@RequestBody CarrinhoDTOs.AtualizarQuantidade dto) {
        return ResponseEntity.ok(carrinhoService.atualizarQuantidade(dto.getItemId(), dto.getQuantidade()));
    }

    @DeleteMapping("/item/deletar")
    @Operation(summary = "Deletar um item do carrinho", description = "Exclui permanentemente um item do carrinho ativo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado para remoção")
    })
    public ResponseEntity<Carrinho> deletarItem(@RequestBody CarrinhoDTOs.RemoverItem dto) {
        return ResponseEntity.ok(carrinhoService.deletarItem(dto.getItemId()));
    }

    @PostMapping("/finalizar")
    @Operation(summary = "Finalizar compra", description = "Muda o status do carrinho para FINALIZADO e registra a data atual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compra finalizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Carrinho vazio ou já finalizado")
    })
    public ResponseEntity<Carrinho> finalizarCompra(@RequestBody CarrinhoDTOs.Criar dto) {
        return ResponseEntity.ok(carrinhoService.finalizarCompra(dto.getUsuarioId()));
    }

    @GetMapping("/historico/{usuarioId}")
    @Operation(summary = "Listar histórico de compras", description = "Retorna todos os carrinhos FINALIZADOS do usuário, do mais recente para o mais antigo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de histórico retornada")
    })
    public ResponseEntity<List<Carrinho>> listarHistorico(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carrinhoService.listarHistoricoCompras(usuarioId));
    }
}

