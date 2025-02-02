package com.escom.gestorpro.models;

public class Post {
    private String id;
    private String usuario;
    private long fecha;
    private String texto;
    private String image;
    private String idProyecto;
    private String tipo;

    public Post(){

    }

    public Post(String id, String usuario, long fecha, String texto, String image, String idProyecto, String tipo) {
        this.id = id;
        this.usuario = usuario;
        this.fecha = fecha;
        this.texto = texto;
        this.image = image;
        this.idProyecto = idProyecto;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(String idProyecto) {
        this.idProyecto = idProyecto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
