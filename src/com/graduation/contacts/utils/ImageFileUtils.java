
package com.graduation.contacts.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class ImageFileUtils {

    private final static String dir = Environment.getExternalStorageDirectory().getAbsolutePath();

    private final static String imageCachedir = dir + File.separator + "contactImages";

    private static String rootCacheDir = null;

    public ImageFileUtils(Context context) {
        rootCacheDir = context.getCacheDir().getAbsolutePath();
    }

    public String getStorePath() {
        boolean isMounted = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED;
        return isMounted ? imageCachedir : rootCacheDir;
    }

    public void saveBitmap(String photoId, Bitmap mBitmap) throws IOException {
        if (mBitmap == null) {
            return;
        }
        String path = getStorePath();
        File folderFile = new File(path);
        if (!folderFile.exists()) {
            folderFile.mkdir();
        }
        File file = new File(path + File.separator + photoId);
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        mBitmap.compress(CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
    }

    public Bitmap getBitmap(String photoId) {
        return BitmapFactory.decodeFile(getStorePath() + File.separator + photoId);
    }

    public boolean isFileExists(String photoId) {
        return new File(getStorePath() + File.separator + photoId).exists();
    }

    public long getFileSize(String photoId) {
        return new File(getStorePath() + File.separator + photoId).length();
    }

    public void deleteFile() {
        File dirFile = new File(getStorePath());
        if (!dirFile.exists()) {
            return;
        }
        if (dirFile.isDirectory()) {
            String[] children = dirFile.list();
            for (int i = 0; i < children.length; i++) {
                new File(dirFile, children[i]).delete();
            }
        }

        dirFile.delete();
    }

}
