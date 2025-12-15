package com.ticketpro.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.ticketpro.db.Conexao;
import com.ticketpro.model.Pedido;

public class PedidoDAO {
    public boolean salvar(Pedido pedido) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO pedidos_fila (usuario_id, evento_id, quantidade, status, data_pedido) VALUES (?, ?, ?, ?, ?);";

        try (Connection conn = Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pedido.getUsuarioId());
            stmt.setInt(2, pedido.getEventoId());
            stmt.setInt(3, pedido.getQuantidade());
            stmt.setString(4, pedido.getStatus());
            stmt.setObject(5, pedido.getDataPedido());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }
    }
}
