package Saborear.com.br.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

import Saborear.com.br.Classes.classeIngrediente;
import Saborear.com.br.Classes.classeMarket;
import Saborear.com.br.Database.Database;
import Saborear.com.br.R;
import Saborear.com.br.Utils.Utils;

public class MiniMarketView extends ArrayAdapter<classeMarket> {

    private final Database db;
    private final Activity context;
    private classeIngrediente ingrediente;
    private ArrayList<classeMarket> markets;
    public MiniMarketView(Activity context, classeIngrediente ingrediente, ArrayList<classeMarket> markets) {
        super(context, R.layout.subreceita, markets);

        this.db = new Database(context);
        this.context = context;
        this.ingrediente = ingrediente;
        this.markets = markets;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater;
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView;

        inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.subminimarket, null, true);

        TextView nome = rowView.findViewById(R.id.subminimarket_insert_nome);
        TextView custo = rowView.findViewById(R.id.subminimarket_insert_preco);

        classeMarket market = markets.get(position);

        if(market.getNome().toLowerCase().indexOf(ingrediente.getNome().toLowerCase()) > 0) nome.setText(Utils.limit(market.getNome().substring(market.getNome().toLowerCase().indexOf(ingrediente.getNome().toLowerCase())), 15));
        else nome.setText(market.getNome());

        custo.setText(market.getValor());

        rowView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Deseja ser encaminhado para o site?")
                    .setTitle("Saborear")
                    .setNegativeButton("Cancelar", (dialog, id) -> {

                    })
                    .setPositiveButton("Sim", (dialog, id) -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(market.getURL()));
                        context.startActivity(intent);
                    });
            builder.create().show();
        });

        return rowView;
    }
}