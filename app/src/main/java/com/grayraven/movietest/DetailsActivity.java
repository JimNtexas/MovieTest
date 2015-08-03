package com.grayraven.movietest;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends ActionBarActivity {
    private TextView titleTextView;
    private ImageView imageView;
    private CheckBox chkFavorite;
    private static final String TAG = "MovietestDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        chkFavorite = (CheckBox) findViewById(R.id.check_favorite);
        addListenerOnFavoriteCheck();

        String title = getIntent().getStringExtra("title");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        titleTextView = (TextView) findViewById(R.id.title);
        imageView = (ImageView) findViewById(R.id.grid_item_image);
        titleTextView.setText(Html.fromHtml(title));

        Picasso.with(this).load(imageUrl).into(imageView);
    }

    private void addListenerOnFavoriteCheck() {
        chkFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "STAR CLICK");
                if (chkFavorite.isChecked()) {
                    Log.i(TAG, "star checked");
                } else {
                    Log.i(TAG, "star not checked");
                }

            }
        });


        ;

    }
}