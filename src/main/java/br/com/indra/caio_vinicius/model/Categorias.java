package br.com.indra.caio_vinicius.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_CATEGORIAS")
/*** Comando para não deletar a categoria, apenas mudar o status para false = 0 (Inativo) e
 * em toda inclusão será adicionado o status true = 1 (Ativo)
 */
@SoftDelete(columnName = "ativo", strategy = SoftDeleteType.ACTIVE)
public class Categorias {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cat_seq")
    @SequenceGenerator(name = "cat_seq", sequenceName = "SEQ_CATEGORIAS", allocationSize = 1)
    private Long id;

    @NotBlank(message = "O nome da categoria é obrigatório")
    @Column(nullable = false)
    private String nome;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria_pai")
    private Categorias categoriaPai;

    @JsonManagedReference
    @OneToMany(mappedBy = "categoriaPai", fetch = FetchType.LAZY)
    private List<Categorias> subcategorias;

    @JsonManagedReference
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private List<Produtos> produtos;
}
