package br.com.indra.caio_vinicius.service;

import br.com.indra.caio_vinicius.model.Categorias;
import br.com.indra.caio_vinicius.repository.CategoriasRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoriasService {

    private final CategoriasRepository categoriasRepository;

    public List<Categorias> getAll(String nome) {
        if (nome != null && !nome.isBlank()) {
            return categoriasRepository.findByNomeContainingIgnoreCase(nome);
        }
        return categoriasRepository.findByCategoriaPaiIsNullOrderByIdAsc();
    }

    public Categorias getById(Long id) {
        return categoriasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria ID " + id + " não encontrada"));
    }

    @Transactional
    public Categorias criarCategoria(Categorias categoria, Long idPai) {

        if (categoria.getNome() == null || categoria.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome da categoria é obrigatório.");
        }

        /// Configurar a hierarquia (Pai e Filho)
        if (idPai != null) {
            Categorias pai = getById(idPai);
            categoria.setCategoriaPai(pai);
        }

        /// Nome único no mesmo nível
        validarNomeUnicoNoNivel(categoria.getNome(), idPai);

        return categoriasRepository.save(categoria);
    }

    @Transactional
    public Categorias atualizarCategoria(Long id, Categorias dadosAtualizados, Long idPai) {
        Categorias categoriaExistente = getById(id);

        if (dadosAtualizados.getNome() == null || dadosAtualizados.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome da categoria não pode ser vazio.");
        }

        validarNomeUnicoNoNivelParaAtualizacao(dadosAtualizados.getNome(), idPai, id);

        categoriaExistente.setNome(dadosAtualizados.getNome());

        if (idPai != null) {
            Categorias novoPai = getById(idPai);
            categoriaExistente.setCategoriaPai(novoPai);
        } else {
            categoriaExistente.setCategoriaPai(null);
        }

        return categoriasRepository.save(categoriaExistente);
    }

    public void deletarCategoria(Long id) {
        Categorias categoria = getById(id);
        categoriasRepository.delete(categoria);
    }

    private void validarNomeUnicoNoNivel(String nome, Long idPai) {
        Optional<Categorias> duplicada;
        if (idPai == null) {
            duplicada = categoriasRepository.findByNomeAndCategoriaPaiIsNull(nome);
        } else {
            duplicada = categoriasRepository.findByNomeAndCategoriaPaiId(nome, idPai);
        }

        if (duplicada.isPresent()) {
            throw new IllegalArgumentException("Já existe uma categoria com o nome '" + nome + "' neste nível.");
        }
    }

    private void validarNomeUnicoNoNivelParaAtualizacao(String nome, Long idPai, Long idAtual) {
        Optional<Categorias> duplicada;
        if (idPai == null) {
            duplicada = categoriasRepository.findByNomeAndCategoriaPaiIsNull(nome);
        } else {
            duplicada = categoriasRepository.findByNomeAndCategoriaPaiId(nome, idPai);
        }

        if (duplicada.isPresent() && !duplicada.get().getId().equals(idAtual)) {
            throw new IllegalArgumentException("O nome '" + nome + "' já está em uso por outra categoria neste nível.");
        }
    }
}
