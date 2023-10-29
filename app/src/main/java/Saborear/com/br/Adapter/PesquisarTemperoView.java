package Saborear.com.br.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import Saborear.com.br.Classes.classeTempero;
import Saborear.com.br.Database.Database;
import Saborear.com.br.R;
import Saborear.com.br.Tempero;
import Saborear.com.br.Utils.Utils;

public class PesquisarTemperoView extends ArrayAdapter<classeTempero> {

    private final Database db;
    private final Activity context;
    private ArrayList<classeTempero> temperos;

    public PesquisarTemperoView(Activity context, ArrayList<classeTempero> temperos) {
        super(context, R.layout.subreceita, temperos);

        this.db = new Database(context);
        this.context = context;
        this.temperos = temperos;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView;

        classeTempero temp = temperos.get(position);

        TextView nome;
        if(temp.getIdtempero().equals("-0")) {
            rowView = inflater.inflate(R.layout.subdivisor, null, true);
            nome = rowView.findViewById(R.id.subdivisor_titulo);
        } else {
            rowView = inflater.inflate(R.layout.subpesquisartempero, null, true);
            nome = rowView.findViewById(R.id.subpesquisartempero_nome);

            rowView.setOnClickListener(v -> {
                Tempero.tempero = new classeTempero(temp);
                Intent myIntent = new Intent(context, Tempero.class);
                context.startActivityForResult(myIntent, 1);
                context.overridePendingTransition(0, 0);
            });
        }
        nome.setText(temp.getNome());

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        Utils.blackMode((ViewGroup) rowView, ignore, inverted);

        return rowView;
    }
}