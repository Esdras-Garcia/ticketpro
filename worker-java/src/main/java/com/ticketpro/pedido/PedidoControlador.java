package com.ticketpro.pedido;

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

public class PedidoControlador extends HttpServlet {

    private PedidoBO pedidoBO = new PedidoBO();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processarRequisicao(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processarRequisicao(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        configurarCORS(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    protected void processarRequisicao(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        configurarCORS(resp);
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String acao = req.getParameter("acao");

        if (acao == null) {
            out.print("{\"erro\": \"Parâmetro 'acao' obrigatório.\"}");
            return;
        }

        try {
            switch (acao) {
                case "comprar": {
                    BufferedReader reader = req.getReader();
                    JsonObject json = gson.fromJson(reader, JsonObject.class);

                    int usuarioId = json.get("usuarioId").getAsInt();
                    int eventoId = json.get("eventoId").getAsInt();
                    int quantidade = json.get("quantidade").getAsInt();
                    BigDecimal precoUnitario = json.get("precoUnitario").getAsBigDecimal();

                    out.print(pedidoBO.entrarNaFila(usuarioId, eventoId, quantidade, precoUnitario));
                    break;
                }

                case "listarHistorico": {
                    String uidParam = req.getParameter("usuarioId");
                    
                    if (uidParam != null) {
                        out.print(pedidoBO.listarHistorico(Integer.parseInt(uidParam)));
                    } else {
                        out.print("{\"erro\": \"usuarioId obrigatório\"}");
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

    private void configurarCORS(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}