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

public class UsuarioControlador extends HttpServlet {

    private UsuarioBO usuarioBO = new UsuarioBO();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String acao = req.getParameter("acao");

        if (acao == null) {
            out.print("{\"status\": \"ERRO\", \"mensagem\": \"Parâmetro 'acao' obrigatório.\"}");
            return;
        }

        try {

            BufferedReader reader = req.getReader();
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            
            switch (acao) {
                case "login":
                    String emailLogin = json.get("email").getAsString();
                    String senhaLogin = json.get("senha").getAsString();
                    out.print(usuarioBO.login(emailLogin, senhaLogin));
                    break;

                case "cadastro":
                    String nome = json.get("nome").getAsString();
                    String emailCad = json.get("email").getAsString();
                    String senhaCad = json.get("senha").getAsString();
                    out.print(usuarioBO.cadastrar(nome, emailCad, senhaCad));
                    break;

                case "recuperarSenha":
                    String emailRec = json.get("email").getAsString();
                    String novaSenha = json.get("senha").getAsString();
                    out.print(usuarioBO.recuperarSenha(emailRec, novaSenha));
                    break;

                case "editar":
                    int idUser = json.get("id").getAsInt();
                    String novoNome = json.get("nome").getAsString();
                    String novoEmail = json.get("email").getAsString();
                    out.print(usuarioBO.editar(idUser, novoNome, novoEmail));
                    break;

                case "adicionarSaldo":
                    int idParaSaldo = json.get("usuarioId").getAsInt();
                    BigDecimal valorSaldo = json.get("valor").getAsBigDecimal();
                    out.print(usuarioBO.adicionarSaldo(idParaSaldo, valorSaldo));
                    break;

                default:
                    out.print("{\"status\": \"ERRO\", \"mensagem\": \"Ação desconhecida: " + acao + "\"}");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\": \"ERRO\", \"mensagem\": \"Erro interno no servidor.\"}");
        }
    }
}