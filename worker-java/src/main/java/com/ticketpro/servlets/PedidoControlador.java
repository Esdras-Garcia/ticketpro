package com.ticketpro.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ticketpro.bo.PedidoBO;

@WebServlet("/comprar")
public class PedidoControlador extends HttpServlet {

    private PedidoBO pedidoBO = new PedidoBO();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            // Pega da URL ou usa valores padrão se não for informado
            String idParam = req.getParameter("usuarioId");
            String eventoParam = req.getParameter("eventoId");
            String qtdParam = req.getParameter("quantidade");

            int usuarioId = (idParam != null) ? Integer.parseInt(idParam) : 1;
            int eventoId = (eventoParam != null) ? Integer.parseInt(eventoParam) : 1;
            int quantidade = (qtdParam != null) ? Integer.parseInt(qtdParam) : 1; // Mudei padrão para 1

            String resultado = pedidoBO.registrarCompra(usuarioId, eventoId, quantidade);
            out.println(resultado);
            
        } catch (Exception e) {
            out.println("Erro nos parâmetros: " + e.getMessage());
        }
    }
}
