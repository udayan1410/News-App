package com.codecenter1430gmail.jsonnew;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.codecenter1430gmail.jsonnew.DataAccessor.Article;
import com.codecenter1430gmail.jsonnew.DataAccessor.Articles;
import com.codecenter1430gmail.jsonnew.DataAccessor.NewsSources;
import com.codecenter1430gmail.jsonnew.DataAccessor.Source;
import com.codecenter1430gmail.jsonnew.NetworkAccessor.Api;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ListView l1;
    ListView navlist;
    public ArrayList<String> title;
    public ArrayList<String> author;
    public ArrayList<String> description;
    public ArrayList<String> url;
    public ArrayList<String> newsurl;
    String[] newsid,newsname;
    DrawerLayout mDrawerlayout;
    ActionBarDrawerToggle mToggle;
    String newsid2;
    ConnectivityManager connectivityManager;
    Articles list;
    List<Article> articlelist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectivityManager = (ConnectivityManager)getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);

        title = new ArrayList<>();
        author = new ArrayList<>();
        description = new ArrayList<>();
        url = new ArrayList<>();
        newsurl = new ArrayList<>();

        l1 = (ListView) findViewById(R.id.listview);
        navlist = (ListView) findViewById(R.id.drawerlist);
        mDrawerlayout = (DrawerLayout) findViewById(R.id.drawer);

        mToggle = new ActionBarDrawerToggle(this, mDrawerlayout, R.string.open, R.string.close);
        mDrawerlayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("The Verge");


        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            getdrawerdata();
            getlistdata();

            l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent intent = new Intent(MainActivity.this, DetailView.class);
                    intent.putExtra("url", newsurl.get(i));
                    intent.putExtra("title", getSupportActionBar().getTitle());
                    startActivity(intent);

                }
            });

            navlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mDrawerlayout.closeDrawers();
                    newsid2 = newsid[i];
                    String URL = "https://newsapi.org/v2/top-headlines?sources=" + newsid[i] + "&apiKey=be8c5d2db3fe41e8935d30f6a4a667a0";
                    getSupportActionBar().setTitle(newsname[i]);

                    final ProgressDialog progressDoalog;
                    progressDoalog = new ProgressDialog(MainActivity.this);
                    progressDoalog.setMax(100);
                    progressDoalog.setMessage("Loading...");
                    progressDoalog.setTitle("Fetching Latest News");
                    progressDoalog.setCancelable(false);
                    progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                    progressDoalog.show();

                    Call<Articles> articlesCall = Api.getHeadlines().getArticles(URL);
                    articlesCall.enqueue(new Callback<Articles>() {
                        @Override
                        public void onResponse(Call<Articles> call, Response<Articles> response) {
                            Articles articles = response.body();
                            List<Article> articlesList = articles.getArticles();

                            title.clear();
                            author.clear();
                            url.clear();
                            description.clear();
                            newsurl.clear();

                            for (int i = 0; i < articlesList.size(); i++) {
                                title.add(articlesList.get(i).getTitle());
                                author.add(articlesList.get(i).getAuthor());
                                url.add(articlesList.get(i).getUrlToImage());
                                description.add(articlesList.get(i).getDescription());
                                newsurl.add(articlesList.get(i).getUrl());
                            }
                            progressDoalog.dismiss();
                            CustomAdapter adapter = new CustomAdapter();
                            l1.setAdapter(adapter);
                        }

                        @Override
                        public void onFailure(Call<Articles> call, Throwable t) {

                        }
                    });
                }
            });
        }
        else
        {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
            builder.setMessage("No Internet Detected. Loading previos images").setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            android.support.v7.app.AlertDialog dialog = builder.create();
            dialog.show();

            l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(MainActivity.this,"No internet Connection",Toast.LENGTH_SHORT).show();
                }
            });
            navlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(MainActivity.this,"No internet Connection",Toast.LENGTH_SHORT).show();
                }
            });
            Toast.makeText(MainActivity.this,"Failed To refresh",Toast.LENGTH_SHORT).show();
            SharedPreferences preferences = getSharedPreferences("OfflineData",Context.MODE_PRIVATE);
            SharedPreferences preferences2 = getSharedPreferences("OfflineDrawer", Context.MODE_PRIVATE);
            for(int i=0;i<10;i++)
            {
                title.add(preferences.getString("title"+i,""));
                author.add(preferences.getString("author"+i,""));
                description.add(preferences.getString("description"+i,""));
                url.add(preferences.getString("url"+i,""));
            }

            newsname = new String[preferences2.getInt("newsnamesize",10)];

            for(int i=0;i<preferences2.getInt("newsnamesize",10);i++)
                newsname[i] = preferences2.getString("newsname"+i,"");

            CustomAdapter adapter = new CustomAdapter();
            l1.setAdapter(adapter);

            ArrayAdapter adapter2 = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,newsname);
            navlist.setAdapter(adapter2);
        }

    }

    public void getdrawerdata()
    {

        Call<NewsSources> newsSourcesCall = Api.getNewsChannels().getDrawerData();
        newsSourcesCall.enqueue(new Callback<NewsSources>() {
            @Override
            public void onResponse(Call<NewsSources> call, Response<NewsSources> response) {
                NewsSources newsSources = response.body();
                List<Source> sourcelist = newsSources.getSources();
                newsname = new String[sourcelist.size()];
                newsid = new String[sourcelist.size()];
                SharedPreferences preferences = getSharedPreferences("OfflineDrawer", Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("newsnamesize", sourcelist.size());
                for(int i=0;i<sourcelist.size();i++)
                {
                    newsname[i] = sourcelist.get(i).getName();
                    newsid[i] = sourcelist.get(i).getId();
                    editor.putString("newsname"+i,newsname[i]);

                }
                editor.commit();
                ArrayAdapter adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,newsname);
                navlist.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<NewsSources> call, Throwable t) {

            }
        });

    }

    public void getlistdata()
    {
        newsid2 = "the-verge";
        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setMax(100);
        progressDoalog.setMessage("Loading...");
        progressDoalog.setTitle("Fetching Latest News");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressDoalog.show();


        Call<Articles> articlesCall = Api.getVergeHeadlines().getData();
        articlesCall.enqueue(new Callback<Articles>() {

            @Override
            public void onResponse(Call<Articles> call, Response<Articles> response) {
                list = response.body();
                articlelist = list.getArticles();

                SharedPreferences preferences = getSharedPreferences("OfflineData", Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = preferences.edit();

                for (int i = 0; i < articlelist.size(); i++) {
                    title.add(articlelist.get(i).getTitle());
                    author.add(articlelist.get(i).getAuthor());
                    description.add(articlelist.get(i).getDescription());
                    url.add(articlelist.get(i).getUrlToImage());
                    newsurl.add(articlelist.get(i).getUrl());
                    editor.putString("title"+i,title.get(i));
                    editor.putString("author"+i,author.get(i));
                    editor.putString("description"+i,description.get(i));
                    editor.putString("url"+i,url.get(i));
                }
                progressDoalog.dismiss();
                editor.commit();
                CustomAdapter adapter = new CustomAdapter();
                l1.setAdapter(adapter);
            }
            @Override
            public void onFailure(Call<Articles> call, Throwable t) {

            }
        });
       }

    public class CustomAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return title.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 1;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.customlist,null);
            ImageView img = (ImageView)view.findViewById(R.id.finalimg);
            TextView tit = (TextView)view.findViewById(R.id.title);
            TextView des = (TextView)view.findViewById(R.id.customdescription);
            TextView aut = (TextView)view.findViewById(R.id.customauthor);

            tit.setText(title.get(i));
            des.setText(description.get(i));

            if("null".equalsIgnoreCase(author.get(i)))
                aut.setText("anonymous");
    else
            {
                aut.setText(author.get(i));
            }
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                Picasso.with(MainActivity.this).load(url.get(i)).into(img);
            }

            else {
                Picasso.with(MainActivity.this)
                        .load(url.get(i))
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(img);
            }
            return view;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
