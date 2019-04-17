package com.coursera.mike.dailyselfie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
/**
 * Created by Mike on 4/24/2015.
 */
public class SelfieViewActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the Intent used to start this Activity
        Intent intent = getIntent();

        // Make a new ImageView
        ImageView imageView = new ImageView(getApplicationContext());

        // Get the ID of the image to display and set it as the image for this ImageView
        int imageId = intent.getIntExtra(MainActivity.IMAGE_RES_ID, 0);

        //imageView.setImageResource(imageId);
        //ImageView image = new ImageView(this);
        Bitmap b = BitmapFactory.decodeByteArray(
                getIntent().getByteArrayExtra("myImage"), 0, getIntent().getByteArrayExtra("myImage").length);
        //image.setImageBitmap(b);
        imageView.setImageBitmap(b);
        setContentView(imageView);
    }
}
