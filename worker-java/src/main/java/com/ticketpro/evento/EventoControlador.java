package com.ticketpro.evento;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class EventoControlador extends HttpServlet {

    private EventoBO eventoBO = new EventoBO();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processarRequisicao(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processarRequisicao(req, resp);
    }

    protected void processarRequisicao(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String acao = req.getParameter("acao");

        if (acao == null) {
            out.print("{\"erro\": \"Parâmetro 'acao' é obrigatório.\"}");
            return;
        }

        try {
            switch (acao) {
                case "listar":
                    out.print(eventoBO.listar());
                    break;

                case "cadastro": {
                    BufferedReader reader = req.getReader();
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    
                    String nome = json.get("nome").getAsString();
                    String data = json.get("data").getAsString();
                    String local = json.get("localizacao").getAsString();
                    
                    int qtd = json.get("numeroMaximoIngressos").getAsInt();
                    java.math.BigDecimal preco = json.get("preco").getAsBigDecimal();
                    
                    out.print(eventoBO.cadastrar(nome, data, local, qtd, preco));
                    break;
                }

                case "editar": {
                    BufferedReader reader = req.getReader();
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    
                    int id = json.get("id").getAsInt();
                    String nome = json.get("nome").getAsString();
                    String data = json.get("data").getAsString();
                    String local = json.get("localizacao").getAsString();
                    
                    out.print(eventoBO.atualizar(id, nome, data, local));
                    break;
                }

                case "excluir": {
                    String idParam = req.getParameter("id");
                    
                    if (idParam != null) {
                        out.print(eventoBO.excluir(Integer.parseInt(idParam)));
                    } else {
                        BufferedReader reader = req.getReader();
                        JsonObject json = gson.fromJson(reader, JsonObject.class);
                        int id = json.get("id").getAsInt();
                        out.print(eventoBO.excluir(id));
                    }
                    break;
                }

                default:
                    out.print("{\"erro\": \"Ação desconhecida: " + acao + "\"}");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\": \"ERRO\", \"mensagem\": \"Erro interno ao processar requisição: " + e.getMessage() + "\"}");
        }
    }
}