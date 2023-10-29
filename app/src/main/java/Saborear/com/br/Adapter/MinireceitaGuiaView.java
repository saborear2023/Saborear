package Saborear.com.br.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.ImageDatabase;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.MaisReceitas;
import Saborear.com.br.R;
import Saborear.com.br.Receita;
import Saborear.com.br.Manage.Manage;
import Saborear.com.br.Utils.Utils;

public class MinireceitaGuiaView extends PagerAdapter {

    private final Database db;
    private final Activity context;
    private final ArrayList<classeReceita> all, receitas;
    public MinireceitaGuiaView(Activity context, ArrayList<classeReceita> receitas) {
        this.all = (ArrayList<classeReceita>) receitas.clone();

        if(receitas.size() > 5) receitas.subList(5, receitas.size()).clear();
        if(all.size() >= 3) receitas.add(new classeReceita());

        this.db = new Database(context);
        this.context = context;
        this.receitas = receitas;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.subminireceita, container, false);

        ImageView imagem = rowView.findViewById(R.id.subminireceita_imagem);
        TextView nome = rowView.findViewById(R.id.subminireceita_texto);
        ProgressBar carregar = rowView.findViewById(R.id.subminireceita_carregar);

        if(all.size() >= 3 && position + 1 == getCount()) {
            imagem.setImageBitmap(Utils.drawableToBitmap(context, R.drawable.saborear_btn_more));
            nome.setText("Ver todos");

            rowView.setOnClickListener(v -> {
                ArrayList<classeReceita> nreceitas = (ArrayList<classeReceita>) all.clone();

                MaisReceitas.receitas = nreceitas;
                Intent myIntent = new Intent(context, MaisReceitas.class);
                context.startActivityForResult(myIntent, 1);
                context.overridePendingTransition(0, 0);
            });

            ArrayList<View> inverted = new ArrayList<>();
            ArrayList<View> ignore = new ArrayList<>();
            inverted.add(nome); ignore.add(imagem);
            Utils.blackMode((ViewGroup) rowView, ignore, inverted);
            if(InternalDatabase.isDarkmode()) {
                rowView.setBackgroundColor(Color.parseColor("#7F7F7F"));
                nome.setTextColor(Color.parseColor("#FFFFFF"));
            }
            container.addView(rowView);

            return rowView;
        }

        classeReceita receita = receitas.get(position);

        if(receita.getImagem() == null){
            carregar.setVisibility(View.VISIBLE);
            db.requestScript("select imagem from receita where id_receita='@id';".replace("@id", receita.getIdreceita()), end -> {
                carregar.setVisibility(View.INVISIBLE);
                receita.setImagem(ImageDatabase.decode(end.get(0).get("imagem")));
                imagem.setImageBitmap(Utils.shapeBitmap(receita.getImagem(), 20));
                Manage.findReceita(receita.getIdreceita()).setImagem(receita.getImagem());
            });
        } else
            imagem.setImageBitmap(Utils.shapeBitmap(receita.getImagem(), 20));

        nome.setText(Utils.limit(receita.getNome(), 23));;

        ViewTreeObserver vto = nome.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int maxLines = 2;
                int lineCount = nome.getLineCount();

                if (lineCount > maxLines) {
                    int lineEndIndex = nome.getLayout().getLineEnd(maxLines - 1);
                    String text = nome.getText().subSequence(0, lineEndIndex - 3) + "...";
                    nome.setText(text);
                }

                nome.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });



        rowView.setOnClickListener(v -> {
            Receita.receita = new classeReceita(receita);
            Intent myIntent = new Intent(context, Receita.class);
            context.startActivityForResult(myIntent, 1);
            context.overridePendingTransition(0, 0);
        });

        ArrayList<View> inverted = new ArrayList<>();
        ArrayList<View> ignore = new ArrayList<>();
        ignore.add(nome); ignore.add(imagem);
        Utils.blackMode((ViewGroup) rowView, ignore, inverted);
        if(InternalDatabase.isDarkmode()) {
            rowView.setBackgroundColor(Color.parseColor("#7F7F7F"));
            nome.setTextColor(Color.parseColor("#FFFFFF"));
        }
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
        return 0.4f;
    }
}
