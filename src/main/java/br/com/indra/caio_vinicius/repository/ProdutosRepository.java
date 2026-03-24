package br.com.indra.caio_vinicius.repository;

import br.com.indra.caio_vinicius.model.Produtos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProdutosRepository extends JpaRepository<Produtos, Long> {

    /// Busca produtos por parte do nome
    List<Produtos> findByNomeContainingIgnoreCase(String nome);

    /// Busca produtos por parte do nome da Categoria
    List<Produtos> findByCategoriaNomeContainingIgnoreCase(String nomeCategorias);

    /// Nome do produto e nome da categoria
    List<Produtos> findByNomeContainingIgnoreCaseAndCategoriaNomeContainingIgnoreCase(String nome, String nomeCategorias);
}