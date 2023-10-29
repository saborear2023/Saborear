package Saborear.com.br.Manage;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import Saborear.com.br.Callback.Callback;
import Saborear.com.br.Classes.classeDatabase;

public class ManageDatabase {

    public static Context context;
    private static final String url175 = "http://200.145.153.175/carlospinto/PHP/TCC/";
    private static final String url91 = "http://200.145.153.91/carlospinto/PHP/TCC/";
    private static Queue<classeDatabase> pilha;

    public static void start(Context cont) {
        context = cont;
        pilha = new LinkedList<>();
    }

    public static void add(String sql, Callback callback) {
        if(containsSQL(sql)) {
            classeDatabase classeDatabase = getBySQL(sql);
            classeDatabase.addCallback(callback);
            if(classeDatabase != null && !classeDatabase.getAtivado()) {
                remove(classeDatabase);
                pilha.add(classeDatabase);
                if(getAtivados() < 20) next();
            }
            return;
        }

        ArrayList<Callback> callbacks = new ArrayList<>(); callbacks.add(callback);
        pilha.add(new classeDatabase(sql, callbacks, false));

        if(getAtivados() < 20) next();
    }
    public static classeDatabase getBySQL(String sql) {
        for (classeDatabase classeDatabase : pilha) {
            if(classeDatabase.getSql().equals(sql)) return classeDatabase;
        }
        return null;
    }

    public static boolean containsSQL(String sql) {
        for (classeDatabase classeDatabase : pilha) {
            if(classeDatabase.getSql().equals(sql)) return true;
        }
        return false;
    }

    public static void remove(String sql) {
        for (classeDatabase classeDatabase : pilha) {
            if(classeDatabase.getSql().equals(sql)) pilha.remove(classeDatabase);
        }
    }

    public static void remove(classeDatabase classeDatabase) {
        pilha.remove(classeDatabase);
    }

    public static int getAtivados() {
        int count = 0;
        for (classeDatabase classeDatabase : pilha) {
            if(classeDatabase.getAtivado()) count++;
        }
        return count;
    }
    public static void next() {
        for (classeDatabase classeDatabase : pilha) {
            if(classeDatabase.getAtivado()) continue;
            execute(classeDatabase);
            break;
        }
    }
    public static void clear() { pilha = new LinkedList<>(); }
    public static void execute(classeDatabase classeDatabase) {
        if(classeDatabase.getAtivado()) return;
        classeDatabase.setAtivado(true);

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
                            ArrayList<Map<String, String>> data = new ArrayList<>();
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Map<String, String> dat = new HashMap<>();
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                                        String coluna = it.next();
                                        dat.put(coluna, jsonObject.getString(coluna));
                                    }
                                    data.add(dat);
                                }
                            } catch (JSONException e) {
                                Log.e("SaborearDatabase", "Erro: " + e.getMessage());
                            }
                            for (Callback callback : classeDatabase.getCallback()) {
                                callback.onValuesReceived(data);
                            }
                            remove(classeDatabase);
                            next();
                        },
                        error -> {
                            Log.e("SaborearDatabase", "Erro ao conectar com o banco [@ip]".replace("@ip", url));
                            Log.e("SaborearDatabase", "Reconectando...");

                            if(!containsSQL(classeDatabase.getSql())) return;
                            handler.postDelayed(this, 3000);
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("sql", classeDatabase.getSql());
                        return params;
                    }
                };
                Volley.newRequestQueue(context).add(stringRequest);
            }
        };
        handler.postDelayed(runnable, 0);
    }

    public static int size() { return pilha.size(); }
    public static boolean allow() { return pilha.size() > 10 ? false : true; }
}
