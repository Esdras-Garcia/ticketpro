package com.ticketpro.ingresso;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Ingresso {
    private int id;
    private int eventoId;
    private String codigoAssento;
    private BigDecimal preco;
    private boolean disponivel;
    private Integer usuarioId;
    private Integer pedidoId;
    private int versao;

    public Ingresso(int eventoId, String codigoAssento, BigDecimal preco) {
        this.eventoId = eventoId;
        this.codigoAssento = codigoAssento;
        this.preco = preco;
        this.disponivel = true;
        this.versao = 0;
    }
}