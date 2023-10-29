package Saborear.com.br;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Random;

import Saborear.com.br.Adapter.BannerView;
import Saborear.com.br.Adapter.CategoriaView;
import Saborear.com.br.Adapter.GrandereceitaView;
import Saborear.com.br.Adapter.MinireceitaView;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Manage.ManageReceita;
import Saborear.com.br.Utils.Notification;
import Saborear.com.br.Utils.Utils;

public class Main extends AppCompatActivity {

    private static Activity activity;
    private static Database db;
    private static ManageReceita mg;

    private static boolean firstTime = true;
    private static ImageButton home;
    private static ImageButton pesquisar;
    private static ImageButton perfil;
    private static ImageButton notificacao;
    private static ImageButton criar;
    private static ImageButton back;
    private static ViewPager banner;
    private static ViewPager categorias;
    private static ViewPager melhores;
    private static ViewPager principal;
    private static ViewPager sugestoes;
    private static ListView listview;
    private static TextView txtmelhores;
    private static TextView txtdrinks;
    private static BannerView at;
    private static CategoriaView ad;

    public static String cat;

    public static int finish = 0, bannerposicao = 0;

    private long time = 0;
    public static int count = 0, ignorarbanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.main);
        initialize();

        banner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ignorarbanner = 1;
            }

            @Override
            public void onPageSelected(int position) {  }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        if(firstTime) {
            bannerposicao = 0;
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if(ignorarbanner > 0) ignorarbanner--;
                    else {
                        bannerposicao = banner.getCurrentItem();
                        if(++bannerposicao == 4) bannerposicao = 0;
                        banner.setCurrentItem(bannerposicao, true);
                    }
                    handler.postDelayed(this, 3000);
                }
            };
            handler.postDelayed(runnable, 3000);

            if(new Random().nextDouble() < 0.25)
                Notification.send(this);
            String sql = "select * from usuario_notificacao where email='@email' and excluido='false' and visto='false'";
            sql = InternalDatabase.convert(sql);
            db.requestScript(sql, onFinish -> {
                if(onFinish.size() > 0)
                    Toast.makeText(getApplicationContext(), "Você tem notificações", Toast.LENGTH_SHORT).show();
            });
            firstTime = false;
        }

        back.setOnClickListener(v -> {
            if(System.currentTimeMillis() - time < 3000) {
                finishAffinity();
            } else {
                Toast.makeText(getApplicationContext(), "Clique novamente para sair", Toast.LENGTH_SHORT).show();
                time = System.currentTimeMillis();
            }
        });

        criar.setOnClickListener(v -> {
            block(true, false);
            criar.animate().rotationBy(360f).setDuration(1000).start();
            Handler handler = new Handler();
            Runnable code = () -> {
                block(false, false);
                CadastrarReceita.preload = false;
                Intent myIntent = new Intent(getApplicationContext(), CadastrarReceita.class);
                startActivity(myIntent);
                this.overridePendingTransition(0, 0);
            };
            handler.postDelayed(code, 1000);
        });

        pesquisar.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Pesquisar.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
        });

        perfil.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Perfil.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
        });

        notificacao.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Notificacao.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
        });
    }

    private static void block(boolean block, boolean invisible) {
        View[] view = new View[]{home, pesquisar, perfil, notificacao, criar};
        for (View v : view)
            v.setEnabled(!block);

        if(!invisible) return;
        sugestoes.setVisibility(!block ? View.VISIBLE : View.INVISIBLE);
        melhores.setVisibility(!block ? View.VISIBLE : View.INVISIBLE);
        principal.setVisibility(!block ? View.VISIBLE : View.INVISIBLE);
        txtmelhores.setVisibility(!block ? View.VISIBLE : View.INVISIBLE);
        txtdrinks.setVisibility(!block ? View.VISIBLE : View.INVISIBLE);
    }

    public void initialize() {
        ActivityTracker.addActivity(this);
        db = new Database(this);
        activity = Main.this;

        home = findViewById(R.id.main_btn_home);
        pesquisar = findViewById(R.id.main_btn_pesquisar);
        perfil = findViewById(R.id.main_btn_perfil);
        notificacao = findViewById(R.id.main_btn_notificacao);
        criar = findViewById(R.id.main_btn_criar);
        banner = findViewById(R.id.main_banner);
        categorias = findViewById(R.id.main_listview_01);
        melhores = findViewById(R.id.main_listview_02);
        principal = findViewById(R.id.main_listview_03);
        sugestoes = findViewById(R.id.main_listview_04);
        txtmelhores = findViewById(R.id.main_titulo_02);
        txtdrinks = findViewById(R.id.main_titulo_03);
        back = findViewById(R.id.main_btn_back);

        ignorarbanner = 0;

        ArrayList<Bitmap> banners = new ArrayList<>();
        ArrayList<Class> target = new ArrayList<>();

        banners.add(Utils.drawableToBitmap(this, R.drawable.saborear_banner_chatbot));
        banners.add(Utils.drawableToBitmap(this, R.drawable.saborear_banner_temperos));
        banners.add(Utils.drawableToBitmap(this, R.drawable.saborear_banner_nutricional));
        banners.add(Utils.drawableToBitmap(this, R.drawable.saborear_banner_pesquisa));
        target.add(Chatbot.class);
        target.add(PesquisarTemperos.class);
        target.add(GuiaIngredientes.class);
        target.add(PesquisaIngrediente.class);

        at = new BannerView(Main.this, banners, target);
        banner.setAdapter(at);

        Random random = new Random();

        ArrayList<String> nomes = new ArrayList<>();
        ArrayList<String> categ = new ArrayList<>();
        ArrayList<Bitmap> imagens = new ArrayList<>();

        categ.add("7"); categ.add("0"); categ.add("6");
        categ.add("3"); categ.add("8"); categ.add("4");
        categ.add("10"); categ.add("107"); categ.add("5");
        categ.add("2"); categ.add("1"); categ.add("106");
        categ.add("9");

        cat = ""+categ.get(random.nextInt(categ.size()));

        nomes.add("Bebidas"); nomes.add("Bolos"); nomes.add("Caldos");
        nomes.add("Doces"); nomes.add("Drinques"); nomes.add("Massas");
        nomes.add("Pães"); nomes.add("Refeição"); nomes.add("Saladas");
        nomes.add("Salgados"); nomes.add("Tortas"); nomes.add("Veganos");
        nomes.add("Petiscos");

        imagens.add(Utils.drawableToBitmap(this, R.drawable.bebidas));
        imagens.add(Utils.drawableToBitmap(this, R.drawable.bolos));
        imagens.add(Utils.drawableToBitmap(this, R.drawable.caldos));
        imagens.add(Utils.drawableToBitmap(this, R.drawable.doces));
        imagens.add(Utils.drawableToBitmap(this, R.drawable.drinks));
        imagens.add(Utils.drawableToBitmap(this, R.drawable.massas));
        imagens.add(Utils.drawableToBitmap(this, R.drawable.paes));
        imagens.add(Utils.drawableToBitmap(this, R.drawable.refeicoes));
        imagens.add(Utils.drawableToBitmap(this, R.drawable.saladas));
        imagens.add(Utils.drawableToBitmap(this, R.drawable.salgados));
        imagens.add(Utils.drawableToBitmap(this, R.drawable.tortas));
        imagens.add(Utils.drawableToBitmap(this, R.drawable.veganos));
        imagens.add(Utils.drawableToBitmap(this, R.drawable.petiscos));

        ad = new CategoriaView(Main.this, nomes, categ, imagens);
        categorias.setAdapter(ad);

        ArrayList<View> ignore = new ArrayList<>();
        ignore.add(banner);
        Utils.blackMode(findViewById(R.id.main_layout), ignore, null);
    }


    public static void atualizar() {
        mg = new ManageReceita(StorageDatabase.receitas);
        MinireceitaView ab = new MinireceitaView(activity, mg.getByIDCATEG(cat).ordenarByNota().inverter().getReceitas());
        melhores.setAdapter(ab);

        mg = new ManageReceita(StorageDatabase.receitas);
        MinireceitaView ad = new MinireceitaView(activity, mg.getByIDCATEG(cat).randomizar().getReceitas());
        sugestoes.setAdapter(ad);

        mg = new ManageReceita(StorageDatabase.receitas);
        GrandereceitaView ac = new GrandereceitaView(activity, mg.getByIDCATEG(cat).ordenarByNota().inverter().limitar(1).getReceitas());
        principal.setAdapter(ac);
    }

    protected void onResume() {
        if(firstTime) return;

        super.onResume();
        atualizar();
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - time < 3000) {
            finishAffinity();
        } else {
            Toast.makeText(getApplicationContext(), "Clique novamente para sair", Toast.LENGTH_SHORT).show();
            time = System.currentTimeMillis();
        }
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
