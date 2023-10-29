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

import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Main;
import Saborear.com.br.R;
import Saborear.com.br.Utils.Utils;

public class CategoriaView extends PagerAdapter {

    private final Activity context;
    private final ArrayList<String> nomes, categorias;
    private final ArrayList<Bitmap> imagens;
    public CategoriaView(Activity context, ArrayList<String> nomes, ArrayList<String> categorias, ArrayList<Bitmap> imagens) {
        this.context = context;
        this.nomes = nomes;
        this.categorias = categorias;
        this.imagens = imagens;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.subcategoria, container, false);
        ImageView imagem = rowView.findViewById(R.id.subcategoria_imagem);
        TextView nome = rowView.findViewById(R.id.subcategoria_texto);

        imagem.setImageBitmap(Utils.shapeBitmap(imagens.get(position), 20));
        nome.setText(nomes.get(position));

        rowView.setOnClickListener(v -> {
            Main.cat = categorias.get(position);
            Main.atualizar();
        });

        if(InternalDatabase.isDarkmode()) nome.setTextColor(Color.parseColor("#FFFFFF"));

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
        return 0.30f;
    }
}