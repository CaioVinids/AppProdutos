package br.com.indra.caio_vinicius.repository;

import br.com.indra.caio_vinicius.model.Categorias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriasRepository extends JpaRepository<Categorias, Long> {

    List<Categorias> findByCategoriaPaiIsNullOrderByIdAsc();

    List<Categorias> findByNomeContainingIgnoreCase(String nome);

    /// Busca categorias raiz (sem pai) por nome
    Optional<Categorias> findByNomeAndCategoriaPaiIsNull(String nome);

    /// Busca subcategorias de um pai específico por nome
    Optional<Categorias> findByNomeAndCategoriaPaiId(String nome, Long paiId);
}
