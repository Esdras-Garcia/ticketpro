package com.ticketpro.pedido;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedidoBO {

    private PedidoDAO dao = new PedidoDAO();
    
    private Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> 
            new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
        .create();

    public String entrarNaFila(int usuarioId, int eventoId, int quantidade, BigDecimal valorUnitario) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (quantidade <= 0) {
                resp.put("status", "ERRO");
                resp.put("mensagem", "Quantidade inválida.");
                return gson.toJson(resp);
            }

            BigDecimal total = valorUnitario.multiply(new BigDecimal(quantidade));

            Pedido pedido = new Pedido(usuarioId, eventoId, quantidade, total);
            
            Long idGerado = dao.criarPedido(pedido);

            if (idGerado != null) {
                resp.put("status", "SUCESSO");
                resp.put("mensagem", "Pedido recebido! Processando na fila...");
                resp.put("pedidoId", idGerado);
            } else {
                resp.put("status", "ERRO");
                resp.put("mensagem", "Erro ao registrar pedido.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("status", "ERRO_CRITICO");
            resp.put("mensagem", e.getMessage());
        }
        return gson.toJson(resp);
    }

    public String listarHistorico(int usuarioId) {
        try {
            List<Pedido> historico = dao.listarPorUsuario(usuarioId);
            return gson.toJson(historico);
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }
}