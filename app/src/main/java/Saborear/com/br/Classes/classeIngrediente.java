package Saborear.com.br.Classes;

import android.util.Log;

import java.util.Map;

public class classeIngrediente {
    private String indingrediente, nome, medida;
    private double quantidade;
    private classeCategoriaIngrediente catingrediente;

    public classeIngrediente(String idingrediente, classeCategoriaIngrediente catingrediente, String nome, String quantidade) {
        this.indingrediente = idingrediente;
        this.catingrediente = catingrediente;
        this.nome = nome;

        String[] partes = quantidade.split(" ", 2);
        this.quantidade = Double.parseDouble(partes[0]);
    this.medida = partes.length > 1 ? partes[1] : "";

    }

    public classeIngrediente(Map<String, String> map) {
        this.indingrediente = map.get("id_ingrediente");
        this.catingrediente = new classeCategoriaIngrediente(map);
        this.nome = map.get("nome_ingrediente");

        String[] partes = map.get("quantidade").split(" ", 2);
        this.quantidade = Double.parseDouble(partes[0]);
        this.medida = partes.length > 1 ? partes[1] : "";
    }

    public classeIngrediente(classeIngrediente ingrediente) {
        this.indingrediente = ingrediente.getIndingrediente();
        this.catingrediente = ingrediente.getCatingrediente();
        this.nome = ingrediente.getNome();
        this.quantidade = ingrediente.getQuantidade();
        this.medida = ingrediente.getMedida();
    }

    public String listar() {
        String aux = "";
        if(getMedida().equals("xícara")) aux = " ("+(Math.round(240*getQuantidade()))+"ml)";
        if(getMedida().equals("colher de sopa")) aux = " ("+(Math.round(15*getQuantidade()))+"ml)";
        if(getMedida().equals("colher de sobremesa")) aux = " ("+(Math.round(10*getQuantidade()))+"ml)";
        if(getMedida().equals("lata")) aux = " ("+(Math.round(350*getQuantidade()))+"ml)";

        return getQuantidadeString()+" "+getMedida()+" de " + getNome().toLowerCase()+aux;
    }

    public String addScript(String idreceita) {
        String sql = "insert into receita_ingred(id_ingrediente, id_receita, quantidade) values('@id', '@receita', '@quantidade');";
        sql = sql.replace("@id", indingrediente);
        sql = sql.replace("@receita", idreceita);
        sql = sql.replace("@quantidade", getQuantidadeString()+" "+medida);
        return sql;
    }

    public void show() {
        Log.i("SaborearDatabase", "ID Ingrediente: " + indingrediente + ", Nome: " + nome + ", Quantidade: " + quantidade + ", Categoria Ingrediente: " + catingrediente.getNome());
    }

    public String getNome() { return nome; }
    public String getIndingrediente() { return indingrediente; }
    public classeCategoriaIngrediente getCatingrediente() { return catingrediente; }
    public String getMedida() { return medida; }
    public double getQuantidade() { return (quantidade); }
    public String getQuantidadeString() { return String.valueOf(quantidade).replace(".0", ""); }
    public void setQuantidade(double quantidade) { this.quantidade = quantidade; }
    public void setMedida(String medida) { this.medida = medida; }

    public void setCatingrediente(classeCategoriaIngrediente catingrediente) { this.catingrediente = catingrediente; }
    public void setIndingrediente(String indingrediente) { this.indingrediente = indingrediente; }

    public void setNome(String nome) { this.nome = nome; }
}
