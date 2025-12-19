package com.ticketpro.evento;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.ticketpro.ingresso.IngressoDAO;

public class EventoBO {

    private EventoDAO dao = new EventoDAO();
    private Gson gson = new GsonBuilder().create(); 
    
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public String cadastrar(String nome, String dataTexto, String localizacao, int qtd, BigDecimal preco) {
        Map<String, Object> resp = new HashMap<>();
        try {
            LocalDateTime dataEvento = LocalDateTime.parse(dataTexto, formatter);
            
            if (dataEvento.isBefore(LocalDateTime.now())) {
                resp.put("status", "ERRO");
                resp.put("mensagem", "A data do evento deve ser no futuro.");
                return gson.toJson(resp);
            }
            
            if (qtd <= 0) {
                resp.put("status", "ERRO");
                resp.put("mensagem", "Capacidade deve ser positiva.");
                return gson.toJson(resp);
            }
            if (preco.compareTo(BigDecimal.ZERO) < 0) {
                resp.put("status", "ERRO");
                resp.put("mensagem", "Preço não pode ser negativo.");
                return gson.toJson(resp);
            }

            Evento novo = new Evento(nome, dataEvento, localizacao, qtd, preco);
            
            int novoId = dao.salvar(novo);

            IngressoDAO ingressoDAO = new IngressoDAO();
            
            if (novoId > 0) {
                boolean ingressosGerados = ingressoDAO.gerarLote(novoId, "LOTE-1", qtd, preco);
                
                if (ingressosGerados) {
                    resp.put("status", "SUCESSO");
                    resp.put("mensagem", "Evento criado e " + qtd + " ingressos gerados!");
                } else {
                    resp.put("status", "ALERTA");
                    resp.put("mensagem", "Evento criado, mas falha ao gerar ingressos.");
                }
            } else {
                resp.put("status", "ERRO");
                resp.put("mensagem", "Erro ao salvar evento no banco.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("status", "ERRO_CRITICO");
            resp.put("mensagem", e.getMessage());
        }
        return gson.toJson(resp);
    }

    public String listar() {
        try {
            List<Evento> eventos = dao.listarAtivos();
            return gson.toJson(eventos);
        } catch (Exception e) {
            return "[]";
        }
    }

    public String atualizar(int id, String nome, String dataTexto, String localizacao) {
        Map<String, Object> resp = new HashMap<>();
        try {
            LocalDateTime dataEvento = LocalDateTime.parse(dataTexto, formatter);
            
            if (dataEvento.isBefore(LocalDateTime.now())) {
                resp.put("status", "ERRO");
                resp.put("mensagem", "A data do evento não pode ser no passado.");
                return gson.toJson(resp);
            }

            Evento evento = new Evento(nome, dataEvento, localizacao);
            evento.setId(id);

            if (dao.atualizar(evento)) {
                resp.put("status", "SUCESSO");
                resp.put("mensagem", "Evento atualizado com sucesso.");
            } else {
                resp.put("status", "ERRO");
                resp.put("mensagem", "Evento não encontrado ou erro ao atualizar.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("status", "ERRO_CRITICO");
            resp.put("mensagem", e.getMessage());
        }
        return gson.toJson(resp);
    }

    public String excluir(int id) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (dao.desativar(id)) {
                resp.put("status", "SUCESSO");
                resp.put("mensagem", "Evento excluído (inativado) com sucesso.");
            } else {
                resp.put("status", "ERRO");
                resp.put("mensagem", "Erro ao excluir: Evento não encontrado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("status", "ERRO_CRITICO");
            resp.put("mensagem", e.getMessage());
        }
        return gson.toJson(resp);
    }
}