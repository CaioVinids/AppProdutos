package br.com.indra.caio_vinicius.repository;

import br.com.indra.caio_vinicius.model.Carrinho;
import br.com.indra.caio_vinicius.model.StatusCarrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {

    /// Busca se o usuário já possui um carrinho com status 'ATIVO' antes de criar um novo.
    Optional<Carrinho> findByUsuarioIdAndStatus(Long usuarioId, StatusCarrinho status);

    List<Carrinho> findAllByUsuarioIdAndStatusOrderByDataFinalizacaoDesc(Long usuarioId, StatusCarrinho status);
}