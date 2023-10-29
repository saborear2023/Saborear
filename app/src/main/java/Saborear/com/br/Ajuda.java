package Saborear.com.br;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.ImageDatabase;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Utils.Utils;

public class Ajuda extends AppCompatActivity {

    private Database db;
    private ImageView foto;
    private ImageButton back, sobre, contato, perguntas, termos, voltar, home, pesquisar, perfil, notificacao;
    private TextView nome, telefone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.ajuda);
        initialize();

        Utils.setupChat(this, findViewById(R.id.ajuda_layout));

        sobre.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Sobre.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
        });

        contato.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Contato.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
        });

        perguntas.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Perguntas.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
        });

        termos.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Termos.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
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

        foto = findViewById(R.id.ajuda_imagem_perfil);
        nome = findViewById(R.id.ajuda_txt_nome);
        telefone = findViewById(R.id.ajuda_txt_telefone);
        sobre = findViewById(R.id.ajuda_btn_sobre);
        termos = findViewById(R.id.ajuda_btn_termos);
        perguntas = findViewById(R.id.ajuda_btn_perguntas);
        voltar = findViewById(R.id.ajuda_btn_voltar);
        home = findViewById(R.id.ajuda_btn_home);
        pesquisar = findViewById(R.id.ajuda_btn_pesquisar);
        perfil = findViewById(R.id.ajuda_btn_perfil);
        notificacao = findViewById(R.id.ajuda_btn_notificacao);
        contato = findViewById(R.id.ajuda_btn_contato);
        back = findViewById(R.id.ajuda_btn_back);

        foto.setImageBitmap(ImageDatabase.getPerfil());
        nome.setText(InternalDatabase.getNome());
        telefone.setText(Utils.getNumber(InternalDatabase.getTelefone()));

        ArrayList<View> ignore = new ArrayList<>();
        ignore.add(foto);
        Utils.blackMode(findViewById(R.id.ajuda_layout), ignore, null);
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
