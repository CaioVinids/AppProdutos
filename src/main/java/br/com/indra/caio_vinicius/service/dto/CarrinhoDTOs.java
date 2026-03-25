package br.com.indra.caio_vinicius.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CarrinhoDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Criar {
        private Long usuarioId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdicionarItem {
        private Long produtoId;
        private Integer quantidade;
        private Long usuarioId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AtualizarQuantidade {
        private Long itemId;
        private Integer quantidade;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoverItem {
        private Long itemId;
    }
}
