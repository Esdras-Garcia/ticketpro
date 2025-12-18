package com.ticketpro.ingresso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.ticketpro.db.Conexao;

public class IngressoDAO {

    public boolean gerarLote(int eventoId, String prefixo, int quantidade, java.math.BigDecimal preco) 
            throws SQLException, ClassNotFoundException {
        
        String sql = "INSERT INTO ingressos (evento_id, codigo_assento, preco, disponivel) VALUES (?, ?, ?, TRUE)";
        
        try (Connection conn = Conexao.getConexao()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 1; i <= quantidade; i++) {
                    stmt.setInt(1, eventoId);
                    stmt.setString(2, prefixo + "-" + i);
                    stmt.setBigDecimal(3, preco);
                    stmt.addBatch();
                }
                
                stmt.executeBatch();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public List<Ingresso> listarPorEvento(int eventoId) throws SQLException, ClassNotFoundException {
        List<Ingresso> lista = new ArrayList<>();
        String sql = "SELECT * FROM ingressos WHERE evento_id = ? ORDER BY id";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventoId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Ingresso i = new Ingresso();
                i.setId(rs.getInt("id"));
                i.setEventoId(rs.getInt("evento_id"));
                i.setCodigoAssento(rs.getString("codigo_assento"));
                i.setPreco(rs.getBigDecimal("preco"));
                i.setDisponivel(rs.getBoolean("disponivel"));
                lista.add(i);
            }
        }
        return lista;
    }
    
    public boolean excluirTodosDoEvento(int eventoId) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM ingressos WHERE evento_id = ? AND disponivel = TRUE";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventoId);
            return stmt.executeUpdate() > 0;
        }
    }
}