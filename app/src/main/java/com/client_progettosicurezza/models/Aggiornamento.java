package com.client_progettosicurezza.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Aggiornamento {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("versione")
    @Expose
    private Integer versione;

    @SerializedName("file")
    @Expose
    private String file;

    public Aggiornamento(Integer id, Integer versione, String file) {
        this.id = id;
        this.versione = versione;
        this.file = file;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVersione() {
        return versione;
    }

    public void setVersione(Integer versione) {
        this.versione = versione;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
