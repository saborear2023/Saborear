package Saborear.com.br.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.PesquisaIngrediente;
import Saborear.com.br.R;

public class ShortIngredienteView extends ArrayAdapter<String> {

    private final Database db;
    private final Activity context;
    private  ArrayList<String> ingredientes, nomes;
    private Map<Integer, String> selecionados;
    public ShortIngredienteView(Activity context, ArrayList<String> ingredientes) {
        super(context, R.layout.subreceita, ingredientes);

        this.db = new Database(context);
        this.context = context;
        this.ingredientes = ingredientes;

        this.nomes = (ArrayList<String>) StorageDatabase.nomes.clone();
        this.nomes.add(0, "Clique para selecionar");

        this.selecionados = new HashMap<>();
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater;
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView;

        inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.subshortingrediente, null, true);

        Spinner spinner = rowView.findViewById(R.id.subshortingrediente_ingrediente);
        ImageButton add = rowView.findViewById(R.id.subshortingrediente_btn_add);
        ImageButton del = rowView.findViewById(R.id.subshortingrediente_btn_remover);

        if(position + 1 == ingredientes.size()) add.setVisibility(View.VISIBLE);

        add.setOnClickListener(v -> PesquisaIngrediente.addIngrediente());
        del.setOnClickListener(v -> {
            if(ingredientes.size() == 1) spinner.setSelection(0);
            else PesquisaIngrediente.delIngrediente(position);
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, nomes);
        spinner.setAdapter(adapter);

        String nome = ingredientes.get(position);

        if(nomes.indexOf(nome) != -1)
            spinner.setSelection(nomes.indexOf(nome));
        else
            spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selecionados.put(position, nomes.get(i));
                PesquisaIngrediente.ingredientes.set(position, nomes.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        return rowView;
    }
}