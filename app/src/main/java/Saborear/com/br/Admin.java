package Saborear.com.br;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.Utils;

public class Admin extends AppCompatActivity {

    private Activity activity;
    private Database db;
    private ImageButton back, voltar, home, pesquisar, perfil, notificacao, catingredadd, catingreddel, catreceitaadd, catreceitadel, salvar, deletar, validar;
    private EditText catingred, catreceita, ingrednome;
    private Spinner spinner, ingrediente, receita, ingredientes, chatbot;
    private ProgressBar loading;
    private HashMap<String, String> ids, idsreceitas;
    private HashMap<String, classeReceita> receitas;
    private int onprocess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin);
        Utils.setup(this);
        initialize();

        Utils.setupChat(this, findViewById(R.id.admin_layout));

        catingredadd.setOnClickListener(v -> {
            String txt = Utils.formatar(catingred.getText().toString());
            if(txt.length() == 0) return;
            Database db = new Database(this);
            String sql = "delete from categ_ingrediente where nome='@nome'; insert into categ_ingrediente(nome) values('@nome');".replace("@nome", txt);
            db.requestScript(sql, data -> {
                atualizar();
                Toast.makeText(getApplicationContext(), "Categoria adicionada", Toast.LENGTH_SHORT).show();
                catingred.setText("");
            });
        });

        catingreddel.setOnClickListener(v -> {
            String txt = catingred.getText().toString();
            if(txt.length() == 0) return;
            Database db = new Database(this);
            String sql = "delete from categ_ingrediente where nome='@nome';".replace("@nome", txt);
            db.requestScript(sql, data -> {
                atualizar();
                Toast.makeText(getApplicationContext(), "Categoria removida", Toast.LENGTH_SHORT).show();
                catingred.setText("");
            });
        });

        catreceitaadd.setOnClickListener(v -> {
            String txt = Utils.formatar(catreceita.getText().toString());
            if(txt.length() == 0) return;
            Database db = new Database(this);
            String sql = "delete from categ_receita where nome='@nome'; insert into categ_receita(nome) values('@nome');".replace("@nome", txt);
            db.requestScript(sql, data -> {
                atualizar();
                Toast.makeText(getApplicationContext(), "Categoria adicionada", Toast.LENGTH_SHORT).show();
                catreceita.setText("");
            });
        });

        catreceitadel.setOnClickListener(v -> {
            String txt = catreceita.getText().toString();
            if(txt.length() == 0) return;
            Database db = new Database(this);
            String sql = "delete from categ_receita where nome='@nome';".replace("@nome", txt);
            db.requestScript(sql, data -> {
                atualizar();
                Toast.makeText(getApplicationContext(), "Categoria removida", Toast.LENGTH_SHORT).show();
                catreceita.setText("");
            });
        });

        salvar.setOnClickListener(v -> {
            String nome = Utils.formatar(ingrednome.getText().toString());
            String selecionado = spinner.getSelectedItem().toString();

            if(nome.length() == 0 || selecionado.length() == 0) {
                Toast.makeText(getApplicationContext(), "Preencha os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            String sql = "insert into ingrediente (id_cat_ingrediente, nome) values ('@id', '@nome') on conflict (nome) do update set id_cat_ingrediente = '@id';".replace("@nome", nome);
            sql = sql.replace("@id", ids.get(selecionado));
            db.requestScript(sql, data -> {
                atualizar();
                Toast.makeText(getApplicationContext(), "Ingrediente adicionado", Toast.LENGTH_SHORT).show();
                ingrednome.setText("");
            });
        });

        deletar.setOnClickListener(v -> {
            String nome = ingrednome.getText().toString();
            String selecionado = spinner.getSelectedItem().toString();

            if(nome.length() == 0 || selecionado.length() == 0) {
                Toast.makeText(getApplicationContext(), "Preencha os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            String sql = "delete from ingrediente where nome='@nome';".replace("@nome", nome);
            sql = sql.replace("@id", ids.get(selecionado));
            db.requestScript(sql, data -> {
                atualizar();
                Toast.makeText(getApplicationContext(), "Ingrediente removido", Toast.LENGTH_SHORT).show();
                ingrednome.setText("");
            });
        });

        validar.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Validar.class);
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
        ViewGroup layout = findViewById(R.id.admin_layout);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            view.setEnabled(!block);
        }
        loading.setIndeterminate(block);
        loading.setVisibility(block ? View.VISIBLE : View.INVISIBLE);
    }

    private void initialize() {
        ActivityTracker.addActivity(this);
        activity = this;
        db = new Database(this);

        voltar = findViewById(R.id.admin_voltar);
        home = findViewById(R.id.admin_btn_home);
        pesquisar = findViewById(R.id.admin_btn_pesquisar);
        perfil = findViewById(R.id.admin_btn_perfil);
        notificacao = findViewById(R.id.admin_btn_notificacao);
        catingred = findViewById(R.id.admin_insert_01);
        catreceita = findViewById(R.id.admin_insert_02);
        ingrednome = findViewById(R.id.admin_insert_03);
        spinner = findViewById(R.id.admin_spinner);
        catingredadd = findViewById(R.id.admin_add_01);
        catreceitaadd = findViewById(R.id.admin_add_02);
        salvar = findViewById(R.id.admin_add_03);
        catreceitadel = findViewById(R.id.admin_del_02);
        catingreddel = findViewById(R.id.admin_del_01);
        deletar = findViewById(R.id.admin_del_03);
        ingredientes = findViewById(R.id.admin_spinner_ingrediente_02);
        receita = findViewById(R.id.admin_spinner_receita);
        ingrediente = findViewById(R.id.admin_spinner_ingrediente);
        loading = findViewById(R.id.admin_carregar);
        chatbot = findViewById(R.id.admin_spinner_chatbot);
        validar = findViewById(R.id.admin_validar);
        back = findViewById(R.id.admin_btn_back);

        ingredientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    block(true);
                    ingrednome.setText(ingredientes.getSelectedItem().toString());
                    db.requestScript("select categ_ingrediente.nome from ingrediente join categ_ingrediente on ingrediente.id_cat_ingrediente = categ_ingrediente.id_cat_ingrediente where ingrediente.nome = '@nome';".replace("@nome", ingrednome.getText().toString()), data -> {
                        String nome = data.get(0).get("nome");
                        if(data != null && data.size() > 0) {
                            SpinnerAdapter adapter = spinner.getAdapter();
                            int size = adapter.getCount();
                            for (int i = 0; i < size; i++) {
                                if(adapter.getItem(i).toString().equals(nome)) {
                                    spinner.setSelection(i);
                                    break;
                                }
                            }
                        }
                        block(false);
                    });
                    ingredientes.setSelection(0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        receita.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    catreceita.setText(receita.getSelectedItem().toString());
                    receita.setSelection(0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        ingrediente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    catingred.setText(ingrediente.getSelectedItem().toString());
                    ingrediente.setSelection(0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        chatbot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    Receita.receita = receitas.get(chatbot.getSelectedItem().toString());
                    Intent myIntent = new Intent(getApplicationContext(), Receita.class);
                    activity.startActivityForResult(myIntent, 1);
                    activity.overridePendingTransition(0, 0);
                    chatbot.setSelection(0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        atualizar();

        ArrayList<View> ignore = new ArrayList<>();
        ignore.add(catingred); ignore.add(catreceita); ignore.add(catingreddel); ignore.add(deletar);
        ignore.add(ingrednome); ignore.add(validar); ignore.add(catingredadd);
        ignore.add(catreceitaadd); ignore.add(catreceitadel); ignore.add(salvar);
        Utils.blackMode(findViewById(R.id.admin_layout), ignore, null);
    }

    private void atualizar() {
        block(true);
        onprocess = 0;
        db.requestScript("select * from categ_ingrediente", data -> {
            ArrayList<String> arr = new ArrayList<>();
            ArrayList<String> narr = new ArrayList<>();


            ids = new HashMap<>();
            for (Map<String, String> i : data) {
                arr.add(i.get("nome")); narr.add(i.get("nome"));
                ids.put(i.get("nome"), i.get("id_cat_ingrediente"));
            }

            Collections.sort(arr);
            Collections.sort(narr); narr.add(0, "Clique para escolher");

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arr);
            spinner.setAdapter(adapter);

            ArrayAdapter<String> nadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, narr);
            ingrediente.setAdapter(nadapter);

            if(++onprocess == 4)
                block(false);
        });

        db.requestScript("select * from categ_receita", data -> {
            ArrayList<String> arr = new ArrayList<>();
            for (Map<String, String> i : data)
                arr.add(i.get("nome"));

            Collections.sort(arr); arr.add(0, "Clique para escolher");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arr);
            receita.setAdapter(adapter);

            if(++onprocess == 4)
                block(false);
        });

        db.requestScript("select * from ingrediente", data -> {
            ArrayList<String> arr = new ArrayList<>();
            for (Map<String, String> i : data)
                arr.add(i.get("nome"));

            Collections.sort(arr); arr.add(0, "Clique para escolher");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arr);
            ingredientes.setAdapter(adapter);

            if(++onprocess == 4)
                block(false);
        });

        //where r.id_receita not in (select id_receita from receita_subcateg where id_acao='Básico' or id_acao='Intermediário' or id_acao='Avançado') and r.id_cat_receita not in(7, 8);
        //where r.id_receita not in (select id_receita from receita_subcateg) and r.id_cat_receita not in(7, 8);
        db.requestScript(StorageDatabase.getScript()+"where r.id_receita not in (select id_receita from receita_subcateg) and r.id_cat_receita not in(7, 8) and id_receita not in(-1);", data -> {
            ArrayList<String> arr = new ArrayList<>();

            idsreceitas = new HashMap<>();
            receitas = new HashMap<>();
            for (Map<String, String> i : data) {
                arr.add(i.get("nome"));
                idsreceitas.put(i.get("nome"), i.get("id_receita"));
                receitas.put(i.get("nome"), new classeReceita(i));
            }

            Collections.sort(arr); arr.add(0, "Clique para escolher");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arr);
            chatbot.setAdapter(adapter);

            if(++onprocess == 4)
                block(false);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            atualizar();
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
