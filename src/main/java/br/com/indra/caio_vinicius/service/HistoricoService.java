package br.com.indra.caio_vinicius.service;

import br.com.indra.caio_vinicius.model.HistoricoPreco;
import br.com.indra.caio_vinicius.repository.HistoricoPrecoRepository;
//import br.com.indra.caio_vinicius.service.dto.HistoricoProdutoDTO;
import br.com.indra.caio_vinicius.service.dto.HistoricoProdutoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoricoService {

    private final HistoricoPrecoRepository historicoPrecoRepository;

    /// Busca a lista completa do histórico de alterações de preços do banco de dados.

    public List<HistoricoProdutoDTO> getAllHistorico() {
        List<HistoricoPreco> todosHistoricos = historicoPrecoRepository.findAll();
        return todosHistoricos.stream()
                .map(h -> {
                    String nomeProduto = "Produto Indisponível";
                    try {
                        if (h.getProdutos() != null) {
                            nomeProduto = h.getProdutos().getNome();
                        }
                    } catch (Exception e) {
                        nomeProduto = "ID: " + h.getProdutos().getId() + " (Removido)";
                    }
                    return HistoricoProdutoDTO.builder()
                            .id(h.getId())
                            .produto(nomeProduto)
                            .precoAntigo(h.getPrecoAntigo())
                            .precoNovo(h.getPrecoNovo())
                            .dataRegistro(h.getDataAlteracao())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /// Retorna o histórico filtrado por um produto específico

    public List<HistoricoProdutoDTO> getHistoricoByProdutoId(Long produtoId) {
        List<HistoricoPreco> historicoPrecos = historicoPrecoRepository.findByProdutosIdOrderByDataAlteracaoDesc(produtoId);
        return historicoPrecos.stream()
                .map(h -> HistoricoProdutoDTO.builder()
                        .id(h.getId())
                        .produto(h.getProdutos().getNome())
                        .precoAntigo(h.getPrecoAntigo())
                        .precoNovo(h.getPrecoNovo())
                        .dataRegistro(h.getDataAlteracao())
                        .build())
                .collect(Collectors.toList());
    }
}