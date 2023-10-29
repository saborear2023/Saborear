package Saborear.com.br.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import Saborear.com.br.Classes.classeIngrediente;
import Saborear.com.br.Classes.classeMarket;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.R;
import Saborear.com.br.Utils.Utils;

public class MarketView extends ArrayAdapter<classeIngrediente> {

    private final Database db;
    private final Activity context;
    private ArrayList<classeIngrediente> ingredientes;
    private ArrayList<ArrayList<classeMarket>> markets;
    public MarketView(Activity context, ArrayList<classeIngrediente> ingredientes, ArrayList<ArrayList<classeMarket>> markets) {
        super(context, R.layout.subreceita, ingredientes);

        this.db = new Database(context);
        this.context = context;
        this.ingredientes = ingredientes;
        this.markets = markets;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater;
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView;

        inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.submarket, null, true);

        TextView nome = rowView.findViewById(R.id.submarket_insert_nome);
        TextView quantidade = rowView.findViewById(R.id.submarket_insert_quantidade);
        TextView referencia = rowView.findViewById(R.id.submarket_insert_referencia);

        ListView listview = rowView.findViewById(R.id.submarket_listview);

        ArrayList<classeMarket> market = markets.get(position);
        classeIngrediente ingrediente = ingredientes.get(position);

        nome.setText(ingrediente.getNome());
        quantidade.setText(ingrediente.getQuantidadeString()+" "+ingrediente.getMedida());
        referencia.setText("Pão de Açúcar");

        MiniMarketView adapter = new MiniMarketView(context, ingrediente, market);
        listview.setAdapter(adapter);

        ViewGroup.LayoutParams layoutParams = listview.getLayoutParams();
        layoutParams.height = Utils.intToDp(40*market.size()+16*(market.size()));
        listview.setLayoutParams(layoutParams);

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        inverted.add(listview);

        Utils.blackMode((ViewGroup) rowView, ignore, inverted);

        if(InternalDatabase.isDarkmode()) {
            int corDivisor = Color.parseColor("#7F7F7F");
            ColorDrawable divisorDrawable = new ColorDrawable(corDivisor);
            listview.setDivider(divisorDrawable);
            listview.setDividerHeight(Utils.intToDp(16));
        }


        return rowView;
    }
}