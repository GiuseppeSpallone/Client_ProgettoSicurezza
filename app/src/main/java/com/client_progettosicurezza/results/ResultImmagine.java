package com.client_progettosicurezza.results;

import com.client_progettosicurezza.models.Immagine;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultImmagine {

    @SerializedName("result")
    @Expose
    private Boolean result;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("immagine")
    @Expose
    private Immagine immagine;

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

    public Immagine getImmagine() {
        return immagine;
    }

    public void setImmagine(Immagine immagine) {
        this.immagine = immagine;
    }
}
