package Saborear.com.br.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Main;
import Saborear.com.br.R;
import Saborear.com.br.Manage.ManageReceita;

public class MainView extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> categ;
    private final Database db;
    private CategoriaView ad;
    private BannerView at;
    private int bannerposicao, ignorarbanner, aux;

    public MainView(Activity context, ArrayList<String> categ, CategoriaView ad, BannerView at) {
        super(context, R.layout.subreceita, categ);

        db = new Database(context);
        this.context = context;
        this.categ = categ;
        this.ad = ad;
        this.at = at;
        this.aux = Main.count;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater;
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView;

        inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.submain, null, true);

        ViewPager melhores = rowView.findViewById(R.id.submain_listview_01);
        ViewPager principal = rowView.findViewById(R.id.submain_listview_02);
        ViewPager sugestoes = rowView.findViewById(R.id.submain_listview_03);
        ViewPager categorias = rowView.findViewById(R.id.submain_listview_categorias);
        ViewPager banner = rowView.findViewById(R.id.submain_banner);

        String cat = categ.get(position);

        ManageReceita mg = new ManageReceita(StorageDatabase.receitas);
        MinireceitaView ab = new MinireceitaView(context, mg.getByIDCATEG(cat).ordenarByNota().inverter().getReceitas());
        melhores.setAdapter(ab);

        mg = new ManageReceita(StorageDatabase.receitas);
        MinireceitaView ag = new MinireceitaView(context, mg.getByIDCATEG(cat).randomizar().getReceitas());
        sugestoes.setAdapter(ag);

        mg = new ManageReceita(StorageDatabase.receitas);
        GrandereceitaView ac = new GrandereceitaView(context, mg.getByIDCATEG(cat).ordenarByNota().inverter().limitar(1).getReceitas());
        principal.setAdapter(ac);

        categorias.setAdapter(ad);
        banner.setAdapter(at);

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
                if(aux == Main.count)  handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);

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

        return rowView;
    }
}

