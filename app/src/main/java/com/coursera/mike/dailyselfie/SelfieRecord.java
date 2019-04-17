package com.coursera.mike.dailyselfie;

import android.graphics.Bitmap;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import android.graphics.BitmapFactory;

/**
 * Created by Mike on 4/24/2015.
 */
public class SelfieRecord {
    private String mDesc;
    private Bitmap mSelfieBitmap;
    private String mFilePath;
    private boolean mUseThumbnail;
    private String mThumbFilePath;

    public static final String ITEM_SEP = System.getProperty("line.separator");

    public SelfieRecord(Bitmap selfieBitmap, String selfieDesc) {
        this.mSelfieBitmap = selfieBitmap;
        this.mDesc = selfieDesc;
    }

    public Bitmap getSelfieBitmap() {
        return mSelfieBitmap;
    }

    public void setSelfieBitmap(Bitmap selfieBitmap) {
        this.mSelfieBitmap = selfieBitmap;
    }

    public String getSelfieDesc() {
        return mDesc;
    }

    @Override
    public String toString() {
        return mDesc + ITEM_SEP + mFilePath + ITEM_SEP + mUseThumbnail + ITEM_SEP + mThumbFilePath;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        this.mFilePath = filePath;
    }

    public boolean isUseThumbnail() {
        return mUseThumbnail;
    }

    public void setUseThumbnail(boolean mUseThumbnail) {
        this.mUseThumbnail = mUseThumbnail;
    }

//    public void saveThumbnail() {
//
//        String filename = getThumbFileName();
//        FileOutputStream out = null;
//        try {
//            out = new FileOutputStream(filename);
//            mSelfieBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (out != null) {
//                    out.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void loadThumbnail() {
//       // Bitmap bitmap=null;
//
//        File f= new File(mThumbFilePath);
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        try {
//            mSelfieBitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    public String getThumbFileName() {
        if (mThumbFilePath == null) {
            mThumbFilePath = "PNG_" + mDesc + ".PNG";
        }

        return mThumbFilePath;
    }

    public void setThumbFilePath(String thumbFilePath) {
        this.mThumbFilePath = thumbFilePath;
    }
}
