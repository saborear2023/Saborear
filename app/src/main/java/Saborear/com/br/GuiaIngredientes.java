package Saborear.com.br;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Collections;

import Saborear.com.br.Adapter.FiltroGuiaView;
import Saborear.com.br.Adapter.GuiaIngredientesView;
import Saborear.com.br.Adapter.OrdenarGuiaView;
import Saborear.com.br.Classes.classeNutricional;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Manage.ManageNutricionista;
import Saborear.com.br.Manage.ManageReceita;
import Saborear.com.br.Utils.Utils;

public class GuiaIngredientes extends AppCompatActivity {
    private static Activity context;
    private static ManageReceita mg;
    private static Database db;
    private static ListView ingredientes;
    private static ArrayList<Pair<ImageView, String>> clicked;
    private ImageButton voltar;
    private ImageButton home;
    private ImageButton pesquisar;
    private ImageButton notificacao;
    private ImageButton perfil;
    private ImageButton deletar;

    private ImageButton back;
    private static EditText insert;
    private static ImageView sem, ord;
    private static ViewPager ordenar, filtros;
    public static int sel;
    public static boolean maior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.guiaingredientes);
        initialize();

        Utils.setupChat(this, findViewById(R.id.guiaingredientes_layout));

        back.setOnClickListener(v -> this.finish());

        ord.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "Arraste pro lado para mais opções", Toast.LENGTH_SHORT).show());

        insert.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                atualizar();
                return true;
            }
            return false;
        });

        deletar.setOnClickListener(v -> {
            insert.setText("");
            atualizar();
        });

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
    private void initialize() {
        ActivityTracker.addActivity(this);
        context = GuiaIngredientes.this;
        db = new Database(this);

        voltar = findViewById(R.id.guiaingredientes_btn_voltar);
        perfil = findViewById(R.id.guiaingredientes_btn_perfil);
        home = findViewById(R.id.guiaingredientes_btn_home);
        pesquisar = findViewById(R.id.guiaingredientes_btn_pesquisar);
        notificacao = findViewById(R.id.guiaingredientes_btn_notificacao);
        ingredientes = findViewById(R.id.guiaingredientes_listview);
        deletar = findViewById(R.id.guiaingredientes_btn_delete);
        insert = findViewById(R.id.guiaingredientes_insert);
        sem = findViewById(R.id.guiaingredientes_sem);
        ordenar = findViewById(R.id.guiaingredientes_ordenar);
        ord = findViewById(R.id.guiaingredientes_icon_ordenar);
        filtros = findViewById(R.id.guiaingredientes_listview_filtros);
        back = findViewById(R.id.guiaingredientes_btn_back);

        clicked = new ArrayList<>();
        maior = true;
        sel = -1;

        ArrayList<String> nomes = StorageDatabase.nomescategingred;

        Collections.sort(nomes);

        FiltroGuiaView adapterView = new FiltroGuiaView(this, nomes);
        filtros.setAdapter(adapterView);

        nomes = new ArrayList<>();
        nomes.add("Sat.");
        nomes.add("Trans");
        nomes.add("Fibra");
        nomes.add("Açúcar");
        nomes.add("Vitam. D");
        nomes.add("Cálcio");
        nomes.add("Ferro");
        nomes.add("Potássio");
        nomes.add("Calorias");
        nomes.add("Peso");

        OrdenarGuiaView ad = new OrdenarGuiaView(this, nomes);
        ordenar.setAdapter(ad);

        atualizar();

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        ignore.add(ord); ignore.add(findViewById(R.id.guiaingredientes_icon_filtros));
        ignore.add(findViewById(R.id.guiaingredientes_campo)); inverted.add(ingredientes);
        Utils.blackMode(findViewById(R.id.guiaingredientes_layout), ignore, inverted);

        ingredientes.setDividerHeight(Utils.intToDp(16));

        if(InternalDatabase.isDarkmode()) {
            ((ImageView)findViewById(R.id.guiaingredientes_icon_filtros)).setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_dark_filtros));
            ((ImageView)findViewById(R.id.guiaingredientes_icon_ordenar)).setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_dark_ordenar));
        }
    }


    public static void atualizar() {
        String search = insert.getText().toString();

        ArrayList<classeNutricional> nutrientes = new ArrayList<>();
        for (String key : StorageDatabase.nutricional.keySet()) {
            classeNutricional nutriente = StorageDatabase.nutricional.get(key);

            if(search.length() == 0 || nutriente.getNome().toLowerCase().contains(search))
                nutrientes.add(nutriente);
        }

        ManageNutricionista mg = new ManageNutricionista(nutrientes);

        ArrayList<String> filtro = new ArrayList<>();
        for (int i = 0; i < clicked.size(); i++)  filtro.add(clicked.get(i).second);
        if(filtro.size() > 0) mg.getByCategoria(filtro);

        switch (sel) {
            default:
                mg.ordernarByNome();
                break;
            case 0:
                mg.ordernarBySat();
                break;
            case 1:
                mg.ordernarByTrans();
                break;
            case 2:
                mg.ordernarByFibra();
                break;
            case 3:
                mg.ordernarByAcucar();
                break;
            case 4:
                mg.ordernarByVitaminaD();
                break;
            case 5:
                mg.ordernarByCalcio();
                break;
            case 6:
                mg.ordernarByFerro();
                break;
            case 7:
                mg.ordernarByPotassio();
                break;
            case 8:
                mg.ordernarByCalorias();
                break;
            case 9:
                mg.ordernarByPeso();
                break;
        }

        if(sel != -1 && maior) mg.inverter();

        GuiaIngredientesView adapterView = new GuiaIngredientesView(context, mg.getNutrientes());
        ingredientes.setAdapter(adapterView);

        if(nutrientes.size() > 0) sem.setVisibility(View.INVISIBLE);
         else sem.setVisibility(View.VISIBLE);

    }

    public static void addImg(ImageView v, String t, boolean atualizar) {
        clicked.add(new Pair<>(v, t));
        atualizar();
    }

    public static void delImg(ImageView v, String t, boolean atualizar) {
        clicked.remove(new Pair<>(v, t));
        atualizar();
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
    protected void onResume() {
        super.onResume();
        atualizar();
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