package com.codecenter1430gmail.jsonnew.NetworkAccessor;

import com.codecenter1430gmail.jsonnew.DataAccessor.Articles;
import com.codecenter1430gmail.jsonnew.DataAccessor.NewsSources;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;


public class Api {

    static final String BASE_URL="https://newsapi.org/v2/";
    static final String BASE_URL2="https://newsapi.org/v2/";
    static final String BASE_URL3="https://newsapi.org/v2/";

    public static VergeHeadlines vergeHeadlines=null;
    public static NewsChannel newsChannel=null;
    public static Headlines headlines=null;

    public static VergeHeadlines getVergeHeadlines()
    {
        if(vergeHeadlines==null)
        {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            vergeHeadlines =retrofit.create(VergeHeadlines.class);

        }
        return vergeHeadlines;
    }

    public static NewsChannel getNewsChannels()
    {
        if(newsChannel==null)
        {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL2)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            newsChannel =retrofit.create(NewsChannel.class);

        }
        return newsChannel;
    }

    public static Headlines getHeadlines()
    {
        if(headlines==null)
        {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL3)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            headlines = retrofit.create(Headlines.class);
        }

        return headlines;
    }


    public interface VergeHeadlines{

        @GET("top-headlines?sources=the-verge&apiKey=be8c5d2db3fe41e8935d30f6a4a667a0")
        Call<Articles> getData();
    }

    public interface NewsChannel{

        @GET("sources?apiKey=3f7681c903ec4ca59ebf6e58eaefa7ce")
        Call<NewsSources> getDrawerData();
    }

    public interface Headlines
    {
        @GET
        Call<Articles> getArticles(@Url String url);
    }

}
