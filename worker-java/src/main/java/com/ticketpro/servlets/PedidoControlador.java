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

        int usuarioId = 1;
        int eventoId = 1;
        int quantidade = 8;

        String resultado = pedidoBO.registrarCompra(usuarioId, eventoId, quantidade);
        out.println(resultado);
    }
}
