package Saborear.com.br.Classes;

import java.util.Map;

public class classeCategoriaIngrediente {
    private String idcatingrediente, nome, sql;

    public classeCategoriaIngrediente(String idcatingrediente, String nome) {
        this.idcatingrediente = idcatingrediente;
        this.nome = nome;
    }

    public classeCategoriaIngrediente(Map<String, String> map) {
        this.idcatingrediente = map.get("id_cat_ingrediente");
        this.nome = map.get("nome_cat_ingrediente");
    }

    public void show() {
        System.out.println("ID Categoria Ingrediente: " + idcatingrediente);
        System.out.println("Nome: " + nome);
    }


    public String getNome() { return nome; }
    public String getIdcatingrediente() { return idcatingrediente; }

    public void setNome(String nome) { this.nome = nome; }
    public void setIdcatingrediente(String idcatingrediente) { this.idcatingrediente = idcatingrediente; }
}
