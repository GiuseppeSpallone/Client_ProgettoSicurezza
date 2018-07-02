package com.client_progettosicurezza.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.client_progettosicurezza.DownloadImmagine;
import com.client_progettosicurezza.R;
import com.client_progettosicurezza.models.Immagine;

import java.util.List;

public class AdapterImmagine extends RecyclerView.Adapter<AdapterImmagine.ViewHolder> {

    private List<Immagine> listaImmagine;
    private Context context;

    public AdapterImmagine(List<Immagine> listaImmagine, Context context) {
        this.listaImmagine = listaImmagine;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_immagine, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Immagine immagine = listaImmagine.get(position);

        holder.textView.setText(immagine.getTitolo());

        Glide.with(context).load(immagine.getFile()).into(holder.imageView);

        //new
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                DownloadImmagine downloadImmagine = new DownloadImmagine();
                downloadImmagine.download(context, immagine.getTitolo(), immagine.getFormato(), immagine.getFile());
            }
        });
        //new

    }

    @Override
    public int getItemCount() {
        return listaImmagine.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.textView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);

        }

    }
}
