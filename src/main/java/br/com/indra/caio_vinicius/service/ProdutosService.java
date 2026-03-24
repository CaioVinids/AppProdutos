package br.com.indra.caio_vinicius.service;

import br.com.indra.caio_vinicius.model.Categorias;
import br.com.indra.caio_vinicius.model.HistoricoPreco;
import br.com.indra.caio_vinicius.model.Produtos;
import br.com.indra.caio_vinicius.repository.CategoriasRepository;
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
    private final CategoriasRepository categoriaRepository;

    public List<Produtos> getAll() {
        return produtosRepository.findAll();
    }

    @Transactional
    public Produtos createdProduto(Produtos produto, Long categoriaId) {
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do produto é obrigatório.");
        }

        validarPreco(produto.getPreco());

        /// Regra: produto deve pertencer a uma categoria
        Categorias categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoria ID " + categoriaId + " não encontrada"));

        produto.setCategoria(categoria);
        return produtosRepository.save(produto);
    }

    @Transactional
    public Produtos atualiza(Long id, Produtos produtoAtualizado, Long categoriaId) {
        /// Busca o que já existe hoje no banco para não perder a data de criação
        Produtos produtoExistente = getById(id);

        /// Validações de negócio
        if (produtoAtualizado.getNome() == null || produtoAtualizado.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do produto não pode ser vazio.");
        }
        validarPreco(produtoAtualizado.getPreco());

        /// Atualiza a categoria se mudar ou mantém a obrigatória
        Categorias categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoria ID " + categoriaId + " não encontrada"));

        /// Lógica do histórico
        if (produtoExistente.getPreco().compareTo(produtoAtualizado.getPreco()) != 0) {
            final var historico = new HistoricoPreco();
            historico.setProdutos(produtoExistente);
            historico.setPrecoAntigo(produtoExistente.getPreco());
            historico.setPrecoNovo(produtoAtualizado.getPreco());
            historico.setDataAlteracao(LocalDateTime.now());

            historicoPrecoRepository.save(historico);
        }

        /// Manter a data de criação original
        produtoAtualizado.setId(id);
        produtoAtualizado.setDataCriacao(produtoExistente.getDataCriacao());
        produtoAtualizado.setCategoria(categoria);

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

        /// Verifica se o campo "preco" existe no JSON enviado
        if (campos.containsKey("preco")) {
            // Converte o valor do Map para String e depois para BigDecimal
            BigDecimal novoPreco = new BigDecimal(campos.get("preco").toString())
                    .setScale(2, java.math.RoundingMode.HALF_UP);

            validarPreco(novoPreco);

            BigDecimal precoAntigo = produto.getPreco();
            produto.setPreco(novoPreco);

            final var historico = new HistoricoPreco();
            historico.setProdutos(produto);
            historico.setPrecoAntigo(precoAntigo);
            historico.setPrecoNovo(novoPreco);

            historicoPrecoRepository.save(historico);
        }
        return produtosRepository.saveAndFlush(produto);
    }
    /// Evitar repetição de código
    private void validarPreco(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço deve ser maior que zero.");
        }
    }

    public List<Produtos> buscarPorFiltros(String nome, String nomeCategoria) {
        // Se ambos estiverem vazios, retorna todos
        if ((nome == null || nome.isBlank()) && (nomeCategoria == null || nomeCategoria.isBlank())) {
            return produtosRepository.findAll();
        }

        // Se só tem o nome da categoria
        if (nome == null || nome.isBlank()) {
            return produtosRepository.findByCategoriaNomeContainingIgnoreCase(nomeCategoria);
        }

        // Se só tem o nome do produto
        if (nomeCategoria == null || nomeCategoria.isBlank()) {
            return produtosRepository.findByNomeContainingIgnoreCase(nome);
        }

        // Se tem os dois filtros
        return produtosRepository.findByNomeContainingIgnoreCaseAndCategoriaNomeContainingIgnoreCase(nome, nomeCategoria);
    }
}
