package Saborear.com.br.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import Saborear.com.br.Classes.classeUtilizacao;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.R;
import Saborear.com.br.Utils.Utils;

public class UtilizacaoView extends ArrayAdapter<classeUtilizacao> {

    private final Activity context;
    private ArrayList<classeUtilizacao> utilizacao;

    public UtilizacaoView(Activity context, ArrayList<classeUtilizacao> utilizacao) {
        super(context, R.layout.subreceita, utilizacao);
        this.context = context;
        this.utilizacao = utilizacao;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView  = inflater.inflate(R.layout.subutilizacao, null, true);;

        classeUtilizacao uti = utilizacao.get(position);

        TextView nome = rowView.findViewById(R.id.subutilizacao_nome);
        ImageView imagem = rowView.findViewById(R.id.subutilizacao_imagem);

        nome.setText(uti.getNome());
        imagem.setImageBitmap(uti.getImagem());

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        Utils.blackMode((ViewGroup) rowView, ignore, inverted);

        if(InternalDatabase.isDarkmode()) rowView.setBackgroundColor(Color.parseColor("#7F7F7F"));

        return rowView;
    }
}