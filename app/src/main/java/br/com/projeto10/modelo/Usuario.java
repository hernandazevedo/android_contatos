package br.com.projeto10.modelo;

import java.io.Serializable;

/**
 * Created by atc1n on 21/02/2017.
 */

public class Usuario implements Serializable {
    private Integer idUsuario;
    private String nome;
    private String login;
    private String senha;
    private byte[] blobFoto;


    public Usuario(){

    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public byte[] getBlobFoto() {
        return blobFoto;
    }

    public void setBlobFoto(byte[] blobFoto) {
        this.blobFoto = blobFoto;
    }
}
