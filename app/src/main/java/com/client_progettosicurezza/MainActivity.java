package com.client_progettosicurezza;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.client_progettosicurezza.adapters.AdapterImmagine;
import com.client_progettosicurezza.api.APIService;
import com.client_progettosicurezza.models.Immagine;
import com.client_progettosicurezza.results.ResultListaImmagini;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.client_progettosicurezza.api.APIUrl.BASE_URL;

public class MainActivity extends AppCompatActivity {

    //new
    public static final int PERMISSION_CODE = 1;
    //new

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //new
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
        //new

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        load();

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(true);

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        swipeRefresh.setRefreshing(false);
                        load();
                    }
                },100);
            }
        });
    }

    private void load() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);

        Call<ResultListaImmagini> call = service.getImmagini();

        call.enqueue(new Callback<ResultListaImmagini>() {
            @Override
            public void onResponse(Call<ResultListaImmagini> call, Response<ResultListaImmagini> response) {

                ArrayList<Immagine> immagini = response.body().getImmagini();

                adapter = new AdapterImmagine(immagini, getApplicationContext());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<ResultListaImmagini> call, Throwable t) {

            }
        });
    }
}
