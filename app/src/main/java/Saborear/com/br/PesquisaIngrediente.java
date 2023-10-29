package Saborear.com.br;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import Saborear.com.br.Adapter.MinireceitaView;
import Saborear.com.br.Adapter.ShortIngredienteView;
import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Manage.ManageReceita;
import Saborear.com.br.Utils.Utils;

public class PesquisaIngrediente  extends AppCompatActivity {
    private static Activity activity;
    private static ManageReceita mg;
    private static Database db;
    private ImageButton voltar;
    private ImageButton home;
    private ImageButton pesquisar;
    private ImageButton notificacao;
    private ImageButton perfil;
    private ImageButton back;
    private ImageButton finish;
    private CheckBox checkbox;
    private ImageView sem;
    private static ListView listview;
    private static ViewPager receitas;

    public static ArrayList<String> ingredientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pesquisaingrediente);
        initialize();

        finish.setOnClickListener(v -> pesquisar());

        back.setOnClickListener(v -> this.finish());

        voltar.setOnClickListener(v -> finish());

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
        activity = this;
        voltar = findViewById(R.id.pesquisaingrediente_btn_voltar);
        perfil = findViewById(R.id.pesquisaingrediente_btn_perfil);
        home = findViewById(R.id.pesquisaingrediente_btn_home);
        pesquisar = findViewById(R.id.pesquisaingrediente_btn_pesquisar);
        notificacao = findViewById(R.id.pesquisaingrediente_btn_notificacao);
        back = findViewById(R.id.pesquisaingrediente_btn_back);
        listview = findViewById(R.id.pesquisaingrediente_listview);
        receitas = findViewById(R.id.pesquisaingrediente_receitas);
        finish = findViewById(R.id.pesquisaingrediente_btn_finish);
        sem = findViewById(R.id.pesquisaingrediente_sem);
        checkbox = findViewById(R.id.pesquisaingrediente_checkbox);

        ingredientes = new ArrayList<>();
        addIngrediente();

        ManageReceita mg = new ManageReceita(StorageDatabase.receitas);
        MinireceitaView adapter = new MinireceitaView(activity, mg.randomizar().getReceitas());
        receitas.setAdapter(adapter);

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        inverted.add(listview); ignore.add(finish);
        Utils.blackMode(findViewById(R.id.pesquisaingrediente_layout), ignore, inverted);
    }

    public static void addIngrediente() {
        ingredientes.add("Clique para selecionar");
        atualizar();
    }

    public static void delIngrediente(int position) {
        if(ingredientes.size() > 1 && ingredientes.size() > position) ingredientes.remove(position);
        atualizar();
    }

    public static void atualizar() {
        ShortIngredienteView ab = new ShortIngredienteView(activity, ingredientes);
        listview.setAdapter(ab);
        listview.setSelection(ingredientes.size()-1);
    }

    public void pesquisar() {
        ArrayList<String> ningredientes = new ArrayList<>();
        for (String ingrediente : ingredientes) {
            if(ingrediente.equals("Clique para selecionar")) continue;
            ningredientes.add(ingrediente);
        }

        if(ningredientes.size() == 0) {
            Toast.makeText(getApplicationContext(), "Selecione um ingrediente", Toast.LENGTH_SHORT).show();
            return;
        }

        ManageReceita mg = new ManageReceita(StorageDatabase.receitas);

        ArrayList<classeReceita> nreceitas;

        if(checkbox.isChecked()) nreceitas = mg.getByAllObrigIngredients(ningredientes).getReceitas();
        else nreceitas = mg.getByAllOpcIngredients(ningredientes).getReceitas();

        MinireceitaView adapter = new MinireceitaView(activity, nreceitas);
        receitas.setAdapter(adapter);

        if(nreceitas.size() > 0) sem.setVisibility(View.INVISIBLE);
        else sem.setVisibility(View.VISIBLE);
    }
}

