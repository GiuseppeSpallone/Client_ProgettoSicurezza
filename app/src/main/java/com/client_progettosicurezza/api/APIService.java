package com.client_progettosicurezza.api;

import com.client_progettosicurezza.results.ResultImmagine;
import com.client_progettosicurezza.results.ResultListaImmagini;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface APIService {

    @GET("immagini")
    Call<ResultListaImmagini> getImmagini();

    @GET("immagini/{id}")
    Call<ResultImmagine> getImmagine(@Path("id") Integer id);

}
