package com.example.covivid.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covivid.Adapters.Reports.NewsAdapter;
import com.example.covivid.Model.News;
import com.example.covivid.Model.NewsResponse;
import com.example.covivid.R;
import com.example.covivid.Retrofit.ITheGuardianAPI;
import com.example.covivid.Utils.Common;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsActivity extends AppCompatActivity {

    private ITheGuardianAPI theGuardianApi;
    private NewsAdapter adapter;
    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.requestFullScreenActivity(this);
        setContentView(R.layout.activity_news);
        recycler = findViewById(R.id.news_rv);
        theGuardianApi = Common.getNewsApi(this);
        getNews();
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

           }
       });

    }

    private void displayNews(List<News> news)
    {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(this, news);
        recycler.setAdapter(adapter);
    }
}