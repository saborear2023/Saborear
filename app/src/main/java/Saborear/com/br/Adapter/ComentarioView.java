package Saborear.com.br.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.HashMap;

import Saborear.com.br.Comentario;
import Saborear.com.br.R;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Receita;
import Saborear.com.br.Manage.Manage;
import Saborear.com.br.Utils.Utils;

public class ComentarioView extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> id;
    private final HashMap<String, String> nome, mensagem, horario, email;
    private final HashMap<String, Bitmap> imagens;
    private final Database db;

    public ComentarioView(Activity context, ArrayList<String> id, HashMap<String, String> email, HashMap<String, String> nome, HashMap<String, String> mensagem, HashMap<String, String> horario, HashMap<String, Bitmap> imagens) {
        super(context, R.layout.subcomentario, id);

        db = new Database(context);
        this.context = context;
        this.id = id;
        this.email = email;
        this.nome = nome;
        this.mensagem = mensagem;
        this.horario = horario;
        this.imagens = imagens;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView = inflater.inflate(R.layout.subcomentario, null, true);

        TextView nome = rowView.findViewById(R.id.subcomentario_nome);
        TextView ponto = rowView.findViewById(R.id.subcomentario_ponto);
        EditText mensagem = rowView.findViewById(R.id.subcomentario_texto);
        TextView horario = rowView.findViewById(R.id.subcomentario_data);
        ImageView foto = rowView.findViewById(R.id.subcomentario_foto);
        ImageButton deletar = rowView.findViewById(R.id.subcomentario_deletar);
        ImageButton editar = rowView.findViewById(R.id.subcomentario_editar);
        ImageButton salvar = rowView.findViewById(R.id.subcomentario_salvar);

        mensagem.setEnabled(false);

        String e = InternalDatabase.convert("@email");
        if(StorageDatabase.isAdmin || e.equals(this.email.get(id.get(position)))) {
            deletar.setVisibility(View.VISIBLE);
            deletar.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Deletar comentário?")
                        .setTitle("Saborear")
                        .setNegativeButton("Cancelar", (dialog, id) -> {

                        })
                        .setPositiveButton("Sim", (dialog, id) -> {
                            Comentario.block(true);
                            String sql = "delete from comentario where id_avaliacao = '@id';".replace("@id", this.id.get(position));
                            db.requestScript(sql, data -> {
                                Manage.findReceita(Receita.receita).setComentarios(Receita.receita.getComentarios()-1);
                                Comentario.atualizar();

                                Toast.makeText(context, "Comentário deletado", Toast.LENGTH_SHORT).show();
                            });
                        });
                builder.create().show();
            });
            editar.setVisibility(View.VISIBLE);
            editar.setOnClickListener(v -> {
                if (!mensagem.isEnabled()) {
                    if (!Comentario.editando) {
                        mensagem.setEnabled(true);
                        nome.setText("");
                        ponto.setText("");
                        horario.setText("Clique para editar...");
                        Comentario.editando = true;
                    } else {
                        Toast.makeText(context, "Você já está editando um comentário", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (mensagem.getText().toString().equals(this.mensagem.get(id.get(position)))) {
                        mensagem.setEnabled(false);
                        Comentario.editando = false;
                        nome.setText(Utils.limit(this.nome.get(id.get(position)), 10));
                        mensagem.setText(this.mensagem.get(id.get(position)));
                        horario.setText(Utils.diferencaData(this.horario.get(id.get(position))));
                    } else {
                        salvar.callOnClick();
                    }
                }
            });
            salvar.setVisibility(View.VISIBLE);
            salvar.setOnClickListener(v -> {
                if(!mensagem.isEnabled()) return;
                String txt = mensagem.getText().toString();
                if(txt.length() == 0) {
                    Toast.makeText(context, "Preencha o campo", Toast.LENGTH_SHORT).show();
                    return;
                } else if(txt.length() > 200) {
                    Toast.makeText(context, "Limite de caracteres atingido", Toast.LENGTH_SHORT).show();
                    return;
                } else if(txt.split("\\r?\\n").length > 10){
                    Toast.makeText(context, "Limite de 10 linhas atingido", Toast.LENGTH_SHORT).show();
                    return;
                }
                Comentario.editando = false;
                Comentario.block(true);
                mensagem.setEnabled(false);
                String sql = "update comentario set descricao='@desc' where id_avaliacao = '@id';".replace("@id", id.get(position));
                sql = sql.replace("@desc", txt);
                db.requestScript(sql, data -> {
                    Comentario.atualizar();
                    Toast.makeText(context, "Comentário atualizado", Toast.LENGTH_SHORT).show();
                });
            });
        }

        nome.setText(Utils.limit(this.nome.get(id.get(position)), 10));
        mensagem.setText(this.mensagem.get(id.get(position)));

        horario.setText(Utils.diferencaData(this.horario.get(id.get(position))));
        if((imagens.get(id.get(position)) != null))
            foto.setImageBitmap(Utils.shapeBitmap(imagens.get(id.get(position))));
        else
            foto.setImageBitmap(Utils.shapeBitmap(Utils.drawableToBitmap(context, R.drawable.saborear_sem_foto_01)));


        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        ignore.add(foto);
        Utils.blackMode((ViewGroup) rowView, ignore, inverted);

        return rowView;
    }
}