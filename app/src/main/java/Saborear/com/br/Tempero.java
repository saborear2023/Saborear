package Saborear.com.br;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import Saborear.com.br.Adapter.UtilizacaoView;
import Saborear.com.br.Classes.classeTempero;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.FireDatabase;
import Saborear.com.br.Database.ImageDatabase;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Manage.Manage;
import Saborear.com.br.Utils.Utils;

public class Tempero extends AppCompatActivity {

    private Database db;
    private TextView nome, descricao;
    private ImageView imagem;
    private ListView listview;
    private ImageButton back, voltar, home, notificacao, pesquisar, perfil, editar, deletar;
    private ProgressBar loading;
    public static classeTempero tempero;

    public static boolean atualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.tempero);
        initialize();
        admin();
        
        deletar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Você realmente deseja deletar o tempero?")
                    .setTitle("Saborear")
                    .setNegativeButton("Cancelar", (dialog, id) -> {})
                    .setPositiveButton("Sim", (dialog, id) -> {
                        block(true);
                        db.requestScript(tempero.deleteScript(), data -> {
                            FireDatabase.updateTempero(tempero.getIdtempero(), "deletar");
                            Manage.deletarTempero(tempero.getIdtempero());
                            Toast.makeText(getApplicationContext(), "Tempero deletado", Toast.LENGTH_SHORT).show();
                            this.finish();
                        });

                    });
            builder.create().show();
        });

        editar.setOnClickListener(v -> {
            CadastrarTempero.preload = tempero;
            Intent myIntent = new Intent(getApplicationContext(), CadastrarTempero.class);
            startActivityForResult(myIntent, 1);
            this.overridePendingTransition(0, 0);
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

    private void block(boolean block) {
        loading.setIndeterminate(block);
        loading.setVisibility(block ? View.VISIBLE : View.INVISIBLE);

        deletar.setEnabled(!block);
        editar.setEnabled(!block);
    }

    private void atualizar() {
        try {
            nome.setText(tempero.getNome());
            descricao.setText(tempero.getDescricao());

            UtilizacaoView adapterView = new UtilizacaoView(this, tempero.getUtilizacoes());
            listview.setAdapter(adapterView);
            listview.requestLayout();

            ViewGroup.LayoutParams layoutParams = listview.getLayoutParams();
            layoutParams.height = Utils.intToDp(64*(tempero.getUtilizacoes().size())+16*(tempero.getUtilizacoes().size()));
            listview.setLayoutParams(layoutParams);

            if(tempero.getBitmap() == null) {
                block(true);
                db.requestScript("select imagem from tempero where id_tempero='@id';".replace("@id", tempero.getIdtempero()), end -> {
                    block(false);
                    tempero.setBitmap(ImageDatabase.decode(end.get(0).get("imagem")));
                    imagem.setImageBitmap(tempero.getBitmap());
                    Manage.findTempero(tempero.getIdtempero()).setBitmap(tempero.getBitmap());
                });
            } else imagem.setImageBitmap(tempero.getBitmap());

        } catch(Exception e) {
            Log.e("SaborearDatabase", "Tempero: "+e.getMessage());
        }
    }

    public void initialize() {
        db = new Database(this);
        voltar = findViewById(R.id.tempero_btn_voltar);
        home = findViewById(R.id.tempero_btn_home);
        pesquisar = findViewById(R.id.tempero_btn_pesquisar);
        perfil = findViewById(R.id.tempero_btn_perfil);
        notificacao = findViewById(R.id.tempero_btn_notificacao);
        editar = findViewById(R.id.tempero_btn_editar);
        deletar = findViewById(R.id.tempero_btn_deletar);
        nome = findViewById(R.id.tempero_titulo);
        descricao = findViewById(R.id.tempero_descricao);
        imagem = findViewById(R.id.tempero_imagem);
        listview = findViewById(R.id.tempero_listview);
        loading = findViewById(R.id.tempero_carregar);
        back = findViewById(R.id.tempero_btn_back);

        atualizar = false;

        atualizar();

        if(InternalDatabase.isDarkmode()) {
            int corDivisor = Color.parseColor("#7F7F7F");
            ColorDrawable divisorDrawable = new ColorDrawable(corDivisor);
            listview.setDivider(divisorDrawable);
        }

        listview.setDividerHeight(Utils.intToDp(16));

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        ignore.add(imagem); ignore.add(editar); ignore.add(deletar); ignore.add(listview);
        Utils.blackMode(findViewById(R.id.tempero_layout), ignore, inverted);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && atualizar) {
            atualizar = false;
            atualizar();
        }
    }

    private void admin() {
        if(!StorageDatabase.isAdmin) return;

        editar.setVisibility(View.VISIBLE);
        deletar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}