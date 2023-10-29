package Saborear.com.br;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import Saborear.com.br.Adapter.MarketView;
import Saborear.com.br.Classes.classeIngrediente;
import Saborear.com.br.Classes.classeMarket;
import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Utils.Utils;

public class Market extends AppCompatActivity {

    private Database db;
    private Activity activity;
    private ImageButton voltar, back, home, pesquisar, perfil, notificacao;
    private TextView custo;
    private ListView listview;

    public static classeReceita receita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.market);
        initialize();

        back.setOnClickListener(v -> this.finish());

        voltar.setOnClickListener(v -> this.finish());

        perfil.setOnClickListener(v -> {
            ActivityTracker.finishExceptMain(this);
            Intent myIntent = new Intent(getApplicationContext(), Perfil.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
        });

        pesquisar.setOnClickListener(v -> {
            ActivityTracker.finishExceptMain(this);
            Intent myIntent = new Intent(getApplicationContext(), Pesquisar.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
        });

        home.setOnClickListener(v -> {
            ActivityTracker.finishExceptMain(this);
            this.overridePendingTransition(0, 0);
            finish();
        });

        notificacao.setOnClickListener(v -> {
            ActivityTracker.finishExceptMain(this);
            Intent myIntent = new Intent(getApplicationContext(), Notificacao.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
        });
    }

    public void initialize() {
        ActivityTracker.addActivity(this);
        db = new Database(this);
        activity = this;

        perfil = findViewById(R.id.market_btn_perfil);
        home = findViewById(R.id.market_btn_home);
        pesquisar = findViewById(R.id.market_btn_pesquisar);
        notificacao = findViewById(R.id.market_btn_notificacao);
        back = findViewById(R.id.market_btn_back);
        voltar = findViewById(R.id.market_btn_voltar);
        custo = findViewById(R.id.market_insert_custo);
        listview = findViewById(R.id.market_listview);

        ArrayList<classeIngrediente> ingredientes = new ArrayList<>();
        ArrayList<ArrayList<classeMarket>> markets = new ArrayList<>();

        double custototal = 0;
        for (classeIngrediente ingrediente : receita.getIngredientes()) {
            if(!StorageDatabase.market.containsKey(ingrediente.getNome())) continue;

            ArrayList<classeMarket> arr = (ArrayList<classeMarket>) StorageDatabase.market.get(ingrediente.getNome()).clone();

            Collections.sort(arr, Comparator.comparing(item -> item.getCusto() == 0 ? 999999 : item.getCusto()));

            if (arr.size() > 3) arr = new ArrayList<>(arr.subList(0, 3));

            markets.add(arr);
            ingredientes.add(ingrediente);

            double custo = -1;
            for (int i = 0; i < StorageDatabase.market.get(ingrediente.getNome()).size(); i++) {
                classeMarket market = StorageDatabase.market.get(ingrediente.getNome()).get(i);
                try {
                    double val = market.getCusto();
                    if(val == 0) continue;


                    if (custo == -1) custo = val;
                    else custo = Math.min(custo, val);
                } catch (Exception e) { Log.i("SaborearDatabase", "Erro ao processar market: "+e.getMessage()); }
            }
            if(custo > 0)  custototal += custo;
        }

        DecimalFormat df = new DecimalFormat("0.00");
        if(custototal > 0) custo.setText("Custo econômico R$ "+df.format(custototal).replace(".", ","));
        else custo.setText("Sem dados suficientes.");

        MarketView adapter = new MarketView(this, ingredientes, markets);
        listview.setAdapter(adapter);

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        ignore.add(custo); inverted.add(listview);
        Utils.blackMode(findViewById(R.id.market_layout), ignore, inverted);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityTracker.delActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
