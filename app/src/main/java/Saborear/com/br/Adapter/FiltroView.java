package Saborear.com.br.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

import Saborear.com.br.Pesquisar;
import Saborear.com.br.R;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Utils.Utils;

public class FiltroView extends PagerAdapter {

    private final Activity context;
    private final ArrayList<String> nomes;
    private final Bitmap full, vazio;

    public FiltroView(Activity context, ArrayList<String> nomes) {
        this.context = context;
        this.nomes = nomes;

        if(InternalDatabase.isDarkmode()) {
            this.full = Utils.drawableToBitmap(context, R.drawable.saborear_campo_03);
            this.vazio = Utils.drawableToBitmap(context, R.drawable.saborear_dark_campo_02);
        } else {
            this.full = Utils.drawableToBitmap(context, R.drawable.saborear_campo_03);
            this.vazio = Utils.drawableToBitmap(context, R.drawable.saborear_campo_02);
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.subfiltro, container, false);

        ImageView img = rowView.findViewById(R.id.subfiltro_filtro);
        TextView nome = rowView.findViewById(R.id.subfiltro_nome);
        nome.setText(nomes.get(position));

        String receita = nomes.get(position);
        if(Pesquisar.contains(receita)) {
            img.setImageBitmap(full);
            nome.setTextColor((Color.parseColor("#FFFFFF")));
            Pesquisar.delImg(Pesquisar.getImageByString(receita), receita, false);
            Pesquisar.addImg(img, receita, false);
        } else {
            nome.setTextColor((Color.parseColor("#1BAB4A")));
            img.setImageBitmap(vazio);
        }

        rowView.setOnClickListener(v -> {
            if(Pesquisar.carregando) return;
            if(Pesquisar.contains(receita)) {
                Pesquisar.delImg(img, receita, true);
                img.setImageBitmap(vazio);
                nome.setTextColor((Color.parseColor("#1BAB4A")));
            } else {
                Pesquisar.addImg(img, receita, true);
                img.setImageBitmap(full);
                nome.setTextColor((Color.parseColor("#FFFFFF")));
            }
        });

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        ignore.add(img); ignore.add(nome);

        Utils.blackMode((ViewGroup) rowView, ignore, inverted);

        container.addView(rowView);

        return rowView;
    }

    @Override
    public int getCount() {
        return nomes.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public float getPageWidth(int position) {
        return 0.45f;
    }
}
