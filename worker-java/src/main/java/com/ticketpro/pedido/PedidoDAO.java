package com.ticketpro.pedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.ticketpro.db.Conexao;

public class PedidoDAO {

    public Long criarPedido(Pedido p) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO pedidos_fila (usuario_id, evento_id, quantidade, valor_total, status, data_pedido) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, p.getUsuarioId());
            stmt.setInt(2, p.getEventoId());
            stmt.setInt(3, p.getQuantidade());
            stmt.setBigDecimal(4, p.getValorTotal());
            stmt.setString(5, p.getStatus()); // PENDENTE
            stmt.setTimestamp(6, java.sql.Timestamp.valueOf(p.getDataPedido()));
            
            int rows = stmt.executeUpdate();
            
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
        }
        return null;
    }

    public List<Pedido> listarPorUsuario(int usuarioId) throws SQLException, ClassNotFoundException {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedidos_fila WHERE usuario_id = ? ORDER BY id DESC";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Pedido p = new Pedido();
                p.setId(rs.getLong("id"));
                p.setUsuarioId(rs.getInt("usuario_id"));
                p.setEventoId(rs.getInt("evento_id"));
                p.setQuantidade(rs.getInt("quantidade"));
                p.setValorTotal(rs.getBigDecimal("valor_total"));
                p.setStatus(rs.getString("status"));
                p.setMensagemErro(rs.getString("mensagem_erro"));
                p.setDataPedido(rs.getTimestamp("data_pedido").toLocalDateTime());
                
                if (rs.getTimestamp("data_atualizacao") != null) {
                    p.setDataAtualizacao(rs.getTimestamp("data_atualizacao").toLocalDateTime());
                }
                
                lista.add(p);
            }
        }
        return lista;
    }
}