package com.ticketpro.usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.ticketpro.db.Conexao;

public class UsuarioDAO {

    public Usuario buscarPorCredenciais(String email, String senha) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, senha);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearUsuario(rs);
            }
        }
        return null;
    }

    public boolean emailExiste(String email) throws SQLException, ClassNotFoundException {
        String sql = "SELECT 1 FROM usuarios WHERE email = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            return stmt.executeQuery().next();
        }
    }

    public boolean emailJaExisteParaOutro(String email, int meuId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT 1 FROM usuarios WHERE email = ? AND id <> ?";
        try (Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setInt(2, meuId);
            return stmt.executeQuery().next();
        }
    }

    public boolean atualizar(Usuario u) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE usuarios SET nome = ?, email = ? WHERE id = ?";
        try (Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getEmail());
            stmt.setInt(3, u.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean salvar(Usuario u) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO usuarios (nome, email, senha, saldo, tipo_usuario) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getEmail());
            stmt.setString(3, u.getSenha());
            stmt.setBigDecimal(4, u.getSaldo());
            stmt.setString(5, u.getTipoUsuario());
            
            return stmt.executeUpdate() > 0;
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNome(rs.getString("nome"));
        u.setEmail(rs.getString("email"));
        u.setSenha(rs.getString("senha"));
        u.setSaldo(rs.getBigDecimal("saldo"));
        u.setTipoUsuario(rs.getString("tipo_usuario"));
        u.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        return u;
    }

    public boolean atualizarSenha(String email, String novaSenha) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE usuarios SET senha = ? WHERE email = ?";
        
        try (Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, novaSenha);
            stmt.setString(2, email);
            
            return stmt.executeUpdate() > 0;
        }
    }
}