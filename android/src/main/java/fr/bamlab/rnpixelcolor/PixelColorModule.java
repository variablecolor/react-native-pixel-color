package fr.bamlab.rnpixelcolor;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.facebook.react.BuildConfig;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.io.IOException;

public class PixelColorModule extends ReactContextBaseJavaModule {
    private final Context context;

    public PixelColorModule(@NonNull ReactApplicationContext context){
        super(context);

        this.context = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "RNPixelColor";
    }

    private ContentResolver getContentResolver() {
        return this.context.getContentResolver();
    }
    @ReactMethod
    public void getHex(@NonNull String uri, @NonNull ReadableMap options, @NonNull Callback callback) {
        String hex;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                hex = this.extract(
                        Uri.parse(uri),
                        options.getInt("x"),
                        options.getInt("y")
                );
            } else {
                hex = this.legacyExtract(
                        Uri.parse(uri),
                        options.getInt("x"),
                        options.getInt("y")
                );
            }


            callback.invoke(null, hex);

        } catch (IOException e) {
            e.printStackTrace();

            callback.invoke(e.getMessage(), "");
        }
    }

    @Deprecated
    public String legacyExtract(Uri pictureURI, int x, int y) throws IOException {
        Bitmap bitmap = null;
        try {

            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pictureURI);

            int rgb = bitmap.getPixel(x, y);
            return "#" + Integer.toHexString(rgb).substring(2).toUpperCase();
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.Q)
    public String extract(Uri pictureURI, int x, int y) throws IOException {
        Bitmap bitmap = null;
        try {

            ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), pictureURI);
            bitmap = ImageDecoder.decodeBitmap(source);

            int rgb = bitmap.getPixel(x, y);
            return "#" + Integer.toHexString(rgb).substring(2).toUpperCase();


        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }
}
