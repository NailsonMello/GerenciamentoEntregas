package com.exemplo.nailson.safad.ChatColection;

public class Messages {

    private String message, type, nome;
    private long  time;
    private boolean seen;

    private String from, imagem;

    public Messages(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Messages(String message, String type, long time, boolean seen, String imagem, String nome) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
        this.imagem = imagem;
        this.nome = nome;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getMessage() {
        return message;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Messages(){

    }

}
