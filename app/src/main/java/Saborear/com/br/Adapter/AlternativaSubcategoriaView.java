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

import Saborear.com.br.CadastrarReceita;
import Saborear.com.br.Chatbot;
import Saborear.com.br.Classes.classeAlternativa;
import Saborear.com.br.Classes.classePergunta;
import Saborear.com.br.Classes.classeReceitaSubcategoria;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.R;
import Saborear.com.br.Utils.Utils;
import kotlin.Pair;

public class AlternativaSubcategoriaView extends PagerAdapter {

    private final Activity context;
    private final ArrayList<classeAlternativa> alternativas;
    private final Bitmap full, vazio;
    private ArrayList<Pair<classeAlternativa, View>> selecionados;
    private boolean multiplos;
    private long time;

    public AlternativaSubcategoriaView(Activity context, ArrayList<classeAlternativa> alternativas, boolean multiplos) {
        this.context = context;
        this.alternativas = alternativas;
        this.multiplos = multiplos;

        this.selecionados = new ArrayList<>();

        this.time = 0;

        this.full = Utils.drawableToBitmap(context, R.drawable.saborear_campo_13);
        this.vazio = Utils.drawableToBitmap(context, R.drawable.saborear_campo_14);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.subfiltro, container, false);

        classeAlternativa alternativa = alternativas.get(position);

        ImageView img = rowView.findViewById(R.id.subfiltro_filtro);
        TextView nome = rowView.findViewById(R.id.subfiltro_nome);

        nome.setTextColor(Color.parseColor("#FF707070"));
        nome.setText(Utils.limit(alternativa.getAlternativa(), 10));

        img.setImageBitmap(vazio);

        if(alternativa.isSelected()) {
            selecionados.add(new Pair<>(alternativa, rowView));
            atualizar();
        }

        rowView.setOnClickListener(v -> {
            Boolean find = false;
            for (Pair<classeAlternativa, View> selecionado : selecionados) {
                if (selecionado.getFirst().getAlternativa().equals(alternativa.getAlternativa())) {
                    find = true;
                    break;
                }
            }

            alternativa.setSelect(!find);

            if (!multiplos) clear();

            if (!find) {
                if (!Chatbot.subcategorias.contains(alternativa.getIdacao()))
                    Chatbot.subcategorias.add(alternativa.getIdacao());
                selecionados.add(new Pair<>(alternativa, rowView));
            } else if(multiplos)  remover(alternativa.getIdacao());

            ArrayList<String> subcategorias = new ArrayList<>();
            for (classePergunta pergunta : Chatbot.perguntas) {
                for (classeAlternativa perguntaalternativa : pergunta.getAlternativas()) {
                    if(perguntaalternativa.isSelected()) subcategorias.add(perguntaalternativa.getIdacao());
                }
            }

            CadastrarReceita.receita.setSubcategoria(new classeReceitaSubcategoria(subcategorias));
            atualizar();
        });

        ArrayList<View> ignore = new ArrayList<>();
        ignore.add(nome); ignore.add(img);
        Utils.blackMode((ViewGroup) rowView, ignore, null);

        if(InternalDatabase.isDarkmode()) rowView.setBackgroundColor(Color.parseColor("#4C4C4C"));

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

            nome.setTextColor(Color.parseColor("#FF707070"));
            imagem.setImageBitmap(vazio);

            selecionado.getFirst().setSelect(false);

            if(Chatbot.subcategorias.contains(selecionado.getFirst().getIdacao())) Chatbot.subcategorias.remove(selecionado.getFirst().getIdacao());
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

            nome.setTextColor(Color.parseColor("#FF707070"));
            imagem.setImageBitmap(vazio);

            if (Chatbot.subcategorias.contains(selecionado.getFirst().getIdacao())) {
                Chatbot.subcategorias.remove(selecionado.getFirst().getIdacao());
            }

            selecionados.remove(selecionado);
        }
    }

    public ArrayList<Pair<classeAlternativa, View>> getStorage() { return selecionados; }

    public ArrayList<String> getResposta() {
        ArrayList<String> subcategorias = new ArrayList<>();
        for (Pair<classeAlternativa, View> selecionado : selecionados)
            subcategorias.add(selecionado.getFirst().getIdacao());
        return subcategorias;
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
        return 0.40f;
    }
}