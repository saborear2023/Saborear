package Saborear.com.br.Database;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

import Saborear.com.br.R;
import Saborear.com.br.Utils.Utils;

public class ImageDatabase {
    private static Bitmap perfil;

    private static Database db;

    private static Activity activity;

    public static void start(Activity act) {
        activity = act;
        db = new Database(act);

    }

    public static Bitmap getPerfil() {
        if(perfil!= null)
            return perfil;
        else
            return Utils.drawableToBitmap(activity, R.drawable.saborear_icon_perfil);
    }
    public static void setPerfil(Bitmap bitmap) {
        perfil = bitmap;
    }

    public static Bitmap decode(String base64String) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            return null; //Utils.drawableToBitmap(activity, R.drawable.saborear_sem_foto_02);
        }
    }

    public static String encode(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static String encodeResize(Bitmap bitmap) {
        byte[] byteArray = ImageDatabase.resize(bitmap, 10, 250, 30000);
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    public static byte[] resize(Bitmap bitmap, int minQuality, int minWidth, int maxLength) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();

        int quality = 100, newWidth = 1000, length;
        while(true) {
            Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, (originalHeight * newWidth) / originalWidth, false);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            newBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            String base64String = Base64.encodeToString(bytes, Base64.DEFAULT);

            length = base64String.length();
            if(length < maxLength)
                return bytes;

            if(quality > minQuality)
                quality -= 10;
            else if(newWidth > minWidth)
                newWidth /= 2;
        }
    }
}
