package com.client_progettosicurezza;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

public class DownloadImmagine {
    DownloadManager downloadManager;

    public void download(Context context, String titolo, String formato, String file){
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(file);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, titolo + "." + formato);
        downloadManager.enqueue(request);
    }
}