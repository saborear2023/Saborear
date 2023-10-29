package Saborear.com.br.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import Saborear.com.br.CadastrarReceita;
import Saborear.com.br.Classes.classeIngrediente;
import Saborear.com.br.R;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.Utils;

public class IngredienteView extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> id, receitas, medidas;
    private final ArrayList<classeIngrediente> ingredientes;
    private final Database db;

    public IngredienteView(Activity context, ArrayList<String> id, ArrayList<String> receitas, ArrayList<String> medidas, ArrayList<classeIngrediente> ingredientes) {
        super(context, R.layout.subingrediente, id);

        db = new Database(context);
        this.context = context;
        this.id = id;
        this.receitas = (ArrayList<String>) receitas.clone();
        this.medidas = medidas;
        this.ingredientes = (ArrayList<classeIngrediente>) ingredientes.clone();

        this.receitas.add(0, "Clique aqui");
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView = inflater.inflate(R.layout.subingrediente, null, true);

        TextView qnt = rowView.findViewById(R.id.subingrediente_quantidade);
        ImageButton add = rowView.findViewById(R.id.subingrediente_add);
        ImageButton del = rowView.findViewById(R.id.subingrediente_del);
        ImageButton edit = rowView.findViewById(R.id.subingrediente_edit);
        Spinner form = rowView.findViewById(R.id.subingrediente_formato);
        Spinner receit = rowView.findViewById(R.id.subingrediente_ingrediente);


        edit.setOnClickListener(v -> {
            id.remove(position);
            ingredientes.remove(position);
            CadastrarReceita.atualizar();
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, medidas);
        form.setAdapter(adapter);

        ArrayAdapter<String> nadapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, receitas);
        receit.setAdapter(nadapter);

        add.setOnClickListener(v -> {
            try {
                double value = Double.parseDouble(qnt.getText().toString());
                qnt.setText((""+(value+1)).replace(".0", ""));
            } catch (Exception e) {
                qnt.setText("0");
                Log.i("SaborearDatabase", e.getMessage());
            }
        });

        del.setOnClickListener(v -> {
            try {
                double value = Double.parseDouble(qnt.getText().toString());
                if(value-1 > 0)
                    qnt.setText((""+(value-1)).replace(".0", ""));
                else
                    qnt.setText("0");
            } catch (Exception e) {
                qnt.setText("0");
            }
        });

        classeIngrediente ingrediente = ingredientes.get(position);

        qnt.setText(ingrediente.getQuantidadeString());

        if(medidas.indexOf(ingrediente.getMedida()) != -1)
            form.setSelection(medidas.indexOf(ingrediente.getMedida()));
        else
            form.setSelection(0);

        receit.setSelection(receitas.indexOf(ingrediente.getNome()));

        qnt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(qnt.getText().toString().length() == 0) return;
                try {
                    ingrediente.setQuantidade(Double.parseDouble(qnt.getText().toString()));
                } catch (Exception e) {
                    Log.i("SaborearDatabase", e.getMessage());
                }
            }
        });

        form.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ingrediente.setMedida(form.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        receit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ingrediente.setNome(receit.getSelectedItem().toString());
                ingrediente.setIndingrediente(StorageDatabase.idingrediente.get(receit.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        ignore.add(add); ignore.add(del); ignore.add(qnt);
        ignore.add(rowView.findViewById(R.id.subingrediente_icon_lanche));
        Utils.blackMode((ViewGroup) rowView, ignore, inverted);

        return rowView;
    }

    public ArrayList<String> getId() { return id; }

    public ArrayList<classeIngrediente> getIngredientes() { return ingredientes; }
}