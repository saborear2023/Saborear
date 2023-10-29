package Saborear.com.br.Database;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import Saborear.com.br.Callback.Callback;
import Saborear.com.br.Manage.ManageDatabase;

public class Database {

    /*
        //SCRIPT SEM RETORNO
        Database db = new Database(this);
        String sql = "insert into usuario(nome, email, telefone, senha) values ('---', '---', 0, '---');";
        db.sendScript(sql);

        //SCRIPT COM RETORNO
        Database db = new Database(this);
        String sql = "select * from usuario where email = '---';";
        db.requestScript(sql, data -> {
            for (Map<String, String> item : data) {
                String senha = item.get("senha");
                Log.i("SaborearDatabase", senha);
            }
        });
     */

    private final String url175 = "http://200.145.153.175/carlospinto/PHP/TCC/";
    private final String url91 = "http://200.145.153.91/carlospinto/PHP/TCC/";
    private final Context context;

    public Database(Context context) { this.context = context; }

    public void sendScript(String sql) {
        Handler handler = new Handler();

        final int[] position = {0};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String url;

                if (position[0] == 0) {
                    url = url175 + "script.php";
                    position[0] = 1;
                } else {
                    url = url91 + "script.php";
                    position[0] = 0;
                }

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        response -> {
                        },
                        error -> {
                            Log.e("SaborearDatabase", "Erro ao conectar com o banco [@ip]".replace("@ip", url));
                            Log.e("SaborearDatabase", "Reconectando...");
                            handler.postDelayed(this, 3000);
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("sql", sql);
                        return params;
                    }
                };
                Volley.newRequestQueue(context).add(stringRequest);
            }
        };
        handler.postDelayed(runnable, 0);
    }

    public void requestScript(String sql, Callback callback) {
        ManageDatabase.add(sql, callback);
    }
}