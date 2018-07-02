package com.client_progettosicurezza.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Immagine {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("titolo")
    @Expose
    private String titolo;

    @SerializedName("formato")
    @Expose
    private String formato;

    @SerializedName("file")
    @Expose
    private String file;

    public Immagine(Integer id, String titolo, String formato, String file) {
        this.id = id;
        this.titolo = titolo;
        this.formato = formato;
        this.file = file;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
