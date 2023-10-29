package Saborear.com.br.Database;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import Saborear.com.br.Callback.URLCallback;
import Saborear.com.br.Classes.classeMarket;

public class MarketDatabase extends AsyncTask<String, Void, String> {
    private URLCallback callback;
    public MarketDatabase(URLCallback callback) {
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

            StorageDatabase.market = new HashMap<>();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.contains("<") && line.contains(">")) continue;
                try {
                    String[] split = line.split("//");

                    classeMarket mkt = new classeMarket(split[1], split[2], split[3]);
                    if (!StorageDatabase.market.containsKey(split[0])) StorageDatabase.market.put(split[0], new ArrayList<>());
                    StorageDatabase.market.get(split[0]).add(mkt);
                } catch (Exception e) {
                    Log.e("SaborearDatabase", "Erro na leitura de market: " + line);
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
        Log.i("SaborearDatabase", "Market ["+StorageDatabase.market.size()+" carregamentos]");
        if (callback != null) {callback.onFinish(); }
    }
}
