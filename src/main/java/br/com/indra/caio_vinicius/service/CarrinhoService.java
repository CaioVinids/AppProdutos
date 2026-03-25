package br.com.indra.caio_vinicius.service;

import br.com.indra.caio_vinicius.model.Carrinho;
import br.com.indra.caio_vinicius.model.ItemCarrinho;
import br.com.indra.caio_vinicius.model.Produtos;
import br.com.indra.caio_vinicius.model.StatusCarrinho;
import br.com.indra.caio_vinicius.repository.CarrinhoRepository;
import br.com.indra.caio_vinicius.repository.EstoqueTransactionRepository;
import br.com.indra.caio_vinicius.repository.ItemCarrinhoRepository;
import br.com.indra.caio_vinicius.repository.ProdutosRepository;
import br.com.indra.caio_vinicius.service.dto.CarrinhoDTOs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ProdutosRepository produtosRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final EstoqueTransactionRepository estoqueTransactionRepository;

    public Carrinho criarCarrinho(Long usuarioId) {
        return carrinhoRepository.findByUsuarioIdAndStatus(usuarioId, StatusCarrinho.ATIVO)
                .orElseGet(() -> {
                    Carrinho novo = new Carrinho();
                    novo.setUsuarioId(usuarioId);
                    novo.setStatus(StatusCarrinho.ATIVO);
                    return carrinhoRepository.save(novo);
                });
    }

    public Carrinho buscarCarrinho(Long usuarioId) {
        return carrinhoRepository.findByUsuarioIdAndStatus(usuarioId, StatusCarrinho.ATIVO)
                .orElseThrow(() -> new RuntimeException("Nenhum carrinho ativo encontrado para o usuário: " + usuarioId));
    }

    @Transactional
    public Carrinho adicionarItem(CarrinhoDTOs.AdicionarItem dto) {

        Carrinho carrinho = criarCarrinho(dto.getUsuarioId());

        validarCarrinhoAberto(carrinho);

        /// Busca o produto e valida se existe
        Produtos produto = produtosRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        /// Valida se há estoque antes de colocar no carrinho
        if (produto.getEstoque() < dto.getQuantidade()) {
            throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        /// Cria o Item de Carrinho com o PRICE SNAPSHOT
        ItemCarrinho novoItem = new ItemCarrinho();
        novoItem.setProduto(produto);
        novoItem.setQuantidade(dto.getQuantidade());
        novoItem.setPriceSnapshot(produto.getPreco());
        novoItem.setCarrinho(carrinho);

        carrinho.getItens().add(novoItem);
        carrinho.calcularTotal();

        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public Carrinho atualizarQuantidade(Long itemId, Integer novaQuantidade) {
        ItemCarrinho item = itemCarrinhoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        validarCarrinhoAberto(item.getCarrinho());

        if (novaQuantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        if (item.getProduto().getEstoque() < novaQuantidade) {
            throw new IllegalArgumentException("Estoque insuficiente para essa alteração.");
        }

        item.setQuantidade(novaQuantidade);
        Carrinho carrinho = item.getCarrinho();
        carrinho.calcularTotal();

        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public Carrinho deletarItem(Long itemId) {
        ItemCarrinho item = itemCarrinhoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        validarCarrinhoAberto(item.getCarrinho());

        Carrinho carrinho = item.getCarrinho();

        /// Remove da lista e deleta do banco
        carrinho.getItens().remove(item);
        itemCarrinhoRepository.delete(item);

        carrinho.calcularTotal();

        return carrinhoRepository.save(carrinho);
    }

    private void validarCarrinhoAberto(Carrinho carrinho) {
        if (carrinho.getStatus() == StatusCarrinho.FINALIZADO) {
            throw new RuntimeException("Não é possível alterar um carrinho que já foi FINALIZADO.");
        }
    }

    @Transactional
    public Carrinho finalizarCompra(Long usuarioId) {
        Carrinho carrinho = buscarCarrinho(usuarioId);

        if (carrinho.getItens().isEmpty()) {
            throw new IllegalStateException("O carrinho está vazio.");
        }

        for (ItemCarrinho item : carrinho.getItens()) {
            Produtos produto = item.getProduto();

            if (produto.getEstoque() < item.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para " + produto.getNome());
            }

            produto.setEstoque(produto.getEstoque() - item.getQuantidade());
            produtosRepository.save(produto);

            br.com.indra.caio_vinicius.model.EstoqueTransaction transacao =
                    br.com.indra.caio_vinicius.model.EstoqueTransaction.builder()
                            .produto(produto)
                            .tipo(br.com.indra.caio_vinicius.model.TipoTransacao.SAIDA)
                            .quantidade(item.getQuantidade())
                            .estoqueNoMomento(produto.getEstoque())
                            .dataTransacao(java.time.LocalDateTime.now())
                            .observacao("Venda aprovada - Carrinho ID: " + carrinho.getId())
                            .build();

            estoqueTransactionRepository.save(transacao);
        }

        carrinho.setStatus(StatusCarrinho.FINALIZADO);
        carrinho.setDataFinalizacao(java.time.LocalDateTime.now());
        return carrinhoRepository.save(carrinho);
    }

    public List<Carrinho> listarHistoricoCompras(Long usuarioId) {
        return carrinhoRepository.findAllByUsuarioIdAndStatusOrderByDataFinalizacaoDesc(usuarioId, StatusCarrinho.FINALIZADO);
    }

}