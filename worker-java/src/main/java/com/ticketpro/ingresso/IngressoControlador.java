package com.ticketpro.ingresso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class IngressoControlador extends HttpServlet {

    private IngressoBO ingressoBO = new IngressoBO();
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
            out.print("{\"erro\": \"Parâmetro 'acao' obrigatório.\"}");
            return;
        }

        try {
            switch (acao) {
                case "gerarLote": {
                    BufferedReader reader = req.getReader();
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    
                    int eventoId = json.get("eventoId").getAsInt();
                    String prefixo = json.get("prefixo").getAsString();
                    int qtd = json.get("quantidade").getAsInt();
                    BigDecimal preco = json.get("preco").getAsBigDecimal();
                    
                    out.print(ingressoBO.gerarLote(eventoId, prefixo, qtd, preco));
                    break;
                }

                case "listar": {
                    String idParam = req.getParameter("eventoId");
                    if (idParam != null) {
                        out.print(ingressoBO.listarPorEvento(Integer.parseInt(idParam)));
                    } else {
                        out.print("{\"erro\": \"Parâmetro 'eventoId' obrigatório para listar.\"}");
                    }
                    break;
                }
                
                case "limparEstoque": {
                     String idParam = req.getParameter("eventoId");
                     if (idParam != null) {
                        out.print(ingressoBO.limparEstoque(Integer.parseInt(idParam)));
                     } else {
                         out.print("{\"erro\": \"ID obrigatório\"}");
                     }
                     break;
                }

                default:
                    out.print("{\"erro\": \"Ação desconhecida: " + acao + "\"}");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\": \"ERRO\", \"mensagem\": \"Erro interno: " + e.getMessage() + "\"}");
        }
    }
}