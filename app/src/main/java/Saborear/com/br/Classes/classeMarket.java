package Saborear.com.br.Classes;

import android.util.Log;

public class classeMarket {
    String id, nome, valor;
    public classeMarket(String id, String nome, String valor) {
        setId(id);
        setNome(nome);
        setValor(valor);
    }
    public classeMarket() {
        setId("");
        setNome("");
        setValor("");
    }

    public void show() {
        Log.i("SaborearDatabase", "ID: "+getId());
        Log.i("SaborearDatabase", "Nome: "+getNome());
        Log.i("SaborearDatabase", "Valor: "+getValor());
        Log.i("SaborearDatabase", "URL: "+getURL());
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
    public double getCusto() {
        if(getValor().equals("Sem estoque")) return 0;
        return Double.parseDouble(getValor().replace("R$ ", "").replace(",", "."));
    };

    public String getURL() {
        return "https://www.paodeacucar.com/produto/@id/".replace("@id", id);
    }

    public String getNome() {
        return nome;
    }

    public String getId() {
        return id;
    }
    public String getValor() {
        return valor;
    }
}
