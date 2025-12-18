package com.ticketpro.usuario;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class UsuarioBO {
    
    private UsuarioDAO dao = new UsuarioDAO();
    private Gson gson = new Gson();

    public String login(String email, String senha) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Usuario usuario = dao.buscarPorCredenciais(email, senha);
            
            if (usuario != null) {
                usuario.setSenha(null); 
                resp.put("status", "SUCESSO");
                resp.put("usuario", usuario);
            } else {
                resp.put("status", "ERRO");
                resp.put("mensagem", "Email ou senha inválidos.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("status", "ERRO_CRITICO");
            resp.put("mensagem", e.getMessage());
        }
        return gson.toJson(resp);
    }

    public String cadastrar(String nome, String email, String senha) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (dao.emailExiste(email)) {
                resp.put("status", "ERRO");
                resp.put("mensagem", "Email já cadastrado.");
                return gson.toJson(resp);
            }

            Usuario novo = new Usuario(nome, email, senha, "CLIENTE");
            if (dao.salvar(novo)) {
                resp.put("status", "SUCESSO");
                resp.put("mensagem", "Usuário criado com sucesso.");
            } else {
                resp.put("status", "ERRO");
                resp.put("mensagem", "Erro ao salvar no banco.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("status", "ERRO_CRITICO");
            resp.put("mensagem", e.getMessage());
        }
        return gson.toJson(resp);
    }

    public String recuperarSenha(String email, String novaSenha) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (!dao.emailExiste(email)) {
                resp.put("status", "ERRO");
                resp.put("mensagem", "E-mail não encontrado na base de dados.");
                return gson.toJson(resp);
            }

            if (dao.atualizarSenha(email, novaSenha)) {
                resp.put("status", "SUCESSO");
                resp.put("mensagem", "Senha alterada com sucesso.");
            } else {
                resp.put("status", "ERRO");
                resp.put("mensagem", "Erro ao atualizar a senha.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("status", "ERRO_CRITICO");
            resp.put("mensagem", e.getMessage());
        }
        return gson.toJson(resp);
    }

    public String editar(int id, String novoNome, String novoEmail) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (dao.emailJaExisteParaOutro(novoEmail, id)) {
                resp.put("status", "ERRO");
                resp.put("mensagem", "Este e-mail já está em uso por outro usuário.");
                return gson.toJson(resp);
            }

            Usuario usuario = new Usuario();
            usuario.setId(id);
            usuario.setNome(novoNome);
            usuario.setEmail(novoEmail);

            if (dao.atualizar(usuario)) {
                resp.put("status", "SUCESSO");
                resp.put("mensagem", "Dados atualizados com sucesso.");
            } else {
                resp.put("status", "ERRO");
                resp.put("mensagem", "Usuário não encontrado.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("status", "ERRO_CRITICO");
            resp.put("mensagem", e.getMessage());
        }
        return gson.toJson(resp);
    }

    public String adicionarSaldo(int usuarioId, BigDecimal valor) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
                resp.put("status", "ERRO");
                resp.put("mensagem", "O valor deve ser maior que zero.");
                return gson.toJson(resp);
            }

            if (dao.adicionarSaldo(usuarioId, valor)) {
                BigDecimal novoSaldo = dao.buscarSaldo(usuarioId);
                
                resp.put("status", "SUCESSO");
                resp.put("mensagem", "Saldo adicionado com sucesso!");
                resp.put("novoSaldo", novoSaldo);
            } else {
                resp.put("status", "ERRO");
                resp.put("mensagem", "Usuário não encontrado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("status", "ERRO_CRITICO");
            resp.put("mensagem", e.getMessage());
        }
        return gson.toJson(resp);
    }
}