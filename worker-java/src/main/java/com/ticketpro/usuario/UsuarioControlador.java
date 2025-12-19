package com.ticketpro.usuario;

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
import com.google.gson.JsonSyntaxException;

public class UsuarioControlador extends HttpServlet {

    private UsuarioBO usuarioBO = new UsuarioBO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processarRequisicao(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processarRequisicao(req, resp);
    }

    protected void processarRequisicao(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String acao = req.getParameter("acao");

        if (acao == null) {
            out.print("{\"status\": \"ERRO\", \"mensagem\": \"Parâmetro 'acao' obrigatório.\"}");
            return;
        }

        JsonObject json = null;
        try {
            BufferedReader reader = req.getReader();
            if (reader != null) {
                try {
                    json = gson.fromJson(reader, JsonObject.class);
                } catch (JsonSyntaxException e) {
                }
            }
        } catch (Exception e) {
        }

        try {
            switch (acao) {
                case "buscar":
                    String idStr = req.getParameter("id");
                    if (idStr == null && json != null && json.has("id")) {
                        idStr = json.get("id").getAsString();
                    }
                    
                    if (idStr != null) {
                        out.print(usuarioBO.buscar(Integer.parseInt(idStr)));
                    } else {
                        out.print("{\"status\": \"ERRO\", \"mensagem\": \"ID obrigatório.\"}");
                    }
                    break;

                case "login":
                    if (json == null) { out.print(erroJson()); return; }
                    out.print(usuarioBO.login(
                        json.get("email").getAsString(), 
                        json.get("senha").getAsString()
                    ));
                    break;

                case "cadastro":
                    if (json == null) { out.print(erroJson()); return; }
                    out.print(usuarioBO.cadastrar(
                        json.get("nome").getAsString(),
                        json.get("email").getAsString(),
                        json.get("senha").getAsString()
                    ));
                    break;

                case "recuperarSenha":
                    if (json == null) { out.print(erroJson()); return; }
                    out.print(usuarioBO.recuperarSenha(
                        json.get("email").getAsString(),
                        json.get("senha").getAsString()
                    ));
                    break;

                case "editar":
                    if (json == null) { out.print(erroJson()); return; }
                    out.print(usuarioBO.editar(
                        json.get("id").getAsInt(),
                        json.get("nome").getAsString(),
                        json.get("email").getAsString()
                    ));
                    break;

                case "adicionarSaldo":
                    if (json == null) { out.print(erroJson()); return; }
                    out.print(usuarioBO.adicionarSaldo(
                        json.get("usuarioId").getAsInt(),
                        json.get("valor").getAsBigDecimal()
                    ));
                    break;

                default:
                    out.print("{\"status\": \"ERRO\", \"mensagem\": \"Ação desconhecida: " + acao + "\"}");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\": \"ERRO\", \"mensagem\": \"Erro interno: " + e.getMessage() + "\"}");
        }
    }
    
    private String erroJson() {
        return "{\"status\": \"ERRO\", \"mensagem\": \"JSON inválido ou vazio.\"}";
    }
}