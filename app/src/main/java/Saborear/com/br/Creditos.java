package Saborear.com.br;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Utils.Utils;

public class Creditos extends AppCompatActivity {
    private ImageButton back, home, pesquisar, perfil, notificacao, voltar;
    private ScrollView scrollview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.creditos);
        initialize();

        scrollview = findViewById(R.id.creditos_layout);
        scroll();

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

    public void initialize() {
        ActivityTracker.addActivity(this);

        home = findViewById(R.id.creditos_btn_home);
        pesquisar = findViewById(R.id.creditos_btn_pesquisar);
        perfil = findViewById(R.id.creditos_btn_perfil);
        notificacao = findViewById(R.id.creditos_btn_notificacao);
        voltar = findViewById(R.id.creditos_btn_voltar);
        back = findViewById(R.id.creditos_btn_back);

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        Utils.blackMode(findViewById(R.id.creditos), ignore, inverted);
    }

    private void scroll() {
        scrollview.scrollTo(0, 800);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityTracker.delActivity(this);
    }
}