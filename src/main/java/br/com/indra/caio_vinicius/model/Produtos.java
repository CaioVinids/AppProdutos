package br.com.indra.caio_vinicius.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_PRODUTOS")
/*** Comando para não deletar o produto, apenas mudar o status para false = 0 (Inativo) e
 * em toda inclusão será adicionado o status true = 1 (Ativo)
 */
@SoftDelete(columnName = "ativo", strategy = SoftDeleteType.ACTIVE)
public class Produtos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    private String descricao;

    @NotNull(message = "O preço é obrigatório")
    @Column(nullable = false, precision = 10, scale = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal preco;

    @Column(name="codigo_barras", unique = true)
    private String codigoBarras;

    @NotNull
    @Column(nullable = false)
    private Integer estoque;

    @Column(name = "data_criacao", updatable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    public BigDecimal getPreco() {
        if (this.preco == null) {
            return null;
        }
        /// Força 2 casas decimais e arredondamento padrão (Ex: 1499.9 vira 1499.90)
        return this.preco.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categorias categoria;
}