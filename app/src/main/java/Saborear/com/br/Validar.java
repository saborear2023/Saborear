package Saborear.com.br;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import Saborear.com.br.Adapter.ValidarView;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Manage.ManageReceita;
import Saborear.com.br.Utils.Utils;

public class Validar extends AppCompatActivity {

    private static Activity activity;
    private Database db;
    private static ManageReceita mg;
    private ImageButton back, voltar, home, pesquisar, perfil, notificacao;
    private static ImageView sem;
    private static ListView listview;
    private static ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.validar);
        initialize();

        back.setOnClickListener(v -> this.finish());

        voltar.setOnClickListener(v -> this.finish());

        home.setOnClickListener(v -> {
            ActivityTracker.finishExceptMain(this);
            this.overridePendingTransition(0, 0);
            finish();
        });

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

        notificacao.setOnClickListener(v -> {
            ActivityTracker.finishExceptMain(this);
            Intent myIntent = new Intent(getApplicationContext(), Notificacao.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
        });
    }

    public static void block(boolean block) {
        loading.setIndeterminate(block);
        loading.setVisibility(block ? View.VISIBLE : View.INVISIBLE);
    }

    public void initialize() {
        ActivityTracker.addActivity(this);
        activity = this;
        db = new Database(this);

        voltar = findViewById(R.id.validar_btn_voltar);
        home = findViewById(R.id.validar_btn_home);
        pesquisar = findViewById(R.id.validar_btn_pesquisar);
        perfil = findViewById(R.id.validar_btn_perfil);
        notificacao = findViewById(R.id.validar_btn_notificacao);
        listview = findViewById(R.id.validar_listview);
        loading = findViewById(R.id.validar_loading);
        back = findViewById(R.id.validar_btn_back);
        sem = findViewById(R.id.validar_sem);

        atualizar();

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        inverted.add(listview);
        Utils.blackMode(findViewById(R.id.validar_layout), ignore, inverted);
    }

    public static void atualizar() {
        mg = new ManageReceita(StorageDatabase.receitas);

        if(mg.getReceitasValidar().size() == 0) sem.setVisibility(View.VISIBLE);
        else sem.setVisibility(View.INVISIBLE);

        ValidarView adapterView = new ValidarView(activity, mg.getReceitasValidar());
        listview.setAdapter(adapterView);
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

