package com.example.giovy.iumanji.database;

/**
 * Created by martina on 07/02/17.
 */

public class Pietanza {

    private Integer id;
    private String nome;
    private Double prezzo;

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @return the prezzo
     */
    public Double getPrezzo() {
        return prezzo;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @param prezzo the prezzo to set
     */
    public void setPrezzo(Double prezzo) {
        this.prezzo = prezzo;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

}
