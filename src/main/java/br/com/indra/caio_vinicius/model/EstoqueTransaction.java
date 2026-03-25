package br.com.indra.caio_vinicius.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_ESTOQUE_TRANSACAO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstoqueTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_produto", nullable = false)
    private Produtos produto;

    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;

    private Integer quantidade;
    private Integer estoqueNoMomento;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataTransacao;
    private String observacao;
}