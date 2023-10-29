package Saborear.com.br.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import Saborear.com.br.Chatingredientes;
import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.R;
import Saborear.com.br.Receita;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.Utils;

public class PerguntaIngredientesView extends ArrayAdapter<String> {
    private final Database db;
    private final Activity context;
    private final ArrayList<String> mensagem;
    private final ArrayList<Boolean> admin;
    private final Map<Integer, Bitmap> imagem;
    private final Map<Integer, String> target;
    private int size;
    public PerguntaIngredientesView(Activity context, ArrayList<Boolean> admin, Map<Integer, Bitmap> imagem, Map<Integer, String> target, ArrayList<String> mensagem) {
        super(context, R.layout.subreceita, mensagem);

        db = new Database(context);
        this.context = context;
        this.mensagem = mensagem;
        this.admin = admin;
        this.imagem = imagem;
        this.target = target;
        this.size = 0;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView;

        TextView texto;
        ImageView ponta;
        if(admin.get(position)) {
            rowView = inflater.inflate(R.layout.submensagem, null, true);
            texto = rowView.findViewById(R.id.submensagem_texto);
            ponta = rowView.findViewById(R.id.submensagem_campo_01);
        } else {
            rowView = inflater.inflate(R.layout.submensagemuser, null, true);
            texto = rowView.findViewById(R.id.submensagemuser_texto);
            ponta = rowView.findViewById(R.id.submensagemuser_campo_01);
        }



        if(imagem.get(position) != null) {
            rowView = inflater.inflate(R.layout.submensagemfoto, null, true);
            ponta = rowView.findViewById(R.id.submensagemfoto_campo_01);
        }

        if(position > 0 && admin.get(position-1) && admin.get(position))
            ponta.setVisibility(View.INVISIBLE);

        if(target.containsKey(position))
            rowView.setOnClickListener(v -> {
                Toast.makeText(context, "Abrindo...", Toast.LENGTH_SHORT).show();
                db.requestScript(StorageDatabase.getScript()+"where id_receita='@id';".replace("@id", target.get(position)), data -> {
                    Receita.receita = new classeReceita(data.get(0));
                    Intent myIntent = new Intent(context, Receita.class);
                    context.startActivityForResult(myIntent, 1);
                    context.overridePendingTransition(0, 0);
                });
            });

        if(imagem.get(position) != null) {
            ImageView img = rowView.findViewById(R.id.submensagemfoto_imagem);
            img.setImageBitmap(imagem.get(position));

            return rowView;
        }

        if(position != mensagem.size()-1 || !admin.get(position))
            texto.setText(mensagem.get(position));
        else {
            texto.setText("Escrevendo");
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (position != mensagem.size() - 1) {
                        texto.setText(mensagem.get(position));
                    } else {
                        if (!Chatingredientes.blocked) {
                            texto.setText(mensagem.get(position));
                            Chatingredientes.chat.setSelection(position);
                        } else {
                            if (texto.getText().length() < 13)
                                texto.setText(texto.getText() + ".");
                            else
                                texto.setText("Escrevendo");
                            handler.postDelayed(this, 500);
                        }
                    }

                }
            };
            handler.postDelayed(runnable, 0);
        }

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        if(!admin.get(position)) inverted.add(texto);
        Utils.blackMode((ViewGroup) rowView, ignore, inverted);

        size += rowView.getHeight();
        return rowView;
    }

    public int getHeight() {
        return size;
    }
}