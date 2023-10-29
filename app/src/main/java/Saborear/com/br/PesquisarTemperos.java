package Saborear.com.br;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import Saborear.com.br.Adapter.PesquisarTemperoView;
import Saborear.com.br.Classes.classeTempero;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Utils.Utils;

public class PesquisarTemperos extends AppCompatActivity {
    private Activity activity;
    private Database db;
    private ImageButton back, voltar, home, notificacao, pesquisar, perfil, cadastrar;
    private ListView listview;
    private ProgressBar loading;

    public static boolean atualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.pesquisartemperos);
        initialize();
        admin();

        cadastrar.setOnClickListener(v -> {
            cadastrar.animate().rotationBy(360f).setDuration(1000).start();
            Handler handler = new Handler();
            block(true, false);
            Runnable code = () -> {
                CadastrarTempero.preload = null;
                Intent myIntent = new Intent(getApplicationContext(), CadastrarTempero.class);
                startActivityForResult(myIntent, 1);
                this.overridePendingTransition(0, 0);
                block(false, false);
            };
            handler.postDelayed(code, 1000);
        });

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

    private void block(boolean block, boolean carregar) {
        cadastrar.setEnabled(!block);

        if(!carregar) return;
        listview.setVisibility(!block ? View.VISIBLE : View.INVISIBLE);
        loading.setIndeterminate(block);
        loading.setVisibility(block ? View.VISIBLE : View.INVISIBLE);
    }

    private void atualizar() {
        ArrayList<classeTempero> narr = new ArrayList<>();
        char letraAnterior = '\0';

        for (int i = 0; i < StorageDatabase.temperos.size(); i++) {
            classeTempero temperoAtual = StorageDatabase.temperos.get(i);
            char primeiraLetraAtual = temperoAtual.getNome().charAt(0);

            if (i == 0 || primeiraLetraAtual != letraAnterior) {
                narr.add(new classeTempero("-0", String.valueOf(primeiraLetraAtual), "", null, new ArrayList<>()));
            }
            letraAnterior = primeiraLetraAtual;
            narr.add(temperoAtual);
        }

        int aux = listview.getLastVisiblePosition();

        PesquisarTemperoView adapterView = new PesquisarTemperoView(this, narr);
        listview.setAdapter(adapterView);
        listview.setSelection(aux - 8 > 0 ? aux - 8 : 0);
    }

    public void initialize() {
        ActivityTracker.addActivity(this);
        db = new Database(this);
        voltar = findViewById(R.id.pesquisartemperos_btn_voltar);
        home = findViewById(R.id.pesquisartemperos_btn_home);
        pesquisar = findViewById(R.id.pesquisartemperos_btn_pesquisar);
        perfil = findViewById(R.id.pesquisartemperos_btn_perfil);
        notificacao = findViewById(R.id.pesquisartemperos_btn_notificacao);
        listview = findViewById(R.id.tempero_listview);
        cadastrar = findViewById(R.id.pesquisartemperos_btn_adicionar);
        loading = findViewById(R.id.pesquisartemperos_carregar);
        back = findViewById(R.id.pesquisartemperos_btn_back);

        atualizar = false;

        atualizar();

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        inverted.add(listview);
        Utils.blackMode(findViewById(R.id.pesquisartemperos_layout), ignore, inverted);
    }

    private void admin() {
        if(!StorageDatabase.isAdmin) return;

        cadastrar.setVisibility(View.VISIBLE);
    }

    protected void onResume() {
        super.onResume();
        atualizar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}