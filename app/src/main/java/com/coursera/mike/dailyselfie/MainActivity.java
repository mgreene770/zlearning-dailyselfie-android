package com.coursera.mike.dailyselfie;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
import android.app.AlarmManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.io.File;
import java.text.SimpleDateFormat;
import android.os.Environment;
import android.net.Uri;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    protected static final String IMAGE_RES_ID = "IRI";
    private static final String FILE_NAME = "SelfieData.txt";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final boolean USE_THUMBNAIL = true;
    private static final long ALARM_DELAY_TEN_SEC = 10 * 1000L;
    private static final long ALARM_DELAY_TWO_MIN = 2 * 60 * 1000L;

    private SelfieAdapter mAdapter;
    private String mCurrentPhotoPath;
    private AlarmManager mAlarmManager;
    private Intent mNotificationReceiverIntent;
    private PendingIntent mNotificationReceiverPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mNotificationReceiverIntent = new Intent(MainActivity.this,
                AlarmNotificationReceiver.class);

        // Create an PendingIntent that holds the NotificationReceiverIntent
        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, mNotificationReceiverIntent, 0);


        mAdapter = new SelfieAdapter(getApplicationContext());
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                SelfieRecord selfieRecord = (SelfieRecord) parent.getItemAtPosition(position);

                if (selfieRecord.isUseThumbnail()) {

                    Intent intent = new Intent(MainActivity.this,
                            SelfieViewActivity.class);

                    Bitmap b = selfieRecord.getSelfieBitmap();
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.PNG, 50, bs);
                    intent.putExtra("myImage", bs.toByteArray());
                    intent.putExtra(IMAGE_RES_ID, (int) id);

                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    File file = new File(selfieRecord.getFilePath());
                    intent.setDataAndType(Uri.fromFile(file), "image/png");
                    startActivity(intent);
                }
            }
        });

        startAlarm(ALARM_DELAY_TWO_MIN);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.new_photo) {
            dispatchTakePictureIntent();
        } else if (id == R.id.action_delete_all) {
            deleteAllSelfies();
        } else if (id == R.id.action_start_timer_tensec) {
            startAlarm(ALARM_DELAY_TEN_SEC);//
        } else if (id == R.id.action_start_timer_twomin) {
            startAlarm(ALARM_DELAY_TWO_MIN);
        } else if (id == R.id.action_stop_timer) {
            mAlarmManager.cancel(mNotificationReceiverPendingIntent);
            Toast.makeText(getApplicationContext(),
                    "Alarms Cancelled", Toast.LENGTH_LONG).show();
        }


        return super.onOptionsItemSelected(item);
    }

    private void startAlarm(long initialDelay) {
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + initialDelay,
                ALARM_DELAY_TWO_MIN,
                mNotificationReceiverPendingIntent);
        Toast.makeText(getApplicationContext(), "Repeating Alarm Started - 2m repeats",
                Toast.LENGTH_LONG).show();
    }

    private void deleteAllSelfies() {
        mAdapter.removeAllViews();

        Toast.makeText(getApplicationContext(), "All Selfies deleted.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdapter.getCount() == 0)
            loadItems();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveItems();
    }

    // Load stored ToDoItems
    private void loadItems() {

        BufferedReader reader = null;
        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(fis));

            String desc = null;
            Boolean useThumbnail;
            String filePath;
            String thumbPath;

            while (null != (desc = reader.readLine())) {
                filePath = reader.readLine();
                useThumbnail = Boolean.parseBoolean(reader.readLine());
                thumbPath = reader.readLine();

                SelfieRecord selfieRecord = new SelfieRecord(null, desc);
                selfieRecord.setFilePath(filePath);
                selfieRecord.setUseThumbnail(useThumbnail);
                selfieRecord.setThumbFilePath(thumbPath);
                //selfieRecord.loadThumbnail();
                selfieRecord.setSelfieBitmap(getThumbnail(thumbPath));
                mAdapter.add(selfieRecord);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Save ToDoItems to file
    private void saveItems() {
        PrintWriter writer = null;
        try {
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    fos)));

            for (int idx = 0; idx < mAdapter.getCount(); idx++) {

                SelfieRecord selfieRecord = (SelfieRecord) mAdapter.getItem(idx);
                boolean savedThumb = saveImageToInternalStorage(selfieRecord.getSelfieBitmap(), selfieRecord.getThumbFileName());
                String thumbnail = selfieRecord.getThumbFileName();
                //selfieRecord.saveThumbnail();
                writer.println(selfieRecord);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }

    public boolean saveImageToInternalStorage(Bitmap image, String thumbPath) {

        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(thumbPath, Context.MODE_PRIVATE);

            // Writing the bitmap to the output stream
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            return true;
        } catch (Exception e) {
            //Log.e("saveToInternalStorage()", e.getMessage());
            return false;
        }
    }

    public Bitmap getThumbnail(String filename) {

        Bitmap thumbnail = null;
        try {
            File filePath = getApplicationContext().getFileStreamPath(filename);
            FileInputStream fi = new FileInputStream(filePath);
            thumbnail = BitmapFactory.decodeStream(fi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return thumbnail;
    }

    private void dispatchTakePictureIntent() {
        if (USE_THUMBNAIL) {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }

    private Bitmap createThumbnail() {
        // Get the dimensions of the View
        int targetW = 100;
        int targetH = 100;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            boolean useThumbnail;

            Bitmap imageBitmap;
            if (data != null) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                useThumbnail = true;
            } else {
                galleryAddPic();
                imageBitmap = createThumbnail();
                useThumbnail = false;
            }

            SelfieRecord selfieRecord = new SelfieRecord(imageBitmap, new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
            selfieRecord.setFilePath(mCurrentPhotoPath);
            selfieRecord.setUseThumbnail(useThumbnail);
            mAdapter.add(selfieRecord);

            Toast.makeText(getApplicationContext(), "New Selfie Added",
                    Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        //File storageDir = Environment.getExternalStorageDirectory();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();

        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}
