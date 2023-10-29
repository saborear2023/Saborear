package Saborear.com.br.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Notificacao;
import Saborear.com.br.R;
import Saborear.com.br.Receita;
import Saborear.com.br.Manage.Manage;
import Saborear.com.br.Utils.Utils;

public class NotificacaoView extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> id, idreceita;

    private final HashMap<String, classeReceita> receitas;
    private final HashMap<String, String>  titulo, mensagem;
    private final HashMap<String, Bitmap> fotos;
    private final boolean enable;

    private final Database db;

    public NotificacaoView(Activity context, ArrayList<String> id, ArrayList<String> idreceita, HashMap<String, String> titulo, HashMap<String, String> mensagem, HashMap<String, classeReceita> receitas, HashMap<String, Bitmap> fotos, boolean enable) {
        super(context, R.layout.subreceita, id);

        db = new Database(context);
        this.context = context;
        this.id = id;
        this.idreceita = idreceita;
        this.receitas = receitas;
        this.fotos = fotos;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.enable = enable;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView = inflater.inflate(R.layout.subnotificacao, null, true);

        TextView titulo = rowView.findViewById(R.id.subnotificacao_titulo);
        TextView mensagem = rowView.findViewById(R.id.subnotificacao_texto);
        ImageView foto = rowView.findViewById(R.id.subnotificacao_icon);
        ImageButton deletar = rowView.findViewById(R.id.subnotificacao_btn_deletar);

        if(fotos != null)
             foto.setImageBitmap(fotos.get(id.get(position)));

        titulo.setText(this.titulo.get(id.get(position)));
        mensagem.setText(this.mensagem.get(id.get(position)));

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        ignore.add(foto); inverted.add(rowView);
        Utils.blackMode((ViewGroup) rowView, ignore, inverted);

        if(enable) {
            deletar.setOnClickListener(v -> {
                String sql = "update usuario_notificacao set excluido='true' where email='@email' and id_notificacao='@id'".replace("@id", "" + id.get(position));
                sql = InternalDatabase.convert(sql);
                db.requestScript(sql, data -> Notificacao.atualizar());
            });

            rowView.setOnClickListener(v -> {
                if(!receitas.containsKey(idreceita.get(position))) return;

                classeReceita receita = receitas.get(idreceita.get(position));
                if(receita.getIdreceita().equals("-1") || Manage.findReceita(receita) == null) {
                    Toast.makeText(context, "A receita não existe mais", Toast.LENGTH_SHORT).show();
                    return;
                }
                Receita.receita = new classeReceita(receitas.get(idreceita.get(position)));
                Intent myIntent = new Intent(context, Receita.class);
                context.startActivityForResult(myIntent, 1);
                context.overridePendingTransition(0, 0);
            });
        }

        return rowView;
    }
}
