package com.blogspot.sontx.bottle.utils;

import android.graphics.Bitmap;

import com.blogspot.sontx.bottle.model.service.SimpleCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import lombok.NonNull;

public final class TempUtils {
    private static final String TEMP_PREFIX = "temp";
    private static final String TEMP_SUFFIX = "3393";

    public static String saveBitmapAsync(@NonNull final Bitmap bitmap, final SimpleCallback<File> callback) {
        File tempFile;
        try {
            tempFile = File.createTempFile(TEMP_PREFIX, TEMP_SUFFIX);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        final File finalTempFile = tempFile;

        ThreadUtils.run(new Runnable() {
            @Override
            public void run() {
                OutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(finalTempFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    if (callback != null)
                        callback.onCallback(finalTempFile);
                } catch (IOException e) {
                    if (callback != null)
                        callback.onCallback(null);
                    e.printStackTrace();
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
        });

        return tempFile.getAbsolutePath();
    }

    private TempUtils() {
    }
}
