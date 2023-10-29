package Saborear.com.br.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import Saborear.com.br.CadastrarTempero;
import Saborear.com.br.Classes.classeUtilizacao;
import Saborear.com.br.R;
import Saborear.com.br.Database.Database;

public class CadastroUtilizacaoView extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> nome;
    private final Database db;
    public CadastroUtilizacaoView(Activity context, ArrayList<String> nome) {
        super(context, R.layout.subreceita, nome);

        db = new Database(context);
        this.context = context;
        this.nome = nome;

    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView = inflater.inflate(R.layout.subcadastroutilizacao, null, true);

        TextView texto = rowView.findViewById(R.id.subcadastroutilizacao_texto);

        texto.setText(nome.get(position));
        rowView.setOnClickListener(v -> {
            for (classeUtilizacao util : CadastrarTempero.utilizacoes) {
                if(util.getNome().equals(nome.get(position))) {
                    CadastrarTempero.utilizacoes.remove(util);
                    break;
                }
            }

            ArrayAdapter<String> adapter = (ArrayAdapter<String>) CadastrarTempero.spinner.getAdapter();
            adapter.add(nome.get(position));
            CadastrarTempero.spinner.setAdapter(adapter);
            CadastrarTempero.atualizarUsos();
        });

        return rowView;
    }
}