package Saborear.com.br;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Utils.Utils;

public class Perguntas extends AppCompatActivity {
    private ImageButton back, voltar, home, notificacao, pesquisar, perfil;
    private TextView texto1, texto2, texto3, texto4, texto5, texto6, texto7, texto8, texto9, texto10;
    private Button pergunta1, pergunta2, pergunta3, pergunta4, pergunta5, pergunta6, pergunta7, pergunta8, pergunta9, pergunta10;

    private ArrayList<View> buttons, textos;
    private boolean isExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.perguntas);
        initialize();

        buttons.get(0).setOnClickListener(v -> toggle(0));
        buttons.get(1).setOnClickListener(v -> toggle(1));
        buttons.get(2).setOnClickListener(v -> toggle(2));
        buttons.get(3).setOnClickListener(v -> toggle(3));
        buttons.get(4).setOnClickListener(v -> toggle(4));
        buttons.get(5).setOnClickListener(v -> toggle(5));
        buttons.get(6).setOnClickListener(v -> toggle(6));
        buttons.get(7).setOnClickListener(v -> toggle(7));
        buttons.get(8).setOnClickListener(v -> toggle(8));
        buttons.get(9).setOnClickListener(v -> toggle(9));

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

    private void toggle(int pos) {
        if(textos.get(pos).getVisibility() == View.VISIBLE)
            textos.get(pos).setVisibility(View.GONE);
        else {
            textos.get(pos).setVisibility(View.VISIBLE);
            for (View texto : textos)
                if(texto != textos.get(pos))
                    texto.setVisibility(View.GONE);
        }
    }

    public void initialize() {
        voltar = findViewById(R.id.perguntas_btn_voltar);
        home = findViewById(R.id.perguntas_btn_home);
        pesquisar = findViewById(R.id.perguntas_btn_pesquisar);
        perfil = findViewById(R.id.perguntas_btn_perfil);
        notificacao = findViewById(R.id.perguntas_btn_notificacao);
        back = findViewById(R.id.perguntas_btn_back);
        pergunta1= findViewById(R.id.pergunta1);
        pergunta2= findViewById(R.id.pergunta2);
        pergunta3= findViewById(R.id.pergunta3);
        pergunta4= findViewById(R.id.pergunta4);
        pergunta5= findViewById(R.id.pergunta5);
        pergunta6= findViewById(R.id.pergunta6);
        pergunta7= findViewById(R.id.pergunta7);
        pergunta8= findViewById(R.id.pergunta8);
        pergunta9= findViewById(R.id.pergunta9);
        pergunta10= findViewById(R.id.pergunta10);
        texto1 = findViewById(R.id.texto1);
        texto2 = findViewById(R.id.texto2);
        texto3 = findViewById(R.id.texto3);
        texto4 = findViewById(R.id.texto4);
        texto5 = findViewById(R.id.texto5);
        texto6 = findViewById(R.id.texto6);
        texto7 = findViewById(R.id.texto7);
        texto8 = findViewById(R.id.texto8);
        texto9 = findViewById(R.id.texto9);
        texto10 = findViewById(R.id.texto10);


        textos = new ArrayList<>();
        textos.add(texto1); textos.add(texto2);
        textos.add(texto3); textos.add(texto4);
        textos.add(texto5); textos.add(texto6);
        textos.add(texto7); textos.add(texto8);
        textos.add(texto9); textos.add(texto10);

        buttons = new ArrayList<>();
        buttons.add(pergunta1); buttons.add(pergunta2);
        buttons.add(pergunta3); buttons.add(pergunta4);
        buttons.add(pergunta5); buttons.add(pergunta6);
        buttons.add(pergunta7); buttons.add(pergunta8);
        buttons.add(pergunta9); buttons.add(pergunta10);

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        Utils.blackMode(findViewById(R.id.perguntas_layout), buttons, inverted);
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