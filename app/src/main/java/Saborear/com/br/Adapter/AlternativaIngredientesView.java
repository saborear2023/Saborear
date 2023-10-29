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

import Saborear.com.br.Chatingredientes;
import Saborear.com.br.R;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Utils.Utils;

public class AlternativaIngredientesView extends PagerAdapter {
    private final Activity context;
    private final Database db;
    private final ArrayList<String> nomes, acoes;
    private final Bitmap full, vazio;
    private ArrayList<String> storage, storagenomes;
    private ArrayList<ImageView> selecionadosimg;
    private ArrayList<TextView> selecionadostext;
    private long time;
    private Boolean add;
    public AlternativaIngredientesView(Activity context, ArrayList<String> nomes, ArrayList<String> acoes, Boolean add) {
        this.context = context;
        this.nomes = nomes;

        this.db = new Database(context);
        this.storage = new ArrayList<>();
        this.storagenomes = new ArrayList<>();
        this.selecionadosimg = new ArrayList<>();
        this.selecionadostext = new ArrayList<>();
        this.acoes = acoes;

        this.add = add;

        time = 0;

        this.full = Utils.drawableToBitmap(context, R.drawable.saborear_campo_09);
        this.vazio = Utils.drawableToBitmap(context, R.drawable.saborear_campo_10);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.subfiltro, container, false);

        ImageView img = rowView.findViewById(R.id.subfiltro_filtro);
        TextView nome = rowView.findViewById(R.id.subfiltro_nome);
        nome.setText(Utils.limit(nomes.get(position), 10));

        img.setImageBitmap(vazio);

        rowView.setOnClickListener(v -> {

            if(Chatingredientes.blocked) return;

            if (!selecionadosimg.contains(img)) {
                clear();
                storage.add(acoes.get(position));
            } else
                storage.remove(acoes.get(position));

            if(!selecionadosimg.contains(img)) {
                nome.setTextColor(Color.parseColor("#FFFFFF"));
                img.setImageBitmap(full);

                storagenomes.add(nomes.get(position));
                selecionadosimg.add(img);
                selecionadostext.add(nome);
            } else {
                nome.setTextColor(Color.parseColor("#000000"));
                img.setImageBitmap(vazio);

                storagenomes.remove(nomes.get(position));
                selecionadosimg.remove(img);
                selecionadostext.remove(nome);
            }

            if(add) Chatingredientes.insert.setText(getResposta());
        });

        ArrayList<View> ignore = new ArrayList<>();
        ignore.add(nome); ignore.add(img);
        Utils.blackMode((ViewGroup) rowView, ignore, null);

        container.addView(rowView);

        return rowView;
    }

    private void clear() {
        selecionadostext.forEach(i -> i.setTextColor(Color.parseColor("#000000")));
        selecionadostext = new ArrayList<>();
        selecionadosimg.forEach(i -> i.setImageBitmap(vazio));
        selecionadosimg = new ArrayList<>();
        storage = new ArrayList<>();
        storagenomes = new ArrayList<>();
    }
    public ArrayList<String> getStorage() {
        return storage;
    }

    public String getResposta() {
        if(storagenomes.size() == 0) return "";

        String resposta = storagenomes.get(0);
        for (int i = 1; i < storagenomes.size(); i++) {
            if(i + 1 < storagenomes.size())
                resposta +=", ";
            else
                resposta += " e ";
            resposta += storagenomes.get(i).toLowerCase();
        }
        return resposta;
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
