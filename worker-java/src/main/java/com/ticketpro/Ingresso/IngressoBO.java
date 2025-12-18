package com.ticketpro.ingresso;

import com.google.gson.Gson;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class IngressoBO {

    private IngressoDAO dao = new IngressoDAO();
    private Gson gson = new Gson();

    public String gerarLote(int eventoId, String prefixo, int quantidade, BigDecimal preco) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (quantidade <= 0) {
                resp.put("status", "ERRO");
                resp.put("mensagem", "Quantidade deve ser maior que zero.");
                return gson.toJson(resp);
            }
            if (preco.compareTo(BigDecimal.ZERO) <= 0) {
                resp.put("status", "ERRO");
                resp.put("mensagem", "Preço deve ser positivo.");
                return gson.toJson(resp);
            }

            if (dao.gerarLote(eventoId, prefixo, quantidade, preco)) {
                resp.put("status", "SUCESSO");
                resp.put("mensagem", quantidade + " ingressos gerados com sucesso.");
            } else {
                resp.put("status", "ERRO");
                resp.put("mensagem", "Erro ao gerar lote.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("status", "ERRO_CRITICO");
            resp.put("mensagem", e.getMessage());
        }
        return gson.toJson(resp);
    }

    public String listarPorEvento(int eventoId) {
        try {
            List<Ingresso> lista = dao.listarPorEvento(eventoId);
            return gson.toJson(lista);
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }
    
    public String limparEstoque(int eventoId) {
         Map<String, Object> resp = new HashMap<>();
         try {
             dao.excluirTodosDoEvento(eventoId);
             resp.put("status", "SUCESSO");
             resp.put("mensagem", "Estoque não vendido removido.");
         } catch(Exception e) {
             resp.put("status", "ERRO");
             resp.put("mensagem", e.getMessage());
         }
         return gson.toJson(resp);
    }
}