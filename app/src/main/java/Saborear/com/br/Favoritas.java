package Saborear.com.br;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import Saborear.com.br.Adapter.FavoritaView;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Manage.ManageReceita;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.Utils;

@SuppressLint("StaticFieldLeak")
public class Favoritas extends AppCompatActivity {

    private static Activity context;
    private static ManageReceita mg;
    private static Database db;
    private static ListView favoritadas;
    private static ProgressBar loading;
    private ImageButton voltar;
    private ImageButton home;
    private ImageButton pesquisar;
    private ImageButton notificacao;
    private ImageButton perfil;

    private ImageButton back;
    private static ImageButton sem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.favoritas);
        initialize();

        back.setOnClickListener(v -> this.finish());

        voltar.setOnClickListener(v -> finish());

        perfil.setOnClickListener(v -> finish());

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

    public static void block(boolean block) {
        loading.setIndeterminate(true);
        loading.setVisibility(block ? View.VISIBLE : View.INVISIBLE);
    }

    private void initialize() {
        ActivityTracker.addActivity(this);
        context = Favoritas.this;
        db = new Database(this);

        favoritadas = findViewById(R.id.receita_listview_nutricional);
        loading = findViewById(R.id.favoritas_carregar);
        voltar = findViewById(R.id.favoritas_btn_voltar);
        perfil = findViewById(R.id.favoritas_btn_perfil);
        home = findViewById(R.id.favoritas_btn_home);
        pesquisar = findViewById(R.id.favoritas_btn_pesquisar);
        notificacao = findViewById(R.id.favoritas_btn_notificacao);
        sem = findViewById(R.id.favoritas_sem);
        back = findViewById(R.id.favoritas_btn_back);

        atualizar();

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        inverted.add(favoritadas);
        Utils.blackMode(findViewById(R.id.favoritas_layout), ignore, inverted);
    }


    public static void atualizar() {
        mg = new ManageReceita(StorageDatabase.receitas);

        if(mg.getByLike().getReceitas().size() == 0) sem.setVisibility(View.VISIBLE);
        else sem.setVisibility(View.INVISIBLE);

        FavoritaView adapterView = new FavoritaView(context, mg.getByLike().getReceitas());
        favoritadas.setAdapter(adapterView);
    }

    protected void onResume() {
        super.onResume();
        atualizar();
        favoritadas.setSelection(favoritadas.getLastVisiblePosition());
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
