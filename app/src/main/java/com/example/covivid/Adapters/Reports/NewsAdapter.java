package com.example.covivid.Adapters.Reports;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covivid.Model.CovidNews.News;
import com.example.covivid.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.text.Html.FROM_HTML_MODE_COMPACT;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    Context context;
    List<News> news;
    private static ClickListener clickListener;

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
        holder.headline.setText(Html.fromHtml(news.get(position).getFields().getHeadline(), FROM_HTML_MODE_COMPACT));
        holder.brief.setText(Html.fromHtml(news.get(position).getFields().getTrailText(), FROM_HTML_MODE_COMPACT));
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


        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            headline = itemView.findViewById(R.id.news_headline);
            brief = itemView.findViewById(R.id.news_brief);
            thumbnail = itemView.findViewById(R.id.news_thumbnail);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        NewsAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}