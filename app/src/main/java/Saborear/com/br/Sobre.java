package Saborear.com.br;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Utils.Utils;

public class Sobre extends AppCompatActivity {
    private ImageButton back, voltar, home, notificacao, pesquisar, perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.sobre);
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
        voltar = findViewById(R.id.sobre_btn_voltar);
        home = findViewById(R.id.sobre_btn_home);
        pesquisar = findViewById(R.id.sobre_btn_pesquisar);
        perfil = findViewById(R.id.sobre_btn_perfil);
        notificacao = findViewById(R.id.sobre_btn_notificacao);
        back = findViewById(R.id.sobre_btn_back);

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        ignore.add(findViewById(R.id.sobre_imagem)); ignore.add(findViewById(R.id.sobre_background));
        inverted.add(findViewById(R.id.sobre_campo));
        Utils.blackMode(findViewById(R.id.sobre_layout), ignore, inverted);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}