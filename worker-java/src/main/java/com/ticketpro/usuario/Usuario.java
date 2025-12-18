package com.ticketpro.usuario;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String senha;
    private BigDecimal saldo = BigDecimal.ZERO;
    private String tipoUsuario;
    private LocalDateTime dataCriacao;

    public Usuario(String nome, String email, String senha, String tipoUsuario) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
        this.saldo = BigDecimal.ZERO;
        this.dataCriacao = LocalDateTime.now();
    }
}