package com.example.covivid.Adapters.Reports;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covivid.Contracts.IItemClickListener;
import com.example.covivid.Model.News;
import com.example.covivid.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    Context context;
    List<News> news;

    public NewsAdapter(Context context, List<News> news) {
        this.context = context;
        this.news = news;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout;
        layout = LayoutInflater
                .from(context)
                .inflate(R.layout.news_card_large_picture, parent, false);

        return new NewsViewHolder(layout);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.headline.setText(news.get(position).getFields().getHeadline());
        holder.brief.setText(news.get(position).getFields().getTrailText());
        Picasso
                .get()
                .load(news.get(position).getFields().getThumbnail())
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView headline, brief;
        ImageView thumbnail;
        IItemClickListener clickListener;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            headline = itemView.findViewById(R.id.news_headline);
            brief = itemView.findViewById(R.id.news_brief);
            thumbnail = itemView.findViewById(R.id.news_thumbnail);

            itemView.setOnClickListener(this);
        }

        public void setClickListener(IItemClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view);
        }
    }
}