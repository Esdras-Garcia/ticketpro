package com.ticketpro.evento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Evento {
    private int id;
    private String nome;
    private LocalDateTime dataEvento;
    private String localizacao;
    private int numeroMaximoIngressos;
    private BigDecimal preco;
    private boolean ativo;

    public Evento(String nome, LocalDateTime dataEvento, String localizacao, int numeroMaximoIngressos, BigDecimal preco) {
        this.nome = nome;
        this.dataEvento = dataEvento;
        this.localizacao = localizacao;
        this.numeroMaximoIngressos = numeroMaximoIngressos;
        this.preco = preco;
        this.ativo = true;
    }
    
    public Evento(String nome, LocalDateTime dataEvento, String localizacao) {
        this(nome, dataEvento, localizacao, 0, BigDecimal.ZERO);
    }
}