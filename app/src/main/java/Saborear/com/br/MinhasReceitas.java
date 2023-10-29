package Saborear.com.br;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import Saborear.com.br.Adapter.MinhaView;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Manage.ManageReceita;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.Utils;

public class MinhasReceitas extends AppCompatActivity {

    private static Database db;

    private static Activity context;
    private static ManageReceita mg;

    private ImageButton home;
    private ImageButton pesquisar;
    private ImageButton perfil;
    private ImageButton notificacao;
    private ImageButton back;
    private ImageButton voltar;
    private static ImageButton sem;

    private static ListView listview;

    private static ImageView loading;

    private static ProgressBar carregando;
    private long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.minhasreceitas);
        initialize();

        loading.setOnClickListener(v -> {;
            loading.animate().rotationBy(360f).setDuration(1000).start();
            Handler handler = new Handler();
            Runnable code = () -> {
                CadastrarReceita.preload = false;
                Intent myIntent = new Intent(getApplicationContext(), CadastrarReceita.class);
                startActivityForResult(myIntent, 1);
                this.overridePendingTransition(0, 0);
            };
            handler.postDelayed(code, 1000);
        });

        back.setOnClickListener(v -> this.finish());

        voltar.setOnClickListener(v -> this.finish());

        perfil.setOnClickListener(v -> this.finish());

        pesquisar.setOnClickListener(v -> {
            ActivityTracker.finishExceptMain(this);
            Intent myIntent = new Intent(getApplicationContext(), Pesquisar.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
        });

        home.setOnClickListener(v -> {
            ActivityTracker.finishExceptMain(this);
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

    public static void block(Boolean block) {
        carregando.setIndeterminate(block);
        carregando.setVisibility(block ? View.VISIBLE : View.INVISIBLE);
    }

    public static void atualizar() {
        mg = new ManageReceita(StorageDatabase.receitas);

        int aux = listview.getFirstVisiblePosition();

        if(mg.getByOwner(InternalDatabase.getNome()).getReceitas().size() == 0) sem.setVisibility(View.VISIBLE);
        else sem.setVisibility(View.INVISIBLE);

        MinhaView adapterView = new MinhaView(context, mg.getByOwner(InternalDatabase.getNome()).getReceitas());
        listview.setAdapter(adapterView);
        listview.setSelection(aux);
    }

    public void initialize() {
        ActivityTracker.addActivity(this);
        db = new Database(this);
        context = MinhasReceitas.this;

        home = findViewById(R.id.minhasreceitas_btn_home);
        pesquisar = findViewById(R.id.minhasreceitas_btn_pesquisar);
        perfil = findViewById(R.id.minhasreceitas_btn_perfil);
        notificacao = findViewById(R.id.minhasreceitas_btn_notificacao);
        voltar = findViewById(R.id.minhasreceitas_btn_voltar);
        listview = findViewById(R.id.receita_listview_nutricional);
        loading = findViewById(R.id.minhasreceitas_btn_recarregar);
        carregando = findViewById(R.id.minhasreceitas_carregar);
        sem = findViewById(R.id.minhasreceitas_sem);
        back = findViewById(R.id.minhasreceitas_btn_back);

        atualizar();

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        inverted.add(listview);
        Utils.blackMode(findViewById(R.id.notificacaoadd_layout), ignore, inverted);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            atualizar();
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

