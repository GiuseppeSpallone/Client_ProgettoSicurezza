package com.client_progettosicurezza.results;

import java.util.ArrayList;

import com.client_progettosicurezza.models.Immagine;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultListaImmagini {

    @SerializedName("result")
    @Expose
    private Boolean result;
    @SerializedName("message")
    @Expose
    private String message = null;
    @SerializedName("immagini")
    @Expose
    private ArrayList<Immagine> immagini;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Immagine> getImmagini() {
        return immagini;
    }

    public void setImmagini(ArrayList<Immagine> immagini) {
        this.immagini = immagini;
    }
}

