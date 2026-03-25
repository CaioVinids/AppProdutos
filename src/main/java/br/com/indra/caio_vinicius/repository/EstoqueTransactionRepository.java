package br.com.indra.caio_vinicius.repository;

import br.com.indra.caio_vinicius.model.EstoqueTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstoqueTransactionRepository extends JpaRepository<EstoqueTransaction, String> {

    List<EstoqueTransaction> findByProduto_IdOrderByDataTransacaoDesc(Long produtoId);
}