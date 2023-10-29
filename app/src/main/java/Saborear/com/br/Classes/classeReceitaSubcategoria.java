package Saborear.com.br.Classes;

import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

public class classeReceitaSubcategoria {

    private ArrayList<String> subcategorias;

    public classeReceitaSubcategoria(ArrayList<String> subcategorias) {
        setSubcategorias(subcategorias);
    }
    public classeReceitaSubcategoria(Map<String, String> map) {
        try {
            subcategorias = new ArrayList<>();
            for (String s : map.get("subcategorias").split("//")) subcategorias.add(s);
        } catch(Exception e) {
            Log.i("SaborearDatabase", "Erro na criação de receitasubcategoria: "+e.getMessage());
        }
    }

    public boolean contains(String subcategoria) { return subcategorias.contains(subcategoria); }

    public String getNomes() {
        String result = "";
        for (int i = 0; i < subcategorias.size(); i++) {
            result += subcategorias.get(i);
            if(i + 1 < subcategorias.size()) result += ", ";
        }
        return result;
    }

    public void setSubcategorias(ArrayList<String> subcategorias) {
        if(subcategorias == null) this.subcategorias = new ArrayList<>();
        else this.subcategorias = subcategorias;
    }

    public ArrayList<String> getSubcategorias() { return subcategorias; }
}
