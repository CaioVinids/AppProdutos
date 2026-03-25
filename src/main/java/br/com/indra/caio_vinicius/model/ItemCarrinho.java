package br.com.indra.caio_vinicius.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "TB_ITEM_CARRINHO")
public class ItemCarrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produtos produto;

    @Column(nullable = false)
    private Integer quantidade;

    /// Preço no momento da inserção
    @Column(nullable = false)
    private BigDecimal priceSnapshot;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "carrinho_id")
    private Carrinho carrinho;
}
