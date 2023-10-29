package Saborear.com.br.Classes;

import android.util.Log;

import java.util.Map;

public class classeCategoriaReceita {
    private String idcatreceita, nome;

    public classeCategoriaReceita(String idcatreceita, String nome) {
        this.idcatreceita = idcatreceita;
        this.nome = nome;
    }

    public classeCategoriaReceita(Map<String, String> map) {
        this.idcatreceita = map.get("id_categ_receita");
        this.nome = map.get("nome_categ_receita");
    }

    public void show() {
        Log.i("SaborearDatabase", "ID Categoria Receita: " + idcatreceita);
        Log.i("SaborearDatabase", "Nome: " + nome);
    }


    public String getNome() { return nome; }
    public String getIdcatreceita() { return idcatreceita; }

    public void setNome(String nome) { this.nome = nome; }
    public void setIdcatingrediente(String idcatreceita) { this.idcatreceita = idcatreceita; }
}