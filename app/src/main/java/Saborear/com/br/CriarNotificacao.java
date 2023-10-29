package Saborear.com.br;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import Saborear.com.br.Adapter.NotificacaoView;
import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.Utils;

public class CriarNotificacao extends AppCompatActivity {

    private Database db;
    private ImageButton back, home, pesquisar, perfil, notificacao, voltar, criar;
    private EditText titulo, mensagem;
    private Spinner spinner;
    private ListView listview;

    private ProgressBar loading;
    private String target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificacaoadd);
        initialize();

        titulo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) { atualizar(); }
        });

        mensagem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) { atualizar(); }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                db.requestScript("select * from receita order by nome", onFinish -> {
                    for (Map<String, String> data : onFinish) {
                        if (data.get("nome").equals(spinner.getSelectedItem().toString())) {
                            target = data.get("id_receita");
                            atualizar();
                            break;
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        criar.setOnClickListener(v -> {
            if(titulo.getText().length() == 0 || mensagem.getText().length() == 0 || spinner.getSelectedItemPosition() == 0) {
                Toast.makeText(getApplicationContext(), "Preencha os campos", Toast.LENGTH_SHORT).show();
                return;
            }
            block(true);
            db.requestScript("select * from receita order by nome", onFinish -> {
                for (Map<String, String> i : onFinish) {
                    if(i.get("nome").equals(spinner.getSelectedItem().toString())) {
                        String sql = "insert into notificacao(id_receita, titulo, mensagem) values('@id', '@titulo', '@mensagem');";
                        sql = sql.replace("@id", i.get("id_receita"));
                        sql = sql.replace("@titulo", titulo.getText().toString());
                        sql = sql.replace("@mensagem", mensagem.getText().toString());

                        titulo.setText("");
                        mensagem.setText("");
                        spinner.setSelection(0);

                        db.requestScript(sql, d -> Toast.makeText(getApplicationContext(), "Notificação adicionada", Toast.LENGTH_SHORT).show());
                        break;
                    }
                }
                block(false);
            });
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
        ViewGroup layout = findViewById(R.id.notificacaoadd_layout);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            view.setEnabled(!block);
        }
        loading.setIndeterminate(block);
        loading.setVisibility(block ? View.VISIBLE : View.INVISIBLE);
    }

    public void initialize() {
        ActivityTracker.addActivity(this);
        db = new Database(this);

        home = findViewById(R.id.notificacaoadd_btn_home);
        pesquisar = findViewById(R.id.notificacaoadd_btn_pesquisar);
        perfil = findViewById(R.id.notificacaoadd_btn_perfil);
        notificacao = findViewById(R.id.notificacaoadd_btn_notificacao);
        voltar = findViewById(R.id.notificacaoadd_voltar);
        criar = findViewById(R.id.notificacaoadd_criar);
        titulo = findViewById(R.id.notificacaoadd_insert_01);
        mensagem = findViewById(R.id.notificacaoadd_insert_02);
        listview = findViewById(R.id.receita_listview_nutricional);
        spinner = findViewById(R.id.notificacaoadd_spinner);
        loading = findViewById(R.id.notificacaoadd_carregar);
        back = findViewById(R.id.notificacaoadd_btn_back);

        atualizar();

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        ignore.add(titulo); inverted.add(listview); ignore.add(criar); ignore.add(mensagem);
        Utils.blackMode(findViewById(R.id.notificacaoadd_layout), ignore, inverted);
    }

    public void atualizar() {
        ArrayList<String> id = new ArrayList<>();
        HashMap<String, classeReceita> receitas = new HashMap<>();
        HashMap<String, String> title = new HashMap<>();
        HashMap<String, String> msg = new HashMap<>();

        id.add("0");
        title.put("0", titulo.getText().toString());
        msg.put("0", mensagem.getText().toString());

        String sql = StorageDatabase.getScript()+"where r.id_receita not in(select id_receita from notificacao);";
        db.requestScript(sql, data -> {
            Map<String, String> values = new HashMap<>();
            ArrayList<String> keys = new ArrayList<>();
            for (Map<String, String> i : data) {
                values.put(i.get("nome"), i.get("id_ingrediente"));
                keys.add(i.get("nome"));
            }

            Collections.sort(keys); keys.add(0, "Clique para escolher a receita");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, keys);
            spinner.setAdapter(adapter);
        });

        NotificacaoView adapterView = new NotificacaoView(CriarNotificacao.this, id, new ArrayList<>(), title, msg, receitas, null, false);
        listview.setAdapter(adapterView);
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