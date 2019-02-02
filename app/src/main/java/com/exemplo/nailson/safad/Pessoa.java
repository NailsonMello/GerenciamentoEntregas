package com.exemplo.nailson.safad;

/**
 * Created by Nailson on 10/01/2018.
 */

public class Pessoa {
    private String imagem;
    private String telefone;
    private String nome;
    private String cliente;
    private String vendedor;
    private String endereco;
    private String fantasia;
    private String status;
    private String data;
    private String codigo;
    private String cep;
    private Double porcentagem;

    public Pessoa(){

    }

    public Pessoa(String imagem, String telefone, String nome, String cliente, String vendedor, String endereco, String fantasia, String status, String data, String codigo, String cep, Double porcentagem) {
        this.imagem = imagem;
        this.telefone = telefone;
        this.nome = nome;
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.endereco = endereco;
        this.fantasia = fantasia;
        this.status = status;
        this.data = data;
        this.codigo = codigo;
        this.cep = cep;
        this.porcentagem = porcentagem;
    }

    public Double getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(Double porcentagem) {
        this.porcentagem = porcentagem;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getFantasia() {
        return fantasia;
    }

    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
