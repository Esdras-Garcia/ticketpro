package com.ticketpro.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ticketpro.db.Conexao;

@WebServlet("/comprar")
public class SimularCompraServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();
        
        String sql = "INSERT INTO pedidos_fila (usuario_id, evento_id, quantidade, status, data_pedido) VALUES (?, ?, ?, 'PENDENTE', NOW());";

        try (Connection conn = Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1,1); // Usuario ID 1
            stmt.setInt(2, 1); // Evento ID 1
            stmt.setInt(3, 1); //Quantidade

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                out.println("SUCESSO: Novo pedido de compra inserido na fila!");
                out.println("Usuário: Ana | Evento: Tech Conference | Status: PENDENTE");
            } else {
                out.println("ERRO: Nenhuma linha inserida.");
            }
        } catch (Exception e) {
            out.println("ERRO NO PROCESSAMENTO:");
            e.printStackTrace(out);
        }
    }
}
