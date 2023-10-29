package Saborear.com.br.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import Saborear.com.br.Classes.classeMensagem;
import Saborear.com.br.Classes.classePergunta;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.ImageDatabase;
import Saborear.com.br.R;
import Saborear.com.br.Manage.Manage;
import Saborear.com.br.Utils.Utils;

public class PerguntaView extends ArrayAdapter<classeMensagem> {

    private final Database db;
    private final Activity context;
    private final ArrayList<classeMensagem> mensagens;
    private int size;
    public PerguntaView(Activity context, ArrayList<classeMensagem> mensagens) {
        super(context, R.layout.subreceita, mensagens);

        db = new Database(context);
        this.context = context;
        this.mensagens = mensagens;
        this.size = 0;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView;

        classeMensagem mensagem = mensagens.get(position);

        TextView texto;
        ImageView ponta;

        if(mensagem.isAdmin()) {
            rowView = inflater.inflate(R.layout.submensagem, null, true);
            texto = rowView.findViewById(R.id.submensagem_texto);
            ponta = rowView.findViewById(R.id.submensagem_campo_01);
        } else {
            rowView = inflater.inflate(R.layout.submensagemuser, null, true);
            texto = rowView.findViewById(R.id.submensagemuser_texto);
            ponta = rowView.findViewById(R.id.submensagemuser_campo_01);
        }

        if(mensagem.hasImage()) {
            rowView = inflater.inflate(R.layout.submensagemfoto, null, true);
            ponta = rowView.findViewById(R.id.submensagemfoto_campo_01);
        }

        if(mensagem.hasPergunta()) {
            rowView = inflater.inflate(R.layout.submensagemsubcategoria, null, true);
            ponta = rowView.findViewById(R.id.submensagemsubcategoria_ponta);
            TextView msg = rowView.findViewById(R.id.submensagemsubcategoria_texto);

            if(position > 0 && mensagem.isAdmin() && mensagens.get(position-1).isAdmin())  ponta.setVisibility(View.INVISIBLE);

            msg.setText(mensagem.getMensagem());

            classePergunta pergunta = mensagem.getPergunta();

            ViewPager listview = rowView.findViewById(R.id.submensagemsubcategoria_listview);

            AlternativaSubcategoriaView adapterView = new AlternativaSubcategoriaView(context, pergunta.getAlternativasReference(), pergunta.isMultiplos());
            listview.setAdapter(adapterView);

            ArrayList<View> ignore = new ArrayList<>();
            ArrayList<View> inverted = new ArrayList<>();
            if(!mensagem.isAdmin()) inverted.add(texto);
            Utils.blackMode((ViewGroup) rowView, ignore, inverted);

            return rowView;
        }

        if(position > 0 && mensagem.isAdmin() && mensagens.get(position-1).isAdmin()) ponta.setVisibility(View.INVISIBLE);

        rowView.setOnClickListener(v -> mensagem.openTarget(context));


        if(mensagem.hasImage() && mensagem.hasTarget()) {
            ProgressBar carregar = rowView.findViewById(R.id.submensagemfoto_carregar);
            ImageView imagem = rowView.findViewById(R.id.submensagemfoto_imagem);

            if(mensagem.getTarget().getImagem() == null) {
                carregar.setVisibility(View.VISIBLE);
                db.requestScript("select imagem from receita where id_receita='@id';".replace("@id", mensagem.getTarget().getIdreceita()), end -> {
                    carregar.setVisibility(View.INVISIBLE);
                    mensagem.getTarget().setImagem(ImageDatabase.decode(end.get(0).get("imagem")));
                    imagem.setImageBitmap(Utils.shapeBitmap(mensagem.getTarget().getImagem(), 20));
                    Manage.findReceita(mensagem.getTarget().getIdreceita()).setImagem(mensagem.getTarget().getImagem());
                });
            } else imagem.setImageBitmap(Utils.shapeBitmap(mensagem.getTarget().getImagem(), 20));
            return rowView;
        }

        if(mensagem.getAdmin() && mensagem.getMensagem().contains("Digitando")) {
            Handler handler = new Handler();
            Runnable runnableCode = new Runnable() {
                @Override
                public void run() {
                    if(mensagem.getMensagem().contains("....")) mensagem.setMensagem("Digitando");
                    else mensagem.setMensagem(mensagem.getMensagem()+".");

                    texto.setText(mensagem.getMensagem());
                    handler.postDelayed(this, 500);
                }
            };
            handler.postDelayed(runnableCode, 0);

        } else texto.setText(mensagem.getMensagem());



        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        if(!mensagem.isAdmin()) inverted.add(texto);
        Utils.blackMode((ViewGroup) rowView, ignore, inverted);

        size += rowView.getHeight();
        return rowView;
    }

    public int getHeight() {
        return size;
    }
}

