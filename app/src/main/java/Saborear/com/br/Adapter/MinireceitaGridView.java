package Saborear.com.br.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.ImageDatabase;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.R;
import Saborear.com.br.Receita;
import Saborear.com.br.Manage.Manage;
import Saborear.com.br.Utils.Utils;

public class MinireceitaGridView extends BaseAdapter {

    private final Database db;
    private final Activity context;
    private final ArrayList<classeReceita> receitas;
    public MinireceitaGridView(Activity context, ArrayList<classeReceita> receitas) {
        this.db = new Database(context);
        this.context = context;
        this.receitas = receitas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.subminireceita, container, false);

        ImageView imagem = rowView.findViewById(R.id.subminireceita_imagem);
        TextView nome = rowView.findViewById(R.id.subminireceita_texto);
        ProgressBar carregar = rowView.findViewById(R.id.subminireceita_carregar);

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
        inverted.add(nome); ignore.add(imagem);
        Utils.blackMode((ViewGroup) rowView, ignore, inverted);
        if(InternalDatabase.isDarkmode()) nome.setTextColor(Color.parseColor("#FFFFFF"));

        return rowView;
    }

    @Override
    public int getCount() {
        return receitas.size();
    }

    @Override
    public Object getItem(int i) {
        return receitas.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}