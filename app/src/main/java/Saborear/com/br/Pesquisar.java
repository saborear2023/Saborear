package Saborear.com.br;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Collections;

import Saborear.com.br.Adapter.FiltroView;
import Saborear.com.br.Adapter.OrdenarView;
import Saborear.com.br.Adapter.PesquisarView;
import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Manage.ManageReceita;
import Saborear.com.br.Utils.Utils;

@SuppressLint("StaticFieldLeak")
public class Pesquisar extends AppCompatActivity {
    private static Database db;
    private static ManageReceita mg;
    private static Activity context;
    private static ViewPager filtros, ordenar;
    public static ListView listView;
    private static EditText text;
    private static ArrayList<Pair<ImageView, String>> clicked;
    private static ProgressBar loading, carregar;
    public static boolean carregando;
    public static ImageButton deletar;
    private ImageButton voltar;
    private static ImageButton home;
    private static ImageButton back, notificacao, recarregar;
    private static ImageButton perfil;
    public static int sel, limit, position;
    public static boolean maior;

    public static ImageView view, filtrar, ordena, sem;
    public static TextView txt;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.pesquisar);
        initialize();

        Utils.setupChat(this, findViewById(R.id.pesquisar_layout));

        ordena.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "Arraste pro lado para mais opções", Toast.LENGTH_SHORT).show());

        filtrar.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "Arraste pro lado para mais opções", Toast.LENGTH_SHORT).show());

        recarregar.setOnClickListener(v -> {
            recarregar.animate().rotationBy(360f).setDuration(1000).start();
            Handler handler = new Handler();
            Runnable code = () -> atualizar(true,listView.getLastVisiblePosition());
            handler.postDelayed(code, 1000);
        });

        text.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                atualizar(true, listView.getLastVisiblePosition());
                return true;
            }
            return false;
        });

        deletar.setOnClickListener(v -> {
            text.setText("");
            atualizar(false, listView.getLastVisiblePosition());
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

        notificacao.setOnClickListener(v -> {
            ActivityTracker.finishExceptMain(this);
            Intent myIntent = new Intent(getApplicationContext(), Notificacao.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
        });

        home.setOnClickListener(v -> {
            ActivityTracker.finishExceptMain(this);
            finish();
        });

    }

    public static void block(boolean block) {
        carregando = block;
        ViewGroup layout = context.findViewById(R.id.pesquisar_layout);
        View[] ignore = new View[]{home, notificacao, perfil};
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            view.setEnabled(!block);
        }
        if(!block)
            listView.setVisibility(View.VISIBLE);
        carregar.setIndeterminate(block);
        carregar.setVisibility(block ? View.VISIBLE : View.INVISIBLE);
    }

    void initialize() {
        ActivityTracker.addActivity(this);
        db = new Database(getApplicationContext());
        context = Pesquisar.this;
        carregando = false;

        voltar = findViewById(R.id.pesquisar_btn_voltar);
        home = findViewById(R.id.pesquisar_btn_home);
        perfil = findViewById(R.id.pesquisar_btn_perfil);
        notificacao = findViewById(R.id.pesquisar_btn_notificacao);
        filtros = findViewById(R.id.pesquisar_listview_01);
        loading = findViewById(R.id.pesquisar_carregar);
        text = findViewById(R.id.pesquisar_insert_nome);
        listView = findViewById(R.id.receita_listview_nutricional);
        deletar = findViewById(R.id.pesquisar_btn_delete);
        ordenar = findViewById(R.id.pesquisar_ordenar);
        carregar = findViewById(R.id.pesquisar_loading);
        ordena = findViewById(R.id.pesquisar_icon_ordenar);
        filtrar = findViewById(R.id.pesquisar_icon_filtros);
        recarregar = findViewById(R.id.pesquisar_recarregar);
        sem = findViewById(R.id.pesquisar_sem);
        back = findViewById(R.id.pesquisar_btn_back);
        clicked = new ArrayList<>();

        maior = true;

        sel = -1;
        limit = 10;
        position = 0;
        atualizar(false, listView.getLastVisiblePosition());

        StorageDatabase.atualizarPesquisar = false;

        ArrayList<String> nomes = new ArrayList<>(StorageDatabase.categ.keySet());
        Collections.sort(nomes);

        FiltroView adapterView = new FiltroView(Pesquisar.this, nomes);
        filtros.setAdapter(adapterView);

        nomes = new ArrayList<>();
        nomes.add("Nome");
        nomes.add("Nota");
        nomes.add("Views");
        nomes.add("Calorias");
        nomes.add("Comentário");
        nomes.add("Custo");
        nomes.add("Tempo");
        nomes.add("Recente");

        OrdenarView ad = new OrdenarView(Pesquisar.this, nomes);
        ordenar.setAdapter(ad);

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        ignore.add(findViewById(R.id.pesquisar_icon_filtros));
        ignore.add(findViewById(R.id.pesquisar_icon_ordenar));
        ignore.add(findViewById(R.id.pesquisar_campo));
        ignore.add(deletar); ignore.add(text); inverted.add(listView);

        Utils.blackMode(findViewById(R.id.pesquisar_layout), ignore, inverted);
        if(InternalDatabase.isDarkmode()) {
            ((ImageView)findViewById(R.id.pesquisar_icon_filtros)).setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_dark_filtros));
            ((ImageView)findViewById(R.id.pesquisar_icon_ordenar)).setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_dark_ordenar));
        }
    }

    public static void atualizar(Boolean scroll, int aux) {
        if(carregando) return;

        mg = new ManageReceita(StorageDatabase.receitas);

        ArrayList<String> filtro = new ArrayList<>();
        for (int i = 0; i < clicked.size(); i++)  filtro.add(StorageDatabase.categ.get(clicked.get(i).second));
        if(filtro.size() > 0) mg.getByIDCATEG(filtro);

        if(text.getText().toString().length() > 0) mg.getByNome(text.getText().toString());

        switch(sel) {
            default:
                mg.ordenarByID();
                break;
            case 0:
                mg.ordenarByNome().inverter();
                break;
            case 1:
                mg.ordenarByNota();
                break;
            case 2:
                mg.ordenarByViews();
                break;
            case 3:
                mg.ordenarByCalorias();
                break;
            case 4:
                mg.ordenarByComentario();
                break;
            case 5:
                mg.ordenarByCusto();
                break;
            case 6:
                mg.ordenarByTempo();
                break;
            case 7:
                mg.ordenarbyCriacao();
                break;
        }

        if(maior) mg.inverter();

        Boolean carregar = mg.getReceitas().size() > limit;

        ArrayList<classeReceita> receitas = mg.limitar(limit).getReceitas();
        if(carregar) receitas.add(new classeReceita());

        if(receitas.size() == 0) sem.setVisibility(View.VISIBLE);
        else sem.setVisibility(View.INVISIBLE);

        PesquisarView adapterView = new PesquisarView(context, receitas, carregar);
        listView.setAdapter(adapterView);
        if(scroll) listView.setSelection(aux > 3 ? aux - 3 : 0);
        else limit = 10;
    }

    public static void addImg(ImageView v, String t, boolean atualizar) {
        clicked.add(new Pair<>(v, t));

        if(atualizar)
            atualizar(false, listView.getLastVisiblePosition());
    }

    public static void delImg(ImageView v, String t, boolean atualizar) {
        clicked.remove(new Pair<>(v, t));

        if(atualizar)
            atualizar(false, listView.getLastVisiblePosition());
    }

    public static boolean contains(String t) {
        for (Pair<ImageView, String> pair : clicked) {
            if(pair.second.equals(t)) return true;
        }
        return false;
    }

    public static ImageView getImageByString(String t) {
        for (Pair<ImageView, String> pair : clicked) {
            if(pair.second.equals(t)) return pair.first;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && StorageDatabase.atualizarPesquisar) {
            atualizar(false, listView.getLastVisiblePosition());
            listView.setVisibility(View.INVISIBLE);
            StorageDatabase.atualizarPesquisar = false;
        }
    }

    protected void onResume() {
        super.onResume();
        atualizar(true, listView.getLastVisiblePosition());
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
