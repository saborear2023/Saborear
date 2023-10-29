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

import java.util.ArrayList;

import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.ImageDatabase;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Favoritas;
import Saborear.com.br.R;
import Saborear.com.br.Receita;
import Saborear.com.br.Manage.Manage;
import Saborear.com.br.Utils.Utils;

public class FavoritaView extends ArrayAdapter<classeReceita> {

    private final Database db;
    private final Activity context;
    private final ArrayList<classeReceita> receitas;

    public FavoritaView(Activity context, ArrayList<classeReceita> receitas) {
        super(context, R.layout.subreceita, receitas);

        db = new Database(context);
        this.context = context;
        this.receitas = receitas;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView = inflater.inflate(R.layout.subreceita, null, true);

        TextView nome = rowView.findViewById(R.id.subfavorita_nome);
        TextView avaliacao = rowView.findViewById(R.id.subfavorita_avaliacao);
        TextView views = rowView.findViewById(R.id.subfavorita_visualizacao);
        TextView comentario = rowView.findViewById(R.id.subfavorita_comentarios);
        ImageView foto = rowView.findViewById(R.id.subfavorita_imagem);
        ImageButton curtir = rowView.findViewById(R.id.subfavorita_btn_curtida);
        ProgressBar carregar = rowView.findViewById(R.id.subfavorita_carregar);

        classeReceita receita = receitas.get(position);

        curtir.setOnClickListener(v -> {
            String sql;
            if(receita.getReceitausuario().getCurtida()) {
                sql = "delete from salva where id_receita='@id' and email='@email';";
                curtir.setImageBitmap(Utils.drawableToBitmap(context, R.drawable.saborear_btn_favorito_vazio));
                receita.getReceitausuario().setCurtida(false);
            } else {
                sql = "insert into salva(email, id_receita) values('@email', '@id');";
                curtir.setImageBitmap(Utils.drawableToBitmap(context, R.drawable.saborear_icon_favorito));
                receita.getReceitausuario().setCurtida(true);
            }

            sql = sql.replace("@id", receita.getIdreceita());
            sql = InternalDatabase.convert(sql);

            Favoritas.block(true);
            db.requestScript(sql, data -> {
                Manage.findReceita(receita).update(new classeReceita(receita));
                Favoritas.atualizar();
                Favoritas.block(false);
            });
        });

        if(receita.getImagem() == null){
            carregar.setVisibility(View.VISIBLE);
            db.requestScript("select imagem from receita where id_receita='@id';".replace("@id", receita.getIdreceita()), end -> {
                carregar.setVisibility(View.INVISIBLE);
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

        ImageView[] estrelas = new ImageView[]{rowView.findViewById(R.id.subfavorita_estrela_01), rowView.findViewById(R.id.subfavorita_estrela_02), rowView.findViewById(R.id.subfavorita_estrela_03)};
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
        ignore.add(curtir);
        Utils.blackMode((ViewGroup) rowView, ignore, null);

        return rowView;
    }
}