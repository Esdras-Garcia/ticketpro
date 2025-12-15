package com.ticketpro.bo;

import com.ticketpro.dao.PedidoDAO;
import com.ticketpro.model.Pedido;

public class PedidoBO {
    private PedidoDAO pedidoDAO;

    public PedidoBO() {
        this.pedidoDAO = new PedidoDAO();
    }

    public String registrarCompra(int usuarioId, int eventoId, int quantidade) {
        if (quantidade <= 0) {
            return "ERRO: A quantidade deve ser maior que zero.";
        }

        if (quantidade > 5) {
            return "ERRO: Limite de 5 ingressos por pessoa.";
        }

        Pedido novoPedido = new Pedido(usuarioId, eventoId, quantidade);

        try {
            boolean sucesso = pedidoDAO.salvar(novoPedido);
            if (sucesso) {
                return "SUCESSO: Pedido registrado na fila de processamento.";
            } else {
                return "ERRO: Falha ao salvar no banco.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERRO CRÍTICO: " + e.getMessage();
        }
    }
}