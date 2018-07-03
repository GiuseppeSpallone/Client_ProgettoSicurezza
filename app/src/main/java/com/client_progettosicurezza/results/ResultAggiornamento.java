package com.client_progettosicurezza.results;

import com.client_progettosicurezza.models.Aggiornamento;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultAggiornamento {
    @SerializedName("result")
    @Expose
    private Boolean result;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("aggiornamento")
    @Expose
    private Aggiornamento aggiornamento;

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

    public Aggiornamento getAggiornamento() {
        return aggiornamento;
    }

    public void setAggiornamento(Aggiornamento aggiornamento) {
        this.aggiornamento = aggiornamento;
    }
}
