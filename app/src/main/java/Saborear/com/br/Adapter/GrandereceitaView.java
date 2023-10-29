package Saborear.com.br.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.ImageDatabase;
import Saborear.com.br.R;
import Saborear.com.br.Receita;
import Saborear.com.br.Manage.Manage;
import Saborear.com.br.Utils.Utils;

public class GrandereceitaView extends PagerAdapter {

    private final Database db;
    private final Activity context;
    private final ArrayList<classeReceita> receitas;
    public GrandereceitaView(Activity context, ArrayList<classeReceita> receitas) {
        this.db = new Database(context);
        this.context = context;
        this.receitas = receitas;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.subreceitagrande, container, false);

        ImageView imagem = rowView.findViewById(R.id.subreceitagrande_imagem);
        ImageView campo01 = rowView.findViewById(R.id.subreceitagrande_campo_01);
        ImageView campo02 = rowView.findViewById(R.id.subreceitagrande_campo_02);
        TextView nome = rowView.findViewById(R.id.subreceitagrande_nome);
        TextView nota = rowView.findViewById(R.id.subreceitagrande_nota);
        ProgressBar carregar = rowView.findViewById(R.id.subreceitagrande_carregar);

        classeReceita receita = receitas.get(position);

        if(receita.getImagem() == null) {
            carregar.setVisibility(View.VISIBLE);
            db.requestScript("select imagem from receita where id_receita='@id';".replace("@id", receita.getIdreceita()), end -> {
                carregar.setVisibility(View.INVISIBLE);
                receita.setImagem(ImageDatabase.decode(end.get(0).get("imagem")));
                imagem.setImageBitmap(Utils.shapeBitmap(receita.getImagem(), 20));
                Manage.findReceita(receita.getIdreceita()).setImagem(receita.getImagem());
            });
        } else
            imagem.setImageBitmap(Utils.shapeBitmap(receita.getImagem(), 20));

        nome.setText(receita.getNome());
        nota.setText(receita.getNota()+"");

        campo01.setColorFilter(Color.parseColor("#1BAB4A"));
        campo02.setColorFilter(Color.parseColor("#1BAB4A"));

        rowView.setOnClickListener(v -> {
            Receita.receita = new classeReceita(receita);
            Intent myIntent = new Intent(context, Receita.class);
            context.startActivityForResult(myIntent, 1);
            context.overridePendingTransition(0, 0);
        });

        container.addView(rowView);
        return rowView;
    }

    @Override
    public int getCount() {
        return receitas.size();
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
        return 1f;
    }
}