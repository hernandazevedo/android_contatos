package br.com.projeto10.modelo;

import java.io.Serializable;

/**
 * Created by atc1n on 02/03/2017.
 */
public class Operadora implements Serializable{

    private Long idOperadora;
    private String nome;
    private Double preco;

    public Operadora(){

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Long getIdOperadora() {
        return idOperadora;
    }

    public void setIdOperadora(Long idOperadora) {
        this.idOperadora = idOperadora;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Operadora operadora = (Operadora) o;

        return idOperadora != null ? idOperadora.equals(operadora.idOperadora) : operadora.idOperadora == null;

    }

    @Override
    public int hashCode() {
        return idOperadora != null ? idOperadora.hashCode() : 0;
    }

    @Override
    public String toString() {
        return nome;
    }
}
