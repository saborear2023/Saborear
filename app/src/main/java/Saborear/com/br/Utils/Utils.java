package Saborear.com.br.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Database.StorageDatabase;

public class Utils {

    public static Bitmap drawableToBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable || drawable instanceof VectorDrawableCompat) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } else
            return null;
    }

    public static Bitmap getBitmap(ImageView view) {
        int width = view.getWidth();
        int height = view.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static Bitmap shapeBitmap(Bitmap bitmapOriginal) {
        int minSize = Math.min(bitmapOriginal.getWidth(), bitmapOriginal.getHeight());

        Bitmap bitmapRedondo = Bitmap.createBitmap(minSize, minSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapRedondo);
        Shader shader = new BitmapShader(bitmapOriginal, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        float radius = minSize / 2f;
        canvas.drawCircle(radius, radius, radius, paint);

        return bitmapRedondo;
    }

    public static Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }


    public static Bitmap shapeBitmap(Bitmap bitmapOriginal, int cornerRadius) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmapOriginal, 128, 128, true);

        Bitmap output = Bitmap.createBitmap(resizedBitmap.getWidth(), resizedBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = cornerRadius;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(resizedBitmap, rect, rect, paint);
        return output;
    }

    public static String diferencaData(String data) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Period periodo = Period.between(LocalDate.parse(data), LocalDate.now());
            if (periodo.getMonths() > 0)
                return "Há " + periodo.getMonths() + (periodo.getMonths() == 1 ? " mês" : " meses");
            else if (periodo.getDays() == 0)
                return "Hoje";
            else
                return "Há " + periodo.getDays() + (periodo.getDays() == 1 ? " dia" : " dias");
        }
        return "";
    }
    public static String converterTempo(int valor) {
        if (valor == 0) return "";

        int horas = valor / 60;
        int minutos = valor % 60;
        if (minutos == 0)
            return horas + "h";
        else if (horas > 0)
            return horas + "h" + minutos + "min";
        else
            return valor + "min";
    }

    public static String formatarTempo(long timestamp) {
        Date data = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        return sdf.format(data);
    }

    public static String limit(String texto, int size) {
        if (texto.length() > size) {
            return texto.substring(0, size) + "...";
        } else {
            return texto;
        }
    }

    public static boolean isNumber(String txt) {
        Pattern pattern = Pattern.compile("\\d{11}");
        Matcher matcher = pattern.matcher(txt);
        return matcher.matches();
    }

    public static boolean isEmail(String txt) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        Matcher matcher = pattern.matcher(txt);
        return matcher.matches();
    }

    public static String getNumber(String txt) {
        String ddd = txt.substring(0, 2);
        String parte1 = txt.substring(2, 7);
        String parte2 = txt.substring(7, 11);
        return "("+ddd+") "+parte1+"-"+parte2;
    }

    public static String formatar(String txt) {
        if(txt == null || txt.isEmpty()) return txt;
        return txt.substring(0, 1).toUpperCase() + txt.substring(1).toLowerCase();
    }

    public static void blackMode(ViewGroup layout, ArrayList<View> ignore, ArrayList<View> inverted) {
        if(!InternalDatabase.isDarkmode()) return;
        layout.setBackgroundColor(Color.parseColor("#000000"));

        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            if(ignore != null && ignore.contains(view)) continue;

            int color = Color.parseColor("#FFFFFF");
            if(inverted != null && inverted.contains(view)) color = Color.parseColor("#000000");

            if (view instanceof ImageView) {
                ImageView imageView = (ImageView) view;
                imageView.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            } else if(view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setTextColor(color);
            } else if(view instanceof ListView) {
                ListView listView = (ListView) view;
                listView.setDivider(new ColorDrawable(color));
                listView.setDividerHeight(intToDp(16));
            } else if(view instanceof ProgressBar) {
                ProgressBar progressBar = (ProgressBar) view;
                Drawable indeterminateDrawable = progressBar.getIndeterminateDrawable();
                Drawable drawablePersonalizado = indeterminateDrawable.getConstantState().newDrawable().mutate();
                drawablePersonalizado.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                progressBar.setIndeterminateDrawable(drawablePersonalizado);
            } else if(view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                checkBox.setTextColor(color);

                checkBox.setBackgroundColor(Color.parseColor("#00e2a5"));
            }
        }
    }

    public static int getHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static int getWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static void setup(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }
    }

    public static void desocultar(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_VISIBLE
            );
        }
    }

    public static void setupChat(Activity activity, View layout) {
        layout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            layout.getWindowVisibleDisplayFrame(r);
            int screenHeight = layout.getHeight();

            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) desocultar(activity);
            else setup(activity);
        });
    }

    public static int intToDp(int value) {
        return (int) (value * StorageDatabase.scale + 0.5f);
    }

}
