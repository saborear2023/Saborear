package Saborear.com.br.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

import Saborear.com.br.Chatbot;
import Saborear.com.br.Classes.classeAlternativa;
import Saborear.com.br.R;
import Saborear.com.br.Utils.Utils;
import kotlin.Pair;

public class AlternativaView extends PagerAdapter {

    private final Activity context;
    private final ArrayList<classeAlternativa> alternativas;
    private final Bitmap full, vazio;
    private ArrayList<Pair<classeAlternativa, View>> selecionados;
    private boolean multiplos;

    private long time;
    public AlternativaView(Activity context, ArrayList<classeAlternativa> alternativas, boolean multiplos) {
        this.context = context;
        this.alternativas = alternativas;
        this.multiplos = multiplos;

        this.selecionados = new ArrayList<>();

        this.time = 0;

        this.full = Utils.drawableToBitmap(context, R.drawable.saborear_campo_09);
        this.vazio = Utils.drawableToBitmap(context, R.drawable.saborear_campo_10);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.subfiltro, container, false);

        classeAlternativa alternativa = alternativas.get(position);

        ImageView img = rowView.findViewById(R.id.subfiltro_filtro);
        TextView nome = rowView.findViewById(R.id.subfiltro_nome);
        nome.setText(Utils.limit(alternativa.getAlternativa(), 10));

        img.setImageBitmap(vazio);

        if(alternativa.isBlock())  img.setColorFilter(Color.parseColor("#F93A3A"));

        rowView.setOnClickListener(v -> {

            if(Chatbot.block) return;
            if(alternativa.isBlock()) {
                if (System.currentTimeMillis() - time > 3000) {
                    Toast.makeText(context, "Não há receitas com essa opção", Toast.LENGTH_SHORT).show();
                    time = System.currentTimeMillis();
                }
                return;
            }

            Boolean find = false;
            for (Pair<classeAlternativa, View> selecionado : selecionados) {
                if(selecionado.getFirst().getAlternativa().equals(alternativa.getAlternativa()))  find = true;
                if(selecionado.getFirst().getIdacao().equals("-1")) remover(selecionado.getFirst().getIdacao());
            }

            if(!multiplos || alternativa.getIdacao().equals("-1")) { clear(); }
            if(!find) selecionados.add(new Pair<>(alternativa, rowView));
            else if(multiplos) remover(alternativa.getIdacao());

            atualizar();

            Chatbot.insert.setText(getResposta());
        });

        ArrayList<View> ignore = new ArrayList<>();
        ignore.add(nome); ignore.add(img);
        Utils.blackMode((ViewGroup) rowView, ignore, null);

        container.addView(rowView);

        return rowView;
    }

    private void atualizar() {
        for (Pair<classeAlternativa, View> selecionado : selecionados) {
            View rowview = selecionado.getSecond();

            TextView nome = rowview.findViewById(R.id.subfiltro_nome);
            ImageView imagem = rowview.findViewById(R.id.subfiltro_filtro);

            nome.setTextColor(Color.parseColor("#FFFFFF"));
            imagem.setImageBitmap(full);
        }
    }

    private void clear() {
        for (Pair<classeAlternativa, View> selecionado : selecionados) {
           View rowview = selecionado.getSecond();

           TextView nome = rowview.findViewById(R.id.subfiltro_nome);
           ImageView imagem = rowview.findViewById(R.id.subfiltro_filtro);

           nome.setTextColor(Color.parseColor("#000000"));
           imagem.setImageBitmap(vazio);
        }
        selecionados = new ArrayList<>();
    }
    private void remover(String idacao) {
        for (int i = 0; i < selecionados.size(); i++) {
            Pair<classeAlternativa, View> selecionado = selecionados.get(i);

            if(!selecionado.getFirst().getIdacao().equals(idacao)) continue;

            View rowview = selecionado.getSecond();


            TextView nome = rowview.findViewById(R.id.subfiltro_nome);
            ImageView imagem = rowview.findViewById(R.id.subfiltro_filtro);

            nome.setTextColor(Color.parseColor("#000000"));
            imagem.setImageBitmap(vazio);

            if (Chatbot.subcategorias.contains(selecionado.getFirst().getIdacao())) {
                Chatbot.subcategorias.remove(selecionado.getFirst().getIdacao());
            }

            selecionados.remove(selecionado);
        }
    }
    public ArrayList<Pair<classeAlternativa, View>> getStorage() { return selecionados; }

    public String getResposta() {
        if(selecionados.size() == 0) return "";

        String resposta = selecionados.get(0).getFirst().getAlternativa();
        for (int i = 1; i < selecionados.size(); i++) {
            if(i + 1 < selecionados.size())
                resposta +=", ";
            else
                resposta += " e ";
            resposta += selecionados.get(i).getFirst().getAlternativa().toLowerCase();
        }
        return resposta;
    }
    public ArrayList<classeAlternativa> getSelecionados() {
        ArrayList<classeAlternativa> alternativas = new ArrayList<>();
        selecionados.forEach(selecionado -> alternativas.add(selecionado.getFirst()));
        return alternativas;
    }

    @Override
    public int getCount() {
        return alternativas.size();
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
