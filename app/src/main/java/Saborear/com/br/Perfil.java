package Saborear.com.br;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.ImageDatabase;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Utils.Utils;

@SuppressWarnings("deprecation")
public class Perfil extends AppCompatActivity {

    private static final int ATUALIZAR = 1;
    private static ViewGroup viewgroup;

    private Database db;
    private ImageView box;
    private static ImageView foto;
    private ImageButton creditos, back, editar, favoritas, ajuda, convidar, sair, voltar, home, pesquisar, perfil, notificacao, admin, minhas;
    private static Button confirmar;
    private static Button cancelar;
    private TextView nome;
    private TextView telefone;
    private static TextView deslogar;
    private View layout;
    private boolean up = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.perfil);
        initialize();

        back.setOnClickListener(v -> this.finish());

        minhas.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), MinhasReceitas.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
        });

        admin.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Admin.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
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

        favoritas.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Favoritas.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
        });


        ajuda.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Ajuda.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
        });

        creditos.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Creditos.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
        });

        convidar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Procurando por inspiração na cozinha? O Saborear é o seu guia culinário definitivo! Explore uma infinidade de pratos deliciosos, desde clássicos reconfortantes até criações ousadas. Eleve sua experiência culinária agora mesmo baixando o aplicativo em "+StorageDatabase.URL);
            startActivity(Intent.createChooser(intent, "Compartilhar via"));
        });

        editar.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), EditarPerfil.class);
            startActivityForResult(myIntent, ATUALIZAR);
            this.overridePendingTransition(0, 0);
        });

        sair.setOnClickListener(v -> {
            if (!up)
                up();
            else
                down();
        });

        confirmar.setOnClickListener(v -> {
            if (!up) return;
            InternalDatabase.clear(getApplicationContext());
            Toast.makeText(getApplicationContext(), "Você saiu da sua conta", Toast.LENGTH_SHORT).show();

            ActivityTracker.finish(this);
            Intent myIntent = new Intent(getApplicationContext(), Login.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
        });

        cancelar.setOnClickListener(v -> {
            if (!up) return;
            down();
        });

        layout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                if (box.getY() > event.getY()) {
                    down();
                    block(false);
                }
            return false;
        });
    }

    void block(Boolean block) {
        View[] views = new View[]{voltar, editar, favoritas, notificacao, ajuda, convidar, sair, home, pesquisar, perfil, notificacao};
        for (View view : views) {
            view.setEnabled(!block);
        }
    }

    public void initialize() {
        ActivityTracker.addActivity(this);
        db = new Database(this);

        foto = findViewById(R.id.perfil_imagem_perfil);
        nome = findViewById(R.id.perfil_txt_nome);
        telefone = findViewById(R.id.perfil_txt_telefone);
        sair = findViewById(R.id.perfil_btn_sair);
        editar = findViewById(R.id.perfil_btn_editar);
        favoritas = findViewById(R.id.perfil_btn_favoritas);
        notificacao = findViewById(R.id.perfil_btn_notificacao);
        ajuda = findViewById(R.id.perfil_btn_ajuda);
        convidar = findViewById(R.id.perfil_btn_convidar);
        voltar = findViewById(R.id.perfil_btn_voltar);
        home = findViewById(R.id.perfil_btn_home);
        pesquisar = findViewById(R.id.perfil_btn_pesquisar);
        perfil = findViewById(R.id.perfil_btn_perfil);
        confirmar = findViewById(R.id.perfil_btn_confirmar);
        cancelar = findViewById(R.id.perfil_btn_cancelar);
        deslogar = findViewById(R.id.perfil_txt_sair);
        minhas = findViewById(R.id.perfil_btn_minhas);
        admin = findViewById(R.id.perfil_btn_admin);
        box = findViewById(R.id.perfil_box_sair);
        layout = findViewById(R.id.perfil_layout);
        viewgroup = findViewById(R.id.perfil_layout);
        creditos = findViewById(R.id.perfil_btn_creditos);
        back = findViewById(R.id.perfil_btn_back);
        admin.setVisibility(View.INVISIBLE);

        admin();

        foto.setImageBitmap(ImageDatabase.getPerfil());
        nome.setText(InternalDatabase.getNome());
        telefone.setText(Utils.getNumber(InternalDatabase.getTelefone()));

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        ignore.add(foto); ignore.add(confirmar); ignore.add(admin);
        inverted.add(deslogar); inverted.add(cancelar); ignore.add(sair);
        Utils.blackMode(findViewById(R.id.perfil_layout), ignore, inverted);
    }

    private void admin() {
        if (!StorageDatabase.isAdmin) return;
        admin.setVisibility(View.VISIBLE); admin.setEnabled(true);
    }

    void up() {
        View[] v = new View[]{box, deslogar, confirmar, cancelar};
        for(int i = 0; i<v.length; i++) {
            v[i].setVisibility(View.VISIBLE);
            v[i].setY(Utils.getHeight(this));
            v[i].animate().translationY(10f).setDuration(2000).start();
        }
        up = true;
        block(true);
    }

    void down() {
        View[] v = new View[]{box, deslogar, confirmar, cancelar};
        for(int i = 0; i<v.length; i++) {
            v[i].animate().translationY(1500f).setDuration(2000).start();
        }
        up = false;
        block(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ATUALIZAR)
            initialize();
    }

    @Override
    public void onBackPressed() {
        if(up)
            down();
        else
            this.finish();
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