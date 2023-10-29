package Saborear.com.br.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.ImageDatabase;
import Saborear.com.br.R;
import Saborear.com.br.Receita;
import Saborear.com.br.Manage.Manage;
import Saborear.com.br.Utils.Utils;
import Saborear.com.br.Validar;

public class ValidarView extends ArrayAdapter<classeReceita> {

    private final Activity context;
    private final ArrayList<classeReceita> receitas;
    private final Database db;

    public ValidarView(Activity context, ArrayList<classeReceita> receitas) {
        super(context, R.layout.subreceita, receitas);

        db = new Database(context);
        this.context = context;
        this.receitas = receitas;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView = inflater.inflate(R.layout.subvalidar, null, true);

        TextView nome = rowView.findViewById(R.id.subvalidar_nome);
        ImageView foto = rowView.findViewById(R.id.subvalidar_imagem);

        ImageButton aceitar = rowView.findViewById(R.id.subvalidar_aceitar);
        ImageButton negar = rowView.findViewById(R.id.subvalidar_negar);

        ProgressBar loading = rowView.findViewById(R.id.subvalidar_loading);

        classeReceita receita = receitas.get(position);

        aceitar.setOnClickListener(v -> {
            Validar.block(true);

            String idnotificacao = ""+new Random().nextInt(99901)+500;
            String not = Manage.criarNotificacao(idnotificacao, receita.getIdreceita(), receita.getNome(), "Sua receita foi aprovada e publicada.");
            String insert = Manage.insertNotificacao(idnotificacao, receita.getCriador().getEmail());

            db.requestScript(not+insert+"update receita set validar='false' where id_receita='@id';".replace("@id", receita.getIdreceita()), data -> {
                Manage.findReceita(receita).setValidar(false);
                Validar.block(false);
                Validar.atualizar();
            });
        });

        negar.setOnClickListener(v -> {
            Validar.block(true);

            String idnotificacao = ""+new Random().nextInt(99901)+500;
            String not = Manage.criarNotificacao(idnotificacao, "-1", receita.getNome(), "Sua receita não foi aprovada.");
            String insert = Manage.insertNotificacao(idnotificacao, receita.getCriador().getEmail());

            Log.i("SaborearDatabase", not+insert);

            db.requestScript(not+insert+receita.scriptDeletar(), data -> {
                Manage.deletarReceita(receita.getIdreceita());
                Validar.block(false);
                Validar.atualizar();
            });
        });


        if(receita.getImagem() == null){
            loading.setVisibility(View.VISIBLE);
            db.requestScript("select imagem from receita where id_receita='@id';".replace("@id", receita.getIdreceita()), end -> {
                loading.setVisibility(View.INVISIBLE);
                receita.setImagem(ImageDatabase.decode(end.get(0).get("imagem")));
                foto.setImageBitmap(Utils.shapeBitmap(receita.getImagem(), 20));
                Manage.findReceita(receita.getIdreceita()).setImagem(receita.getImagem());
            });
        } else
            foto.setImageBitmap(Utils.shapeBitmap(receita.getImagem(), 20));

        nome.setText(Utils.limit(receita.getNome(), 15));

        rowView.setOnClickListener(v -> {
            Receita.receita = new classeReceita(receita);
            Intent myIntent = new Intent(context, Receita.class);
            context.startActivityForResult(myIntent, 1);
            context.overridePendingTransition(0, 0);
        });

        ArrayList<View> ignore = new ArrayList<>();
        ignore.add(foto);
        Utils.blackMode((ViewGroup) rowView, ignore, null);

        return rowView;
    }
}
