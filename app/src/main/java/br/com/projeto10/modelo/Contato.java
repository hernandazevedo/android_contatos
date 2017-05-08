package br.com.projeto10.modelo;

import java.io.Serializable;

/**
 * Created by atc1n on 02/03/2017.
 */

public class Contato  implements Serializable {

    private Long idContato;
    private Long data; // nascimento in millis
    private String nome;
    private String telefone;
    private Operadora operadora;
    private String serial;//campo gerado automaticamente UUID

    public Contato(){

    }


    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Operadora getOperadora() {
        return operadora;
    }

    public void setOperadora(Operadora operadora) {
        this.operadora = operadora;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public Long getIdContato() {
        return idContato;
    }

    public void setIdContato(Long idContato) {
        this.idContato = idContato;
    }
}
