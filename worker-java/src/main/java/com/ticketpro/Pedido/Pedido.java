package com.ticketpro.pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Pedido {
    private Long id;
    private int usuarioId;
    private int eventoId;
    private int quantidade;
    private BigDecimal valorTotal;
    private String status;
    private String mensagemErro;
    private LocalDateTime dataPedido;
    private LocalDateTime dataAtualizacao;

    public Pedido(int usuarioId, int eventoId, int quantidade, BigDecimal valorTotal) {
        this.usuarioId = usuarioId;
        this.eventoId = eventoId;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
        this.status = "PENDENTE";
        this.dataPedido = LocalDateTime.now();
    }
}