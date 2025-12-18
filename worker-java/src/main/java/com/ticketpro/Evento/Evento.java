package com.ticketpro.evento;

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
    private boolean ativo;

    public Evento(String nome, LocalDateTime dataEvento, String localizacao) {
        this.nome = nome;
        this.dataEvento = dataEvento;
        this.localizacao = localizacao;
        this.ativo = true;
    }
}