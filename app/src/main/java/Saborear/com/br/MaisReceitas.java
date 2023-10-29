package Saborear.com.br;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import Saborear.com.br.Adapter.MinireceitaGridView;
import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Manage.ManageReceita;
import Saborear.com.br.Utils.Utils;

public class MaisReceitas extends AppCompatActivity {

    private static Activity context;
    private ImageButton voltar;
    private ImageButton home;
    private ImageButton pesquisar;
    private ImageButton notificacao;
    private ImageButton perfil;
    private ImageButton back;
    private static GridView gridView;

    public static ArrayList<classeReceita> receitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.maisreceitas);
        initialize();

        back.setOnClickListener(v -> finish());

        voltar.setOnClickListener(v -> finish());

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

    private void initialize() {
        ActivityTracker.addActivity(this);
        context = MaisReceitas.this;

        voltar = findViewById(R.id.maisreceitas_btn_voltar);
        perfil = findViewById(R.id.maisreceitas_btn_perfil);
        home = findViewById(R.id.maisreceitas_btn_home);
        pesquisar = findViewById(R.id.maisreceitas_btn_pesquisar);
        notificacao = findViewById(R.id.maisreceitas_btn_notificacao);
        back = findViewById(R.id.maisreceitas_btn_back);
        gridView = findViewById(R.id.maisreceitas_grid);

        atualizar();

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        Utils.blackMode(findViewById(R.id.maisreceitas_layout), ignore, inverted);
    }


    public static void atualizar() {
        ManageReceita mg = new ManageReceita(StorageDatabase.receitas);
        ArrayList<classeReceita> nreceitas = new ArrayList<>();

        for (classeReceita classeReceita : receitas) {
            for (classeReceita receita : mg.getReceitas()) {
                if(!classeReceita.getIdreceita().equals(receita.getIdreceita())) continue;
                nreceitas.add(receita);
                break;
            }
        }

//        if(mg.getByLike().getReceitas().size() == 0) sem.setVisibility(View.VISIBLE);
//        else sem.setVisibility(View.INVISIBLE);

        MinireceitaGridView adapterView = new MinireceitaGridView(context, nreceitas);
        gridView.setAdapter(adapterView);
    }

    protected void onResume() {
        super.onResume();
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

