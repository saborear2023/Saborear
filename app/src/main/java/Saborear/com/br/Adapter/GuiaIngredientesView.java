package Saborear.com.br.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import Saborear.com.br.Classes.classeNutricional;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.R;
import Saborear.com.br.Manage.ManageReceita;
import Saborear.com.br.Utils.Utils;

public class GuiaIngredientesView extends ArrayAdapter<classeNutricional> {

    private final Activity context;
    private final ArrayList<classeNutricional> nutrientes;
    private final Database db;

    public GuiaIngredientesView(Activity context, ArrayList<classeNutricional> nutrientes) {
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
        rowView = inflater.inflate(R.layout.subguiaingredientes, null, true);

        TextView nome = rowView.findViewById(R.id.subguiaingredientes_nome);
        ListView listview = rowView.findViewById(R.id.subguiaingredientes_nutricional);
        ViewPager receitas = rowView.findViewById(R.id.subguiaingredientes_receitas);
        ImageView sem = rowView.findViewById(R.id.subguiaingredientes_sem);

        classeNutricional nutriente = nutrientes.get(position);

        nome.setText(nutriente.getNome());

        ArrayList<classeNutricional> nutrientes = new ArrayList<>(); nutrientes.add(nutriente);
        NutrientesGuiaView adapterView = new NutrientesGuiaView(context, nutrientes);
        listview.setAdapter(adapterView);

        ManageReceita mg = new ManageReceita(StorageDatabase.receitas);
        if(mg.getByIngrediente(nutriente.getNome()).getReceitas().size() > 0) {
            MinireceitaGuiaView adapterReceitas = new MinireceitaGuiaView(context, mg.getByIngrediente(nutriente.getNome()).getReceitas());
            receitas.setAdapter(adapterReceitas);
        } else sem.setVisibility(View.VISIBLE);

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        ignore.add(listview); ignore.add(receitas);
        Utils.blackMode((ViewGroup) rowView, ignore, inverted);

        return rowView;
    }

}
