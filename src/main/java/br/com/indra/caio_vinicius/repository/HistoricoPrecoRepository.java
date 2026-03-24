package br.com.indra.caio_vinicius.repository;

import br.com.indra.caio_vinicius.model.HistoricoPreco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface HistoricoPrecoRepository extends JpaRepository<HistoricoPreco, UUID> {

    /// Ordenação por data para o DTO ficar mais organizado
    List<HistoricoPreco> findByProdutosIdOrderByDataAlteracaoDesc(Long produtoId);
}
