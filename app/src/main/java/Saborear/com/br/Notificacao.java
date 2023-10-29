package Saborear.com.br;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Saborear.com.br.Adapter.NotificacaoView;
import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.Utils;

@SuppressLint("StaticFieldLeak")
public class Notificacao extends AppCompatActivity {

    private static Activity context;
    private static Database db;

    private static ProgressBar loading;
    private static ListView listview;

    private ImageButton voltar;
    private ImageButton home;
    private ImageButton pesquisar;
    private ImageButton perfil;
    private ImageButton criarnotificacao;
    private ImageButton back;
    private static ImageButton sem;

    private static ImageButton limpar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.notificacao);
        initialize();
        admin();

        criarnotificacao.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), CriarNotificacao.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
        });

        limpar.setOnClickListener(v -> {
            block(true);
            String sql = "update usuario_notificacao set excluido='true' where email='@email';";
            sql = InternalDatabase.convert(sql);
            db.requestScript(sql, data -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, new ArrayList<>());
                listview.setAdapter(adapter);
                block(false);
            });
        });

        back.setOnClickListener(v -> this.finish());

        voltar.setOnClickListener(v -> finish());

        pesquisar.setOnClickListener(v -> {
            ActivityTracker.finishExceptMain(this);
            Intent myIntent = new Intent(getApplicationContext(), Pesquisar.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
        });

        perfil.setOnClickListener(v -> {
            ActivityTracker.finishExceptMain(this);
            Intent myIntent = new Intent(getApplicationContext(), Perfil.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
        });

        home.setOnClickListener(v -> {
            ActivityTracker.finishExceptMain(this);
            finish();
        });
    }
    static void block(Boolean block) {
        View[] views = new View[]{limpar};
        for (View view : views) {
            view.setEnabled(!block);

        }
        loading.setIndeterminate(block);
        loading.setVisibility(block ? View.VISIBLE : View.INVISIBLE);
    }

    void initialize() {
        ActivityTracker.addActivity(this);
        context = this;
        db = new Database(this);

        voltar = findViewById(R.id.notificacao_btn_voltar);
        home = findViewById(R.id.notificacao_btn_home);
        pesquisar = findViewById(R.id.notificacao_btn_pesquisar);
        perfil = findViewById(R.id.notificacao_btn_perfil);
        limpar = findViewById(R.id.notificacao_btn_limpar);
        loading = findViewById(R.id.notificacao_carregar);
        listview = findViewById(R.id.receita_listview_nutricional);
        criarnotificacao = findViewById(R.id.notificacao_btn_addnotificacao);
        sem = findViewById(R.id.notificacao_sem);
        back = findViewById(R.id.notificacao_btn_back);
        atualizar();

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        ignore.add(limpar); inverted.add(listview);
        Utils.blackMode(findViewById(R.id.notificacao_layout), ignore, inverted);
    }

    private void admin() {
        if(StorageDatabase.isAdmin)
            criarnotificacao.setVisibility(View.VISIBLE);
    }

    public static void atualizar() {
        block(true);
        String sql = "update usuario_notificacao set visto='true' where email='@email'; select * from notificacao where id_notificacao in (select id_notificacao from usuario_notificacao where email = '@email' and excluido = 'false');";
        sql = InternalDatabase.convert(sql);
        db.requestScript(sql, data -> {
            if (data.size() == 0) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, new ArrayList<>());
                listview.setAdapter(adapter);
                block(false);

                sem.setVisibility(View.VISIBLE);
                return;

            } else sem.setVisibility(View.INVISIBLE);

            ArrayList<String> id = new ArrayList<>();
            ArrayList<String> idreceitas  = new ArrayList<>();
            HashMap<String, String> titulo = new HashMap<>();
            HashMap<String, String> mensagem = new HashMap<>();
            HashMap<String, String> target = new HashMap<>();

            for (Map<String, String> item : data) {
                id.add(item.get("id_notificacao"));
                idreceitas.add(item.get("id_receita"));
                titulo.put(item.get("id_notificacao"), item.get("titulo"));
                mensagem.put(item.get("id_notificacao"), item.get("mensagem"));
                target.put(item.get("id_notificacao"), item.get("id_receita"));
            }

            if(id.size() == 0) sem.setVisibility(View.VISIBLE);
            else sem.setVisibility(View.INVISIBLE);

            String search = "where id_receita in (";
            for (int i = 0; i < idreceitas.size(); i++) {
                search += idreceitas.get(i);
                if(i + 1 < id.size()) search += ",";
            }
            search += ");";

            db.requestScript(StorageDatabase.getScript()+search, finish -> {
                HashMap<String, classeReceita> receitas = new HashMap<>();
                for (Map<String, String> map : finish) {
                    receitas.put(map.get("id_receita"), new classeReceita(map));
                }

                NotificacaoView adapterView = new NotificacaoView(context, id, idreceitas, titulo, mensagem, receitas,null, true);
                listview.setAdapter(adapterView);
                block(false);
            });
        });
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
