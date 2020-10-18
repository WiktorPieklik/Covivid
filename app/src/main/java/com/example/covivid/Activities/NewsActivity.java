package com.example.covivid.Activities;

import android.os.Bundle;

import com.example.covivid.Adapters.Reports.NewsAdapter;
import com.example.covivid.Model.News;
import com.example.covivid.Model.NewsFields;
import com.example.covivid.Model.NewsPack;
import com.example.covivid.Retrofit.ITheGuardianAPI;
import com.example.covivid.R;
import com.example.covivid.Utils.Common;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.covivid.Utils.Common.GUARDIAN_API_KEY;

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
        theGuardianApi = Common.getNewsApi();
        //TODO: Here is the error. Just uncomment the line below this and comment everything else after it upto the end of onCreate();
        //getNews();
        List<News> test = new ArrayList<News>();
        test.add(
                new News("url", new NewsFields("HEADLINE",
                        "https://media.guim.co.uk/97931aa8fa700020f03f6a8fb45558c5cfb870db/0_276_5096_3057/500.jpg",
                        "Description text")));

        displayNews(test);
    }


    private void getNews() {
        Call<NewsPack> call = theGuardianApi.testCall();
//                .getNews("covid",
//                        GUARDIAN_API_KEY,
//                        new String[]{
//                                "headline",
//                                "thumbnail",
//                                "trailText"});

        call.enqueue(new Callback<NewsPack>() {
            @Override
            public void onResponse(Call<NewsPack> call, Response<NewsPack> response) {
                if (response.isSuccessful())
                {
                    List<News> news = response.body().getResults();
                    displayNews(news);
                }


            }

            @Override
            public void onFailure(Call<NewsPack> call, Throwable t) {

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