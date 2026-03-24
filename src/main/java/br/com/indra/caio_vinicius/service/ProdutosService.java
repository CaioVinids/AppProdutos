package br.com.indra.caio_vinicius.service;

import br.com.indra.caio_vinicius.model.HistoricoPreco;
import br.com.indra.caio_vinicius.model.Produtos;
import br.com.indra.caio_vinicius.repository.HistoricoPrecoRepository;
import br.com.indra.caio_vinicius.repository.ProdutosRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProdutosService {

    private final ProdutosRepository produtosRepository;
    private final HistoricoPrecoRepository historicoPrecoRepository;

    public List<Produtos> getAll() {

        return produtosRepository.findAll();
    }

    public Produtos createdProduto(Produtos produto) {
        return produtosRepository.save(produto);
    }

    public Produtos atualiza(Produtos produtoAtualizado) {
        // Busca o que já existe hoje no banco para não perder a data de criação
        Produtos produtoExistente = getById(produtoAtualizado.getId());

        // Preserva a data de criação original
        produtoAtualizado.setDataCriacao(produtoExistente.getDataCriacao());

        return produtosRepository.save(produtoAtualizado);
    }

    public void deletarProduto(Long id) {

        produtosRepository.deleteById(id);
    }

    public Produtos getById(Long id) {
        return produtosRepository.findById(id).orElseThrow(() -> new RuntimeException("Produto ID " + id + " não encontrado"));
    }

    @Transactional /// Garantir que se um save falhar, nada mude no banco
    public Produtos atualizaPreco(Long id, Map<String, Object> campos) {

        final var produto = getById(id);

        // Verifica se o campo "preco" existe no JSON enviado
        if (campos.containsKey("preco")) {
            BigDecimal precoAntigo = produto.getPreco();

            // Converte o valor do Map para String e depois para BigDecimal
            BigDecimal novoPreco = new BigDecimal(campos.get("preco").toString())
                    .setScale(2, java.math.RoundingMode.HALF_UP);

            produto.setPreco(novoPreco);

            final var historico = new HistoricoPreco();
            historico.setProdutos(produto);
            historico.setPrecoAntigo(precoAntigo);
            historico.setPrecoNovo(novoPreco);

            historicoPrecoRepository.save(historico);
        }
        return produtosRepository.saveAndFlush(produto);
    }
}
