package com.example.covivid.Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class NewsFragment extends Fragment {

    private ITheGuardianAPI theGuardianApi;
    private NewsAdapter adapter;
    private RecyclerView recycler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        recycler = rootView.findViewById(R.id.news_rv);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        theGuardianApi = Common.getNewsApi(getActivity());
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
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new NewsAdapter(getActivity(), news);
        recycler.setAdapter(adapter);
    }
}