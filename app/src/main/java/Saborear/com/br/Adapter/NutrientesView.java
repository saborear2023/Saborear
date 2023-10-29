package Saborear.com.br.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import Saborear.com.br.Classes.classeNutricional;
import Saborear.com.br.Database.Database;
import Saborear.com.br.R;
import Saborear.com.br.Utils.Utils;

public class NutrientesView extends ArrayAdapter<classeNutricional> {

    private final Activity context;
    private final ArrayList<classeNutricional> nutrientes;
    private final Database db;

    public NutrientesView(Activity context, ArrayList<classeNutricional> nutrientes) {
        super(context, R.layout.subreceita, nutrientes);

        db = new Database(context);
        this.context = context;
        this.nutrientes = nutrientes;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater;
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView;

        inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.subnutricional, null, true);

        TextView calcio = rowView.findViewById(R.id.subnutricional_insert_calcio);
        TextView calorias = rowView.findViewById(R.id.subnutricional_insert_calorias);
        TextView acucar = rowView.findViewById(R.id.subnutricional_insert_acucar);
        TextView ferro = rowView.findViewById(R.id.subnutricional_insert_ferro);
        TextView fibra = rowView.findViewById(R.id.subnutricional_insert_fibra);
        TextView gordurasaturada = rowView.findViewById(R.id.subnutricional_insert_gordurasaturada);
        TextView gorduratrans = rowView.findViewById(R.id.subnutricional_insert_gorduratrans);
        TextView peso = rowView.findViewById(R.id.subnutricional_insert_peso);
        TextView vitaminad = rowView.findViewById(R.id.subnutricional_insert_vitaminad);
        TextView potassio = rowView.findViewById(R.id.subnutricional_insert_potassio);

        TextView gorduras = rowView.findViewById(R.id.subnutricional_titulo_gorduras);
        TextView proteinas = rowView.findViewById(R.id.subnutricional_titulo_proteinas);
        TextView calorico = rowView.findViewById(R.id.subnutricional_titulo_calorico);
        TextView carboidratos = rowView.findViewById(R.id.subnutricional_titulo_carboidratos);
        TextView alerta = rowView.findViewById(R.id.subnutricional_titulo_alerta);
        ImageView sem = rowView.findViewById(R.id.subnutricional_sem);

       classeNutricional nutricional = nutrientes.get(position);

       Boolean empty = true;
       if(nutricional.getSaturadaQuantidade() > 0) empty = false;
       if(nutricional.getTransQuantidade() > 0) empty = false;
       if(nutricional.getFibraQuantidade() > 0) empty = false;
       if(nutricional.getAcucarQuantidade() > 0) empty = false;
       if(nutricional.getVitaminaDQuantidade() > 0) empty = false;
       if(nutricional.getCalcioQuantidade() > 0) empty = false;
       if(nutricional.getFerroQuantidade() > 0) empty = false;
       if(nutricional.getPotassioQuantidade() > 0) empty = false;
       if(nutricional.getCaloriasQuantidade() > 0) empty = false;
       if(nutricional.getPesoQuantidade() > 0) empty = false;

       if(empty) {
           gorduras.setVisibility(View.INVISIBLE);
           proteinas.setVisibility(View.INVISIBLE);
           calorico.setVisibility(View.INVISIBLE);
           carboidratos.setVisibility(View.INVISIBLE);
           alerta.setVisibility(View.INVISIBLE);
           sem.setVisibility(View.VISIBLE);

           return rowView;
       }

       calcio.setText("Cálcio "+nutricional.getCalcioQuantidade()+"mg");
       calorias.setText(Math.round(nutricional.getCaloriasQuantidade())+"kcal");
       acucar.setText("Açúcar "+nutricional.getAcucarQuantidade()+"g");
       ferro.setText("Ferro "+nutricional.getFerroQuantidade()+"mg");
       fibra.setText("Fibra "+nutricional.getFibraQuantidade()+"g");
       gordurasaturada.setText("Saturada "+nutricional.getSaturadaQuantidade()+"g");
       gorduratrans.setText("Trans "+nutricional.getTransQuantidade()+"g");
       peso.setText(nutricional.getPesoQuantidade()+"g");
       vitaminad.setText("Vitam. D "+nutricional.getVitaminaDQuantidade()+"µg");
       potassio.setText("Potássio "+nutricional.getPotassioQuantidade()+"mg");

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        Utils.blackMode((ViewGroup) rowView, ignore, inverted);

        return rowView;
    }
}
