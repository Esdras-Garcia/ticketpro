package com.ticketpro.evento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.ticketpro.db.Conexao;

public class EventoDAO {

    public int salvar(Evento e) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO eventos (nome, data_evento, localizacao, numero_maximo_ingressos, preco, ativo) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, e.getNome());
            stmt.setTimestamp(2, Timestamp.valueOf(e.getDataEvento()));
            stmt.setString(3, e.getLocalizacao());
            stmt.setInt(4, e.getNumeroMaximoIngressos());
            stmt.setBigDecimal(5, e.getPreco());
            stmt.setBoolean(6, true);
            
            int rows = stmt.executeUpdate();
            
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    public List<Evento> listarAtivos() throws SQLException, ClassNotFoundException {
        List<Evento> lista = new ArrayList<>();
        String sql = "SELECT * FROM eventos WHERE ativo = TRUE ORDER BY data_evento";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Evento e = new Evento();
                e.setId(rs.getInt("id"));
                e.setNome(rs.getString("nome"));
                e.setLocalizacao(rs.getString("localizacao"));
                e.setAtivo(rs.getBoolean("ativo"));
                e.setDataEvento(rs.getTimestamp("data_evento").toLocalDateTime());
                e.setNumeroMaximoIngressos(rs.getInt("numero_maximo_ingressos"));
                e.setPreco(rs.getBigDecimal("preco"));
                
                lista.add(e);
            }
        }
        return lista;
    }

    public boolean desativar(int id) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE eventos SET ativo = FALSE WHERE id = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean atualizar(Evento e) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE eventos SET nome = ?, data_evento = ?, localizacao = ? WHERE id = ?";
        try (Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, e.getNome());
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(e.getDataEvento()));
            stmt.setString(3, e.getLocalizacao());
            stmt.setInt(4, e.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean excluir(int id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM eventos WHERE id = ?";
        try (Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}