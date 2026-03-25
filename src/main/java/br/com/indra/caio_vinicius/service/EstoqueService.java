package br.com.indra.caio_vinicius.service;

import br.com.indra.caio_vinicius.model.EstoqueTransaction;
import br.com.indra.caio_vinicius.model.Produtos;
import br.com.indra.caio_vinicius.model.TipoTransacao;
import br.com.indra.caio_vinicius.repository.EstoqueTransactionRepository;
import br.com.indra.caio_vinicius.repository.ProdutosRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final ProdutosRepository produtosRepository;
    private final EstoqueTransactionRepository transactionRepository;

    @Transactional
    public void adicionarEstoque(Long produtoId, Integer qtd, String obs) {
        Produtos produto = produtosRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        produto.setEstoque(produto.getEstoque() + qtd);
        produtosRepository.save(produto);

        registrarTransacao(produto, qtd, TipoTransacao.ENTRADA, obs);
    }

    @Transactional
    public void removerEstoque(Long produtoId, Integer qtd, String obs) {
        Produtos produto = produtosRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        /// Impedir estoque insuficiente
        if (produto.getEstoque() < qtd) {
            throw new RuntimeException("Saldo insuficiente! Estoque atual: " + produto.getEstoque());
        }

        produto.setEstoque(produto.getEstoque() - qtd);
        produtosRepository.save(produto);

        registrarTransacao(produto, qtd, TipoTransacao.SAIDA, obs);

        verificarEstoqueBaixo(produto);
    }

    private void registrarTransacao(Produtos p, Integer qtd, TipoTransacao tipo, String obs) {
        EstoqueTransaction ts = EstoqueTransaction.builder()
                .produto(p)
                .tipo(tipo)
                .quantidade(qtd)
                .estoqueNoMomento(p.getEstoque())
                .dataTransacao(LocalDateTime.now())
                .observacao(obs)
                .build();
        transactionRepository.save(ts);
    }

    public List<EstoqueTransaction> obterHistorico(Long produtoId) {
        return transactionRepository.findByProduto_IdOrderByDataTransacaoDesc(produtoId);
    }

    public void devolverEstoque(Long produtoId, Integer qtd, String obs) {
        Produtos produto = produtosRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        produto.setEstoque(produto.getEstoque() + qtd);
        produtosRepository.save(produto);

        registrarTransacao(produto, qtd, TipoTransacao.DEVOLUCAO, obs);
    }

    public void ajustarEstoque(Long produtoId, Integer novaQuantidade, String obs) {
        Produtos produto = produtosRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        /// No ajuste, registra a diferença para o histórico
        int diferenca = novaQuantidade - produto.getEstoque();

        produto.setEstoque(novaQuantidade);
        produtosRepository.save(produto);

        registrarTransacao(produto, diferenca, TipoTransacao.AJUSTE, obs);

        verificarEstoqueBaixo(produto);
    }

    private void verificarEstoqueBaixo(Produtos produto) {
        if (produto.getEstoque() <= 5) {
            System.err.println("ALERTA: O produto " + produto.getNome() + " está com estoque baixo (" + produto.getEstoque() + " unidades)!");
        }

    }

}
