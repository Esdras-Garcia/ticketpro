package com.ticketpro.evento;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class EventoBO {

    private EventoDAO dao = new EventoDAO();
    private Gson gson = new GsonBuilder().create(); 
    
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public String cadastrar(String nome, String dataTexto, String localizacao) {
        Map<String, Object> resp = new HashMap<>();
        try {
            LocalDateTime dataEvento = LocalDateTime.parse(dataTexto, formatter);
            
            if (dataEvento.isBefore(LocalDateTime.now())) {
                resp.put("status", "ERRO");
                resp.put("mensagem", "A data do evento deve ser no futuro.");
                return gson.toJson(resp);
            }

            Evento novo = new Evento(nome, dataEvento, localizacao);
            if (dao.salvar(novo)) {
                resp.put("status", "SUCESSO");
                resp.put("mensagem", "Evento criado.");
            } else {
                resp.put("status", "ERRO");
                resp.put("mensagem", "Erro ao salvar.");
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
            if (dao.excluir(id)) {
                resp.put("status", "SUCESSO");
                resp.put("mensagem", "Evento excluído (inativado).");
            } else {
                resp.put("status", "ERRO");
                resp.put("mensagem", "Erro ao excluir.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("status", "ERRO_CRITICO");
            resp.put("mensagem", e.getMessage());
        }
        return gson.toJson(resp);
    }
}