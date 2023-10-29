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

public class OrdenarView extends PagerAdapter {

    private final Activity context;
    private final ArrayList<String> nomes;
    private final Bitmap full, vazio;
    public OrdenarView(Activity context, ArrayList<String> nomes) {
        this.context = context;
        this.nomes = nomes;

        if(InternalDatabase.isDarkmode()) {
            this.full = Utils.drawableToBitmap(context, R.drawable.saborear_campo_05);
            this.vazio = Utils.drawableToBitmap(context, R.drawable.saborear_dark_campo_03);
        } else {
            this.full = Utils.drawableToBitmap(context, R.drawable.saborear_campo_05);
            this.vazio = Utils.drawableToBitmap(context, R.drawable.saborear_campo_06);
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

        if(Pesquisar.sel == position) {
            img.setImageBitmap(full);
            nome.setTextColor((Color.parseColor("#FFFFFF")));
        } else {
            img.setImageBitmap(vazio);
            nome.setTextColor((Color.parseColor("#FD9F1C")));
        }

        rowView.setOnClickListener(v -> {
            if(Pesquisar.carregando) return;
            if(Pesquisar.sel != position) {
                if(Pesquisar.sel != -1) {
                    Pesquisar.view.setImageBitmap(vazio);
                    Pesquisar.txt.setTextColor((Color.parseColor("#FD9F1C")));

                    Pesquisar.txt.setText(Pesquisar.txt.getText().toString().replace("-", ""));
                    Pesquisar.txt.setText(Pesquisar.txt.getText().toString().replace("+", ""));
                }
                img.setImageBitmap(full);
                nome.setTextColor((Color.parseColor("#FFFFFF")));
                Pesquisar.view = img;
                Pesquisar.sel = position;
                Pesquisar.txt = nome;

                Pesquisar.maior = true;
                if(position != 0) Pesquisar.txt.setText("+ "+nomes.get(position));
            } else {
                String aux = Pesquisar.txt.getText().toString();

                if(position != 0) {
                    if (!aux.contains("+") && !aux.contains("-")) {
                        Pesquisar.maior = true;
                        Pesquisar.txt.setText("+ " + nomes.get(position));
                    } else if (aux.contains("+")) {
                        Pesquisar.maior = false;
                        Pesquisar.txt.setText("- " + nomes.get(position));
                    } else {
                        img.setImageBitmap(vazio);
                        nome.setTextColor((Color.parseColor("#FD9F1C")));
                        Pesquisar.sel = -1;
                        Pesquisar.txt.setText(aux.replace("-", "").replace("+", ""));
                    }
                } else {
                    img.setImageBitmap(vazio);
                    nome.setTextColor((Color.parseColor("#FD9F1C")));
                    Pesquisar.sel = -1;
                }

            }
            Pesquisar.atualizar(false, Pesquisar.listView.getLastVisiblePosition());
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
        return 0.46f;
    }
}