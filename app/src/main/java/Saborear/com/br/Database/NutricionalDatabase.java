package Saborear.com.br.Database;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import Saborear.com.br.Callback.URLCallback;
import Saborear.com.br.Classes.classeNutricional;

public class NutricionalDatabase extends AsyncTask<String, Void, String> {
    private URLCallback callback;
    public NutricionalDatabase(URLCallback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            connection.connect();

            StorageDatabase.nutricional = new HashMap<>();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line; int count = 0;

            classeNutricional nutricional = new classeNutricional();
            while ((line = reader.readLine()) != null) {
                if(line.contains("<") && line.contains(">")) continue;
                try {
                    if(count == 0) nutricional = new classeNutricional();

                    switch(count) {
                        case 0: nutricional.setNome(line); break;
                        case 1: nutricional.setPeso(line); break;
                        case 2: nutricional.setCalorias(line); break;
                        case 3: nutricional.setSaturada(line); break;
                        case 4: nutricional.setTrans(line); break;
                        case 5: nutricional.setFibra(line); break;
                        case 6: nutricional.setAcucar(line); break;
                        case 7: nutricional.setPotassio(line); break;
                        case 8: nutricional.setFerro(line); break;
                        case 9: nutricional.setCalcio(line); break;
                        case 10: nutricional.setVitaminad(line); break;
                    }

                    if(count++ == 10) {
                        if(StorageDatabase.nomesingred.containsKey(nutricional.getNome())) nutricional.setCategoria(StorageDatabase.nomesingred.get(nutricional.getNome()));
                        StorageDatabase.nutricional.put(nutricional.getNome(), nutricional);
                        count = 0;
                    }

                } catch (Exception e) {
                    Log.e("SaborearDatabase", "Erro na leitura de nutricional: " + line);
                }
            }

            reader.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "finish";
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i("SaborearDatabase", "Nutricional ["+StorageDatabase.nutricional.size()+" carregamentos]");
        if (callback != null) {callback.onFinish(); }
    }
}