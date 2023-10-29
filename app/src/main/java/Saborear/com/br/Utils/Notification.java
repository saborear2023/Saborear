package Saborear.com.br.Utils;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Map;
import java.util.Objects;

import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.R;
public class Notification {

    private static Database db;

    private static final String CHANNEL_ID = "saborear_channel_id";
    private static final String CHANNEL_NAME = "Saborear";
    private static final String CHANNEL_DESCRIPTION = "Saborear";
    public static void send(Context context) {
        db = new Database(context);
        String sql = "select * from notificacao where id_notificacao not in (select id_notificacao from usuario_notificacao where email = '@email') and titulo != 'Um novo comentário!' order by random() limit 1;";
        sql = InternalDatabase.convert(sql);
        try {
            db.requestScript(sql, data -> {
                if (data == null || data.size() == 0) return;
                for (Map<String, String> item : data) {
                    String newsql = "insert into usuario_notificacao(email, id_notificacao) values('@email', '@id')".replace("@id", Objects.requireNonNull(item.get("id_notificacao")));
                    newsql = InternalDatabase.convert(newsql);
                    db.sendScript(newsql);
                    showNotification(context, item.get("titulo"), item.get("mensagem"));
                }
            });
        } catch (Exception e) {
            Log.i("SaborearDatabase", "Erro ao inserir notificação: " + e.getMessage());
        }
    }
    @SuppressLint("NewApi")
    private static void showNotification(Context context, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(CHANNEL_DESCRIPTION);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.saborear_icon_saborear)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);

        notificationManager.notify(0, builder.build());
    }
}