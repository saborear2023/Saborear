package Saborear.com.br.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

import Saborear.com.br.CadastrarReceita;
import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Database.ImageDatabase;
import Saborear.com.br.MinhasReceitas;
import Saborear.com.br.R;
import Saborear.com.br.Receita;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.FireDatabase;
import Saborear.com.br.Manage.Manage;
import Saborear.com.br.Utils.Utils;

public class MinhaView extends ArrayAdapter<classeReceita> {

    private final Activity context;
    private final ArrayList<classeReceita> receitas;
    private final Database db;

    public MinhaView(Activity context, ArrayList<classeReceita> receitas) {
        super(context, R.layout.subreceita, receitas);

        db = new Database(context);
        this.context = context;
        this.receitas = receitas;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView = inflater.inflate(R.layout.subminha, null, true);

        TextView nome = rowView.findViewById(R.id.subminha_nome);
        TextView avaliacao = rowView.findViewById(R.id.subminha_avaliacao);
        TextView views = rowView.findViewById(R.id.subminha_visualizacao);
        TextView comentario = rowView.findViewById(R.id.subminha_comentarios);
        ImageView foto = rowView.findViewById(R.id.subminha_imagem);

        ImageButton visibilidade = rowView.findViewById(R.id.subminha_btn_visibilidade);
        ImageButton editar = rowView.findViewById(R.id.subminha_btn_editar);
        ImageButton deletar = rowView.findViewById(R.id.subminha_btn_deletar);

        ProgressBar loading = rowView.findViewById(R.id.subminha_loading);

        classeReceita receita = receitas.get(position);

        if(!receita.getPublica())
            visibilidade.setImageBitmap(Utils.drawableToBitmap(context, R.drawable.saborear_icon_invisivel));

        visibilidade.setOnClickListener(v -> {
            String sql = "update receita set visibilidade='@visi' where id_receita='@id';".replace("@id", receita.getIdreceita());
            Boolean privada = !receita.getPublica();
            receita.setPublica(!receita.getPublica());
            sql = sql.replace("@visi", receita.getPublica() ? "Pública" : "Privada");

            MinhasReceitas.block(true);
            db.requestScript(sql, data -> {
                if(privada)
                    visibilidade.setImageDrawable(context.getDrawable(R.drawable.saborear_icon_olho));
                else
                    visibilidade.setImageDrawable(context.getDrawable(R.drawable.saborear_icon_invisivel));

                Manage.findReceita(receita).setPublica(privada);
                MinhasReceitas.atualizar();

                FireDatabase.updateReceita(receita.getIdreceita(), "atualizar");

                MinhasReceitas.block(false);
            });


        });

        editar.setOnClickListener(v -> {
            CadastrarReceita.preload = true;
            CadastrarReceita.receita = new classeReceita(receita);
            Intent myIntent = new Intent(context, CadastrarReceita.class);
            context.startActivityForResult(myIntent, 1);
            context.overridePendingTransition(0, 0);
        });

        deletar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Você realmente deseja deletar a receita?")
                    .setTitle("Saborear")
                    .setNegativeButton("Cancelar", (dialog, id) -> {})
                    .setPositiveButton("Sim", (dialog, id) -> {
                       MinhasReceitas.block(true);
                        db.requestScript(receita.scriptDeletar(), data -> {

                            Manage.deletarReceita(receita.getIdreceita());

                            Toast.makeText(context, "Receita deletada", Toast.LENGTH_SHORT).show();
                            MinhasReceitas.atualizar();

                            FireDatabase.updateReceita(receita.getIdreceita(), "deletar");

                            MinhasReceitas.block(false);
                        });
                    });
            builder.create().show();
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
        views.setText(receita.getViews()+"");
        comentario.setText(receita.getComentarios()+"");

        double value = receita.getNota();
        avaliacao.setText(Double.toString(value));

        ImageView[] estrelas = new ImageView[]{rowView.findViewById(R.id.subminha_estrela_01), rowView.findViewById(R.id.subminha_estrela_02), rowView.findViewById(R.id.subminha_estrela_03)};
        if(value > 1)
            estrelas[0].setAlpha(1f);
        if(value > 2)
            estrelas[1].setAlpha(1f);
        if(value > 4)
            estrelas[2].setAlpha(1f);

        rowView.setOnClickListener(v -> {
            Receita.receita = new classeReceita(receita);
            Intent myIntent = new Intent(context, Receita.class);
            context.startActivityForResult(myIntent, 1);
            context.overridePendingTransition(0, 0);
        });

        ArrayList<View> ignore = new ArrayList<>();
        for (ImageView estrela : estrelas)
            ignore.add(estrela);
        ignore.add(foto);
        Utils.blackMode((ViewGroup) rowView, ignore, null);

        return rowView;
    }
}