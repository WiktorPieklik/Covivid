package com.example.covivid.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covivid.Adapters.Reports.NewsAdapter;
import com.example.covivid.Model.CovidNews.News;
import com.example.covivid.Model.CovidNews.NewsResponse;
import com.example.covivid.R;
import com.example.covivid.Retrofit.ITheGuardianAPI;
import com.example.covivid.Utils.Common;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFragment extends Fragment  {

    private ITheGuardianAPI theGuardianApi;
    private NewsAdapter adapter;
    private RecyclerView recycler;
    private TextView no_news_tv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        recycler = rootView.findViewById(R.id.news_rv);
        no_news_tv = rootView.findViewById(R.id.no_news_text);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getNews();
        no_news_tv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        theGuardianApi = Common.getNewsApi(context);
    }

    private void getNews()
    {
       Call<NewsResponse> call = theGuardianApi.getNews("covid", Common.GUARDIAN_API_KEY, "thumbnail,trailText,headline");
       call.enqueue(new Callback<NewsResponse>() {
           @Override
           public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
               if(response.isSuccessful())
               {
                   NewsResponse newsResponse = response.body();
                   displayNews(newsResponse.getNewsPack().getNews());
               }
           }

           @Override
           public void onFailure(Call<NewsResponse> call, Throwable t) {
               Toast.makeText(getActivity(), "Couldn't fetch news", Toast.LENGTH_SHORT).show();
               Log.d("ERROR", "The Guardian Api call failure");
               no_news_tv.setVisibility(View.VISIBLE);
           }
       });

    }

    private void displayNews(List<News> news)
    {
        Log.d("DEBUG", "GetActivity() = " + getActivity().toString());
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new NewsAdapter(getActivity(), news);
        adapter.setOnItemClickListener((position, v) -> {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(news.get(position).getUrl())));;
        });
        recycler.setAdapter(adapter);
        no_news_tv.setVisibility(View.INVISIBLE);
    }
}