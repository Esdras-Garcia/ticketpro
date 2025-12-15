package com.ticketpro.model;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Pedido {
    private int id;
    private int usuarioId;
    private int eventoId;
    private int quantidade;

    private String status = "PENDENTE";
    private LocalDateTime dataPedido = LocalDateTime.now();

    public Pedido(int usuarioId, int eventoId, int quantidade) {
        this.usuarioId = usuarioId;
        this.eventoId = eventoId;
        this.quantidade = quantidade;
    }
}
