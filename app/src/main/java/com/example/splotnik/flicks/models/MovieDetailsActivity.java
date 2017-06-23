package com.example.splotnik.flicks.models;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.splotnik.flicks.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.example.splotnik.flicks.MovieListActivity.API_BASE_URL;

public class MovieDetailsActivity extends YouTubeBaseActivity {

    //the movie to display
    Movie movie;

    // the view objects
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvOverview)TextView tvOverview;
    @BindView(R.id.rbVoteAverage) RatingBar rbVoteAverage;
    int id;
    String videoKey="";
    AsyncHttpClient client;
    Button button;
    String url = API_BASE_URL + "/movie/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        client = new AsyncHttpClient();
        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // set the title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        button = (Button) findViewById(R.id.btVideo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Info","Goes here");
                onWatchVideo();
            }
        });

        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);
    }

    // handles when the back arrow is pressed to go back to home page
    public void onWatchVideo() {
        Log.i("Info","Goes here");
        url = API_BASE_URL + "/movie/";
        url+=Integer.toString(movie.getId())+"/videos"+"?api_key="+getString(R.string.api_key);
        //url = String.format("http://api.themoviedb.org/3/movie/%s/videos?api_key=38bb7f77c187b58a76260fbf17912adf", id);
        Log.i("Info",url);

        client.get(url, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray videoJsonResults = null; // gets the results array
                try {
                    Log.i("info", "onSuccess: ");
                    videoJsonResults= response.getJSONArray("results");
                    JSONObject result = videoJsonResults.getJSONObject(0); // get the first json object for video
                    // STORE the KEY for later use
                    videoKey = result.getString("key");
                    //Toast.makeText(getApplicationContext(), "second"+videoKey,Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getBaseContext(), MovieVideo.class);
                    //Toast.makeText(getApplicationContext(),"first"+videoKey,Toast.LENGTH_LONG).show();
                    i.putExtra("YOUTUBE_ID",videoKey);
                    startActivity(i);
                } catch(JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Null",Toast.LENGTH_LONG).show();

                }  }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(),"Wrong error",Toast.LENGTH_LONG).show();
            }
        });

    }
}
