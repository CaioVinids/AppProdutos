package br.com.indra.caio_vinicius.controller;

import br.com.indra.caio_vinicius.model.Categorias;
import br.com.indra.caio_vinicius.service.CategoriasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "Categorias", description = "Endpoints para gerenciamento de categorias")
@RequestMapping("/categorias")
public class CategoriasController {

    private final CategoriasService categoriasService;

    @GetMapping
    @Operation(summary = "Lista todas as categorias" ,description = "Retorna lista completa de categorias cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<Categorias>> listarCategoria(@RequestParam(required = false) String nome) {
        return ResponseEntity.ok(categoriasService.getAll(nome));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca categoria por ID" ,description = "Retorna os detalhes de uma categoria específica")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "ID não encontrado no banco")
    })
    public ResponseEntity<Categorias> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriasService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Criar nova categoria" ,description = "Cria uma categoria raiz ou vincula a uma categoria pai")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Nome duplicado no mesmo nível ou dados inválidos")
    })
    public ResponseEntity<Categorias> criarCategoria(@RequestBody Categorias categoria,
                                                      @Parameter(description = "ID da categoria pai (opcional para categorias raiz)")
                                                      @RequestParam(required = false) Long idPai) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriasService.criarCategoria(categoria, idPai));
    }

    @PutMapping("/atualiza/{id}")
    @Operation(summary = "Atualizar categoria" ,description = "Permite alterar o nome ou a posição da categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "ID da categoria não encontrado")
    })
    public ResponseEntity<Categorias> atualizarCategoria(@PathVariable Long id,
                                                          @RequestBody Categorias categoria,
                                                          @Parameter(description = "Novo ID da categoria pai")
                                                          @RequestParam(required = false) Long idPai) {
        return ResponseEntity.ok(categoriasService.atualizarCategoria(id, categoria, idPai));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover categoria" ,description = "Realiza a exclusão lógica (Soft Delete) da categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "ID não encontrado")
    })
    public ResponseEntity<Void> deletarCategoria(@PathVariable Long id) {
        categoriasService.deletarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}